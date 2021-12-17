package com.virnect.download.application.download;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import net.dongliu.apk.parser.ByteArrayApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.download.application.workspace.WorkspaceRestService;
import com.virnect.download.dao.AppRepository;
import com.virnect.download.dao.DeviceRepository;
import com.virnect.download.dao.OSRepository;
import com.virnect.download.dao.ProductRepository;
import com.virnect.download.domain.App;
import com.virnect.download.domain.AppGuideUrl;
import com.virnect.download.domain.AppImageUrl;
import com.virnect.download.domain.AppStatus;
import com.virnect.download.domain.AppUpdateStatus;
import com.virnect.download.domain.Device;
import com.virnect.download.domain.OS;
import com.virnect.download.domain.Product;
import com.virnect.download.dto.request.AdminAppUploadRequest;
import com.virnect.download.dto.request.AppInfoUpdateRequest;
import com.virnect.download.dto.request.AppSigningKeyRegisterRequest;
import com.virnect.download.dto.request.AppUploadRequest;
import com.virnect.download.dto.response.AdminAppUploadResponse;
import com.virnect.download.dto.response.AppDetailInfoResponse;
import com.virnect.download.dto.response.AppSigningKetRegisterResponse;
import com.virnect.download.dto.response.AppUploadResponse;
import com.virnect.download.dto.response.AppVersionInfoListResponse;
import com.virnect.download.dto.response.AppVersionInfoResponse;
import com.virnect.download.dto.response.SignedAppInfoResponse;
import com.virnect.download.dto.response.WorkspaceInfoListResponse;
import com.virnect.download.dto.response.WorkspaceInfoResponse;
import com.virnect.download.exception.AppServiceException;
import com.virnect.download.global.common.ApiResponse;
import com.virnect.download.global.common.ResponseMapper;
import com.virnect.download.global.error.ErrorCode;
import com.virnect.download.infra.file.upload.FileUploadService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppService {
	private final AppRepository appRepository;
	private final DeviceRepository deviceRepository;
	private final ProductRepository productRepository;
	private final OSRepository osRepository;
	private final FileUploadService fileUploadService;
	private final ResponseMapper responseMapper;
	private final WorkspaceRestService workspaceRestService;
	private final EntityManager entityManager;

	@Transactional
	public ApiResponse<AppUploadResponse> applicationUploadAndRegister(AppUploadRequest appUploadRequest) {
		// 1. APK 파일 파싱
		ApkMeta apkMeta = parsingAppInfo(appUploadRequest.getUploadAppFile());
		String applicationSignature = null;

		Optional<App> previousAppInfo = this.appRepository.getLatestVersionActiveAppInfoByPackageName(
			apkMeta.getPackageName());

		if (previousAppInfo.isPresent()) {
			newAppVersionCodeValidation(apkMeta.getPackageName(), apkMeta.getVersionCode());
			applicationSignature = previousAppInfo.get().getSignature();
		}

		// 1. find app device type, device, os information
		Device device = getDeviceInfoByDeviceTypeAndProductName(
			appUploadRequest.getDeviceType(), appUploadRequest.getProductName());
		OS os = getOS(appUploadRequest.getOperationSystem());
		Product product = productRepository.findByName(appUploadRequest.getProductName())
			.orElseThrow(() -> new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_PRODUCT_INFO_NOT_FOUND));

		// 2. application artifact upload
		String appUploadUrl = fileUploadService.upload(appUploadRequest.getUploadAppFile());

		App apps = App.builder()
			.uuid(generateAppUUID())
			.device(device)
			.product(product)
			.os(os)
			.packageName(apkMeta.getPackageName())
			.versionName(apkMeta.getVersionName())
			.versionCode(apkMeta.getVersionCode())
			//.appOriginUrl(appUploadUrl)
			.appUrl(appUploadUrl)
			.imageUrl("")
			.signature(applicationSignature)
			.appStatus(AppStatus.INACTIVE)
			.appUpdateStatus(AppUpdateStatus.OPTIONAL)
			.build();

		// Remote 가이드 문서 및 이미지 설정
		if (product.getName().equals("REMOTE") && device.getType().equals("MOBILE")) {
			apps.setGuideUrl(AppGuideUrl.REMOTE_USER_GUIDE.getUrl());
			apps.setImageUrl(AppImageUrl.REMOTE_MOBILE.getUrl());
		} else if (product.getName().equals("REMOTE") && device.getType().equals("REALWEAR")) {
			apps.setGuideUrl(AppGuideUrl.REMOTE_USER_GUIDE.getUrl());
			apps.setImageUrl(AppImageUrl.REMOTE_REALWEAR.getUrl());
		} else if (product.getName().equals("REMOTE") && device.getType().equals("LINKFLOW")) {
			apps.setGuideUrl(AppGuideUrl.REMOTE_LINKFLOW_USER_GUIDE.getUrl());
			apps.setImageUrl(AppImageUrl.REMOTE_LINKFLOW.getUrl());
		}

		//VIEW 가이드 문서 및 이미지 설정
		if (product.getName().equals("VIEW") && device.getType().equals("MOBILE")) {
			apps.setGuideUrl(AppGuideUrl.VIEW_MOBILE_USER_GUIDE.getUrl());
			apps.setImageUrl(AppImageUrl.VIEW_MOBILE.getUrl());
		} else if (product.getName().equals("VIEW") && device.getType().equals("REALWEAR")) {
			apps.setGuideUrl(AppGuideUrl.VIEW_REALWEAR_USER_GUIDE.getUrl());
			apps.setImageUrl(AppImageUrl.VIEW_REALWEAR.getUrl());
		} else if (product.getName().equals("MAKE")) {
			apps.setGuideUrl(AppGuideUrl.MAKE_USER_GUIDE.getUrl());
			apps.setImageUrl(AppImageUrl.MAKE.getUrl());
		}
		appRepository.save(apps);

		AppUploadResponse appUploadResponse = new AppUploadResponse();
		appUploadResponse.setDeviceType(appUploadRequest.getDeviceType());
		appUploadResponse.setOperationSystem(os.getName());
		appUploadResponse.setPackageName(apps.getPackageName());
		appUploadResponse.setUuid(apps.getUuid());
		appUploadResponse.setVersion(apps.getVersionName());
		appUploadResponse.setAppUrl(appUploadUrl);
		appUploadResponse.setProductName(apps.getProduct().getName());
		return new ApiResponse<>(appUploadResponse);
	}

	private void newAppVersionCodeValidation(String newAppPackageName, long newAppVersionCode) {
		// 2.버전 코드 중복 체크 및 중복 시 예외 발생
		if (versionDuplicateCheck(newAppPackageName, newAppVersionCode)) {
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_DUPLICATE_VERSION);
		}

		// 3. 현재 앱 버전 코드가 기존 최신 버전 보다 낮은 버전 코드의 경우 예외 발생
		if (isLowerThanPreviousLatestAppVersionCode(newAppPackageName, newAppVersionCode)) {
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_VERSION_IS_LOWER);
		}
	}

	private Device getDeviceInfoByDeviceTypeAndProductName(String deviceType, String productName) {
		log.info("[FIND_DEVICE] - DEVICE_TYPE: [{}] , PRODUCT_NAME: [{}]", deviceType, productName);
		return deviceRepository.findByTypeAndProduct_Name(deviceType, productName)
			.orElseThrow(() -> new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_DEVICE_INFO_NOT_FOUND));
	}

	private OS getOS(String operationSystem) {
		return osRepository.findByName(operationSystem.toUpperCase())
			.orElseThrow(() -> new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_OS_INFO_NOT_FOUND));
	}

	private String generateAppUUID() {
		String[] tokens = UUID.randomUUID().toString().split("-");
		return tokens[1] + "-" + tokens[4];
	}

	private ApkMeta parsingAppInfo(MultipartFile app) {
		if (app == null || app.isEmpty()) {
			log.error("Upload Application file is empty or set as null");
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL);
		}
		try {
			ByteArrayApkFile apkFile = new ByteArrayApkFile(app.getBytes());
			ApkMeta apkMeta = apkFile.getApkMeta();
			log.info(apkMeta.toString());
			apkFile.close();
			return apkMeta;
		} catch (IOException e) {
			log.error("APK Parsing Error", e);
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL);
		}
	}

	private boolean versionDuplicateCheck(String packageName, Long versionCode) {
		return appRepository.existsByPackageNameAndVersionCode(packageName, versionCode);
	}

	private boolean isLowerThanPreviousLatestAppVersionCode(String newAppPackageName, Long newAppVersionCode) {
		Long previousLatestAppVersionCode = appRepository.getLatestVersionCodeByPackageName(newAppPackageName);
		log.info("Latest VersionCode: {} , Request VersionCode: {}", previousLatestAppVersionCode, newAppVersionCode);
		log.info("Request VersionCode > Latest VersionCode => {}", newAppVersionCode > previousLatestAppVersionCode);
		return previousLatestAppVersionCode > newAppVersionCode;
	}

	@Transactional(readOnly = true)
	public ApiResponse<AppDetailInfoResponse> getLatestAppInfoByPackageName(String packageName) {
		App app = appRepository.getLatestVersionActiveAppInfoByPackageName(packageName)
			.orElseThrow(() -> new AppServiceException(ErrorCode.ERR_APP_PACKAGE_NAME_NOT_FOUND));
		AppDetailInfoResponse appDetailInfoResponse = new AppDetailInfoResponse();
		appDetailInfoResponse.setAppUrl(app.getAppUrl());
		appDetailInfoResponse.setDeviceType(app.getDevice().getType());
		appDetailInfoResponse.setOperationSystem(app.getOs().getName());
		appDetailInfoResponse.setProductName(app.getProduct().getName());
		appDetailInfoResponse.setSigningKey(app.getSignature());
		appDetailInfoResponse.setUuid(app.getUuid());
		appDetailInfoResponse.setVersion(app.getVersionName());
		appDetailInfoResponse.setVersionCode(app.getVersionCode());
		appDetailInfoResponse.setPackageName(app.getPackageName());
		appDetailInfoResponse.setUpdateRequired(app.getAppUpdateStatus().equals(AppUpdateStatus.REQUIRED));
		return new ApiResponse<>(appDetailInfoResponse);
	}

	@Transactional
	public ApiResponse<AppSigningKetRegisterResponse> registerAppSigningKey(
		AppSigningKeyRegisterRequest signingKeyRegisterRequest
	) {
		boolean hasUnRegisteredApp = appRepository.existsByPackageNameAndSignatureIsNull(
			signingKeyRegisterRequest.getPackageName());

		if (!hasUnRegisteredApp) {
			log.error("unsigned app information is not exist");
			throw new AppServiceException(ErrorCode.ERR_APP_SIGNING_KEY_REGISTER);
		}

		long amountOfAffectAppInfo = appRepository.registerSigningKeyByPackageName(
			signingKeyRegisterRequest.getPackageName(), signingKeyRegisterRequest.getSigningKey());

		if (amountOfAffectAppInfo <= 0) {
			log.error("Affect App not exist");
			throw new AppServiceException((ErrorCode.ERR_APP_SIGNING_KEY_REGISTER));
		}

		List<App> updateAppList = appRepository.getAppByPackageNameAndSignature(
			signingKeyRegisterRequest.getPackageName(), signingKeyRegisterRequest.getSigningKey());

		List<SignedAppInfoResponse> signedAppInfoResponses = updateAppList.stream()
			.map(app -> {
				SignedAppInfoResponse appInfo = new SignedAppInfoResponse();
				appInfo.setUuid(app.getUuid());
				appInfo.setVersion(app.getVersionName());
				appInfo.setDeviceType(app.getDevice().getType());
				appInfo.setOperationSystem(app.getOs().getName());
				appInfo.setPackageName(app.getPackageName());
				appInfo.setAppUrl(app.getAppUrl());
				appInfo.setProductName(app.getProduct().getName());
				appInfo.setUpdateRequired(app.getAppUpdateStatus().equals(AppUpdateStatus.REQUIRED));
				appInfo.setCreatedDate(app.getCreatedDate());
				appInfo.setAppInfoUpdatedDate(app.getUpdatedDate());
				return appInfo;
			}).collect(Collectors.toList());
		return new ApiResponse<>(new AppSigningKetRegisterResponse(signedAppInfoResponses));
	}

	@Transactional
	public ApiResponse<AppDetailInfoResponse> appInfoUpdate(AppInfoUpdateRequest appInfoUpdateRequest) {
		App app = appRepository.findByUuid(appInfoUpdateRequest.getAppUUID())
			.orElseThrow(() -> new AppServiceException(ErrorCode.ERR_APP_INFO_NOT_FOUND));

		log.info("[APP_INFORMATION_UPDATE] - [{}]", appInfoUpdateRequest.toString());

		if (appInfoUpdateRequest.getAppStatus() != null) {
			app.setAppStatus(appInfoUpdateRequest.getAppStatus());
		}

		if (appInfoUpdateRequest.getAppUpdateStatus() != null) {
			app.setAppUpdateStatus(appInfoUpdateRequest.getAppUpdateStatus());
		}

		appRepository.save(app);

		AppDetailInfoResponse appDetailInfoResponse = new AppDetailInfoResponse();
		appDetailInfoResponse.setAppUrl(app.getAppUrl());
		appDetailInfoResponse.setVersionCode(app.getVersionCode());
		appDetailInfoResponse.setDeviceType(app.getDevice().getType());
		appDetailInfoResponse.setOperationSystem(app.getOs().getName());
		appDetailInfoResponse.setProductName(app.getProduct().getName());
		appDetailInfoResponse.setSigningKey(app.getSignature());
		appDetailInfoResponse.setUuid(app.getUuid());
		appDetailInfoResponse.setVersion(app.getVersionName());
		appDetailInfoResponse.setPackageName(app.getPackageName());
		appDetailInfoResponse.setUpdateRequired(app.getAppUpdateStatus().equals(AppUpdateStatus.REQUIRED));
		return new ApiResponse<>(appDetailInfoResponse);
	}

	@Transactional(readOnly = true)
	public ApiResponse<AppVersionInfoListResponse> getAllAppInfo() {
		List<App> apps = appRepository.findAll();
		List<AppVersionInfoResponse> appInfoList = new ArrayList<>();
		apps.forEach((app) -> {
			AppVersionInfoResponse appInfo = new AppVersionInfoResponse();
			appInfo.setId(app.getId());
			appInfo.setUuid(app.getUuid());
			appInfo.setVersionName(app.getVersionName());
			appInfo.setDeviceName(app.getDevice().getType());
			appInfo.setAppUrl(app.getAppUrl());
			appInfo.setPackageName(app.getPackageName());
			appInfo.setGuideUrl(app.getGuideUrl());
			appInfo.setImageUrl(app.getImageUrl());
			appInfo.setRegisterDate(app.getCreatedDate());
			appInfo.setAppStatus(app.getAppStatus());
			appInfo.setAppUpdateStatus(app.getAppUpdateStatus());
			appInfo.setSigningApp(StringUtils.hasText(app.getSignature()));
			appInfoList.add(appInfo);
		});
		return new ApiResponse<>(new AppVersionInfoListResponse(appInfoList));
	}

	@Transactional
	public AdminAppUploadResponse adminApplicationUploadAndRegister(
		AdminAppUploadRequest adminAppUploadRequest, String userUUID
	) {
		//1-1. 마스터 유저인지 검증
		masterUserValidation(userUUID);

		//1-2. product, os, device 검증
		Product product = getProduct(adminAppUploadRequest.getProductName());
		OS os = getOS(adminAppUploadRequest.getOperationSystem());
		Device device = getDevice(
			adminAppUploadRequest.getDeviceType(), adminAppUploadRequest.getDeviceModel(),
			adminAppUploadRequest.getProductName()
		);

		String applicationSignature = adminAppUploadRequest.getSigningKey();
		String packageName = null;
		String versionName = null;
		Long versionCode = null;

		//1-3. apk 파일 버전 유효성 검증
		if (adminAppUploadRequest.isApkApp()) {
			ApkMeta apkMeta = parsingAppInfo(adminAppUploadRequest.getUploadAppFile());
			packageName = apkMeta.getPackageName();
			versionName = apkMeta.getVersionName();
			versionCode = apkMeta.getVersionCode();

			Optional<App> latestActiveApp = appRepository.getLatestVersionActiveAppInfoByPackageName(packageName);
			if (latestActiveApp.isPresent()) {
				newAppVersionCodeValidation(packageName, versionCode);
				applicationSignature = latestActiveApp.get().getSignature();
			}
		}

		//1-4. apk 제외한 파일 버전 유효성 검증
		if (!adminAppUploadRequest.isApkApp()) {
			versionName = adminAppUploadRequest.getVersionName();
			versionCode = adminAppUploadRequest.getVersionCode();
			if (StringUtils.isEmpty(versionName) || StringUtils.isEmpty(versionCode)) {
				throw new AppServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
			}
			Optional<App> latestActiveApp = appRepository.getLatestVersionActiveAppInfoByDeviceAndOs(device, os);
			if (latestActiveApp.isPresent()) {
				newAppVersionCodeValidation(device, os, versionCode);
			}
		}

		//2-1. 파일 업로드
		String appUploadUrl = fileUploadService.upload(adminAppUploadRequest.getUploadAppFile());

		//2-2. 앱 정보 저장
		App app = App.builder()
			.uuid(generateAppUUID())
			.device(device)
			.product(product)
			.os(os)
			.packageName(packageName)
			.versionName(versionName)
			.versionCode(versionCode)
			.appUrl(appUploadUrl)
			.imageUrl("")
			.signature(applicationSignature)
			.appStatus(AppStatus.ACTIVE)
			.appUpdateStatus(AppUpdateStatus.REQUIRED)
			.build();
		appRepository.save(app);

		//2-3. 응답
		return responseMapper.appToAdminAppUploadResponse(app);
	}

	private void masterUserValidation(String userUUID) {
		log.info("[REST - WORKSPACE SERVER][GET MY WORKSPACE LIST] request user uuid : {}", userUUID);
		ApiResponse<WorkspaceInfoListResponse> apiResponse = workspaceRestService.getMyWorkspaceInfoList(
			userUUID);
		if (apiResponse.getCode() != 200 || CollectionUtils.isEmpty(apiResponse.getData().getWorkspaceList())) {
			log.error(
				"[REST - WORKSPACE SERVER][GET MY WORKSPACE LIST] response code : {}, response message : {}",
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
		if (appRepository.isLowerThanPreviousAppVersionCode(device, os, versionCode)) {
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_VERSION_IS_LOWER);
		}
	}
}