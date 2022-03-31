package com.virnect.download.application.download;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import net.dongliu.apk.parser.bean.ApkMeta;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.download.application.workspace.WorkspaceRestService;
import com.virnect.download.dao.AppRepository;
import com.virnect.download.dao.DeviceRepository;
import com.virnect.download.dao.OSRepository;
import com.virnect.download.dao.ProductRepository;
import com.virnect.download.domain.App;
import com.virnect.download.domain.Device;
import com.virnect.download.domain.OS;
import com.virnect.download.domain.Product;
import com.virnect.download.dto.request.AdminAppUploadRequest;
import com.virnect.download.dto.response.AdminAppDeleteResponse;
import com.virnect.download.dto.response.AdminAppUploadResponse;
import com.virnect.download.dto.response.WorkspaceInfoListResponse;
import com.virnect.download.dto.response.WorkspaceInfoResponse;
import com.virnect.download.exception.AppServiceException;
import com.virnect.download.exception.AppServiceException;
import com.virnect.download.global.common.ApiResponse;
import com.virnect.download.global.common.ResponseMapper;
import com.virnect.download.global.error.ErrorCode;
import com.virnect.download.infra.file.upload.FileUploadService;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {
	private final ApkService apkService;
	private final AppRepository appRepository;
	private final FileUploadService fileUploadService;
	private final ProductRepository productRepository;
	private final DeviceRepository deviceRepository;
	private final WorkspaceRestService workspaceRestService;
	private final ResponseMapper responseMapper;
	private final OSRepository osRepository;

	@Value("#{'${file.allowed-extension}'.split(',')}")
	private List<String> extensionWhiteList;
	@Value("#{'${file.allowed-mime-type}'.split(',')}")
	private List<String> mimeTypeWhiteList;

	private static final String PRODUCT_REMOTE = "REMOTE";
	private static final String EXTENSION_APK = "apk";
	private static final String SEPARATOR_DOT = ".";
	private static final String SEPARATOR_UNDER_BAR = "_";
	private static final String REGEXP_FILE_NAME_CHECK = "^([a-z]+_[a-z]+_[0-9]{4,7}\\.[a-zA-Z]+)$";

	@Transactional
	public AdminAppUploadResponse uploadApplication(
		AdminAppUploadRequest adminAppUploadRequest, String userUUID
	) {
		MultipartFile file = adminAppUploadRequest.getUploadAppFile();

		if (file.getSize() <= 0) {
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_EMPTY_APPLICATION_FILE);
		}

		if (StringUtils.isEmpty(file.getOriginalFilename())) {
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_INVALID_FILE_NAME);
		}

		String extension = getExtension(file.getOriginalFilename());
		if (!extensionWhiteList.contains(extension)) {
			log.error("[ADMIN_APP_UPLOAD] File extension not support. extension : {}", extension);
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FILE_EXTENSION_NOT_SUPPORT);
		}

		if (!mimeTypeWhiteList.contains(file.getContentType())) {
			log.error("[ADMIN_APP_UPLOAD] File extension not support. file content-type : {}", file.getContentType());
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FILE_EXTENSION_NOT_SUPPORT);
		}

		masterUserValidationCheck(userUUID);

		Product product = getProduct(adminAppUploadRequest.getProductName());
		OS os = getOS(adminAppUploadRequest.getOperationSystem());
		Device device = getDevice(adminAppUploadRequest.getDeviceType(), adminAppUploadRequest.getDeviceModel(),
			adminAppUploadRequest.getProductName()
		);

		fileNameValidationCheck(
			file.getOriginalFilename(), adminAppUploadRequest.getProductName(), adminAppUploadRequest.getDeviceType());

		String versionCode = getVersionCodeByFileName(file.getOriginalFilename());
		log.info("Version Code Check. >> {}", versionCode);

		String versionName;
		if (product.getName().equals(PRODUCT_REMOTE)) {
			if (device.getType().equals("HOLOLENS")) {
				versionName = getVersionName(versionCode, 1, 1, 1, 1);
			} else {
				versionName = getVersionName(versionCode, 1, 1, 2, 3);
			}
		} else {
			versionName = getVersionName(versionCode, 1, 2, 2, 2);
		}
		log.info("Version Name Check. >> {}", versionName);

		newAppVersionCodeValidation(device, os, Long.parseLong(versionCode));

		String uploadedUrl = fileUploadService.upload(adminAppUploadRequest.getUploadAppFile());

		App app = App.appByAdminBuilder()
			.uuid(generateAppUUID())
			.device(device)
			.product(product)
			.os(os)
			.versionName(versionName)
			.versionCode(Long.parseLong(versionCode))
			.appUrl(uploadedUrl)
			.signature(adminAppUploadRequest.getSigningKey())
			.build();

		if (product.getName().equals(PRODUCT_REMOTE) && extension.equals(EXTENSION_APK)) {
			ApkMeta apkMeta = apkService.parsingAppInfo(file);
			app.setPackageName(apkMeta.getPackageName());
			appRepository.getLatestVersionActiveAppInfoByPackageName(apkMeta.getPackageName())
				.ifPresent(latestApp -> app.setSignature(latestApp.getSignature()));
		}

		appRepository.save(app);
		return responseMapper.appToAdminAppUploadResponse(app);
	}

	private String getExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf(SEPARATOR_DOT);
		if (dotIndex == -1) {
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_INVALID_FILE_NAME);
		}

		return fileName.substring(dotIndex + 1);
	}

	// {product}_{device_type}_{version_code}.{extension}
	private String getVersionCodeByFileName(String fileName) {
		String[] productAndDeviceAndVersionAndExtension = StringUtils.delimitedListToStringArray(
			fileName, SEPARATOR_UNDER_BAR);

		String versionAndExtension = productAndDeviceAndVersionAndExtension[2];

		if (StringUtils.isEmpty(versionAndExtension)) {
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_INVALID_FILE_NAME);
		}

		int dotIndex = versionAndExtension.lastIndexOf(SEPARATOR_DOT);
		if (dotIndex == -1) {
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_INVALID_FILE_NAME);
		}
		return versionAndExtension.substring(0, dotIndex);
	}

	// {product}_{device_type}_{version_code}.{extension}
	private void fileNameValidationCheck(String fileName, String requestProduct, String requestDevice) {
		log.info("File Name Check. >> {}", fileName);
		boolean fileNameMatch = Pattern.matches(REGEXP_FILE_NAME_CHECK, fileName);
		log.info("Regexp Check. >> {}", fileNameMatch);
		if (!fileNameMatch) {
			log.error("not matched file name regex.");
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_INVALID_FILE_NAME);
		}

		String[] productAndDeviceAndVersionAndExtension = StringUtils.delimitedListToStringArray(
			fileName, SEPARATOR_UNDER_BAR);

		String product = productAndDeviceAndVersionAndExtension[0];
		log.info("Product Check. >> {}", product);

		if (StringUtils.isEmpty(product) || !requestProduct.equalsIgnoreCase(product)) {
			log.error("not matched file name and request product. product by request : {}, product by file name : {}",
				requestProduct, product
			);
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_INVALID_FILE_NAME);
		}

		String device = productAndDeviceAndVersionAndExtension[1];
		log.info("Device Check. >> {}", device);

		if (StringUtils.isEmpty(device) || !requestDevice.equalsIgnoreCase(device)) {
			log.error("not matched file name and request device. device by request : {}, device by file name : {}",
				requestDevice, device
			);
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_INVALID_FILE_NAME);
		}
	}

	//version_code => major.minor.patch.optional
	private String getVersionName(
		String versionCode, int majorDigit, int minorDigit, int patchDigit, int optionalDigit
	) {
		long major = Long.parseLong(versionCode.substring(0, majorDigit));
		long minor = Long.parseLong(versionCode.substring(majorDigit, majorDigit + minorDigit));
		long patch = Long.parseLong(
			versionCode.substring(majorDigit + minorDigit, majorDigit + minorDigit + patchDigit));
		long optional = Long.parseLong(versionCode.substring(
			majorDigit + minorDigit + patchDigit,
			majorDigit + minorDigit + patchDigit + optionalDigit
		));

		if (optional == 0L) {
			return major + SEPARATOR_DOT + minor + SEPARATOR_DOT + patch;
		}
		return major + SEPARATOR_DOT + minor + SEPARATOR_DOT + patch + SEPARATOR_DOT + optional;

	}

	private void masterUserValidationCheck(String userUUID) {
		log.info("[REST - WORKSPACE SERVER][GET MY WORKSPACE LIST] request user uuid : {}", userUUID);
		ApiResponse<WorkspaceInfoListResponse> apiResponse = workspaceRestService.getMyWorkspaceInfoList(userUUID);
		if (apiResponse.getCode() != 200 || CollectionUtils.isEmpty(apiResponse.getData().getWorkspaceList())) {
			log.error("[REST - WORKSPACE SERVER][GET MY WORKSPACE LIST] response code : {}, response message : {}",
				apiResponse.getCode(), apiResponse.getMessage()
			);
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_WORKSPACE_INVALID_PERMISSION);
		}
		List<WorkspaceInfoResponse> workspaceList = apiResponse.getData().getWorkspaceList();
		if (workspaceList.stream().noneMatch(workspaceInfo -> workspaceInfo.getRole().equals("MASTER"))) {
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_WORKSPACE_INVALID_PERMISSION);
		}
	}

	private Product getProduct(String productName) {
		return productRepository.findByName(productName.toUpperCase())
			.orElseThrow(() -> new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_PRODUCT_INFO_NOT_FOUND));
	}

	private Device getDevice(
		String deviceType, String deviceModel, String productName
	) {
		return deviceRepository.findByTypeAndModelAndProduct_Name(
				deviceType.toUpperCase(), deviceModel.toUpperCase(), productName.toUpperCase())
			.orElseThrow(() -> new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_DEVICE_INFO_NOT_FOUND));
	}

	private void newAppVersionCodeValidation(Device device, OS os, long versionCode) {
		// 버전 코드 중복 체크 및 중복 시 예외 발생
		if (appRepository.existAppVersionCode(device, os, versionCode)) {
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_DUPLICATE_VERSION);
		}
		// 현재 앱 버전 코드가 기존 최신 버전 보다 낮은 버전 코드의 경우 예외 발생
		Long latestVersion = appRepository.getLatestVersionByDeviceAndOs(device, os);
		if (latestVersion != null && latestVersion > 0L) {
			if (latestVersion > versionCode) {
				throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_VERSION_IS_LOWER);
			}
		}
	}

	public AdminAppDeleteResponse deleteApplication(String appUUID, String userUUID) {
		App app = appRepository.findByUuid(appUUID)
			.orElseThrow(() -> new AppServiceException(ErrorCode.ERR_APP_INFO_NOT_FOUND));

		masterUserValidationCheck(userUUID);

		fileUploadService.delete(app.getAppUrl());

		appRepository.delete(app);

		return new AdminAppDeleteResponse(true, LocalDateTime.now());
	}

	private String generateAppUUID() {
		String[] tokens = UUID.randomUUID().toString().split("-");
		return tokens[1] + "-" + tokens[4];
	}

	private OS getOS(String operationSystem) {
		return osRepository.findByName(operationSystem.toUpperCase())
			.orElseThrow(() -> new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_OS_INFO_NOT_FOUND));
	}

}
