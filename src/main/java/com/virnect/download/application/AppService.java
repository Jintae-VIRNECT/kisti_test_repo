package com.virnect.download.application;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import net.dongliu.apk.parser.ByteArrayApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.download.dao.AppRepository;
import com.virnect.download.dao.DeviceRepository;
import com.virnect.download.dao.OSRepository;
import com.virnect.download.dao.ProductRepository;
import com.virnect.download.domain.App;
import com.virnect.download.domain.AppUpdateStatus;
import com.virnect.download.domain.Device;
import com.virnect.download.domain.OS;
import com.virnect.download.domain.Product;
import com.virnect.download.dto.request.AppSigningKeyRegisterRequest;
import com.virnect.download.dto.request.AppUploadRequest;
import com.virnect.download.dto.response.AppDetailInfoResponse;
import com.virnect.download.dto.response.AppSigningKetRegisterResponse;
import com.virnect.download.dto.response.AppUploadResponse;
import com.virnect.download.dto.response.SignedAppInfoResponse;
import com.virnect.download.exception.AppServiceException;
import com.virnect.download.global.common.ApiResponse;
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

	@Transactional
	public ApiResponse<AppUploadResponse> applicationUploadAndRegister(AppUploadRequest appUploadRequest) {
		// 1. APK 파일 파싱
		ApkMeta apkMeta = parsingAppInfo(appUploadRequest.getUploadAppFile());
		String applicationSignature = null;

		Optional<App> previousAppInfo = this.appRepository.getLatestVersionAppInfoByPackageName(
			apkMeta.getPackageName());

		if (previousAppInfo.isPresent()) {

			// 2. 버전 코드 중복 체크
			boolean isDuplicateVersion = versionDuplicateCheck(apkMeta.getPackageName(), apkMeta.getVersionCode());

			// 3. 중복 시 예외 발생
			if (isDuplicateVersion) {
				throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_DUPLICATE_VERSION);
			}

			// 4. 최신 버전 코드 및 현재 버전 코드 비교 체크
			boolean isLowerThenLatestVersionCode = versionLowerCheck(
				apkMeta.getPackageName(), apkMeta.getVersionCode());

			// 5. 최신 버전 보다 낮은 버전 코드의 경우 예외 발생
			if (isLowerThenLatestVersionCode) {
				throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_VERSION_IS_LOWER);
			}

			applicationSignature = previousAppInfo.get().getSignature();
		}

		// 1. find app device type, device, os information
		Device device = getDeviceInfoByDeviceType(appUploadRequest.getDeviceType());
		OS os = getOSInfo(appUploadRequest.getOperationSystem());
		Product product = productRepository.findByName(appUploadRequest.getProductName())
			.orElseThrow(() -> new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_PRODUCT_INFO_NOT_FOUND));

		// 2. application artifact upload
		String appUploadUrl;
		try {
			appUploadUrl = fileUploadService.upload(appUploadRequest.getUploadAppFile());
		} catch (IOException e) {
			log.error("[FILE UPLOAD ERROR]", e);
			throw new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL);
		}

		App apps = App.builder()
			.uuid(generateAppUUID())
			.device(device)
			.product(product)
			.os(os)
			.packageName(apkMeta.getPackageName())
			.versionName(apkMeta.getVersionName())
			.versionCode(apkMeta.getVersionCode())
			.appOriginUrl(appUploadUrl)
			.appUrl(appUploadUrl)
			.image("")
			.signature(applicationSignature)
			.build();

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

	private Device getDeviceInfoByDeviceType(String deviceType) {
		return deviceRepository.findByName(deviceType)
			.orElseThrow(() -> new AppServiceException(ErrorCode.ERR_APP_UPLOAD_FAIL_DEVICE_INFO_NOT_FOUND));
	}

	private OS getOSInfo(String operationSystem) {
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

	private boolean versionLowerCheck(String packageName, Long versionCode) {
		Long latestVersionCode = appRepository.getLatestVersionCodeByPackageName(packageName);
		log.info("Latest VersionCode: {} , Request VersionCode: {}", latestVersionCode, versionCode);
		log.info("Request VersionCode > Latest VersionCode => {}", versionCode > latestVersionCode);
		return latestVersionCode > versionCode;
	}

	@Transactional(readOnly = true)
	public ApiResponse<AppDetailInfoResponse> getLatestAppInfoByPackageName(String packageName) {
		App app = appRepository.getLatestVersionAppInfoByPackageName(packageName)
			.orElseThrow(() -> new AppServiceException(ErrorCode.ERR_APP_PACKAGE_NAME_NOT_FOUND));
		AppDetailInfoResponse appDetailInfoResponse = new AppDetailInfoResponse();
		appDetailInfoResponse.setAppUrl(app.getAppUrl());
		appDetailInfoResponse.setDeviceType(app.getDevice().getName());
		appDetailInfoResponse.setOperationSystem(app.getOs().getName());
		appDetailInfoResponse.setProductName(app.getProduct().getName());
		appDetailInfoResponse.setSigningKey(app.getSignature());
		appDetailInfoResponse.setUuid(app.getUuid());
		appDetailInfoResponse.setVersion(app.getVersionName());
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

		List<App> updateAppList = appRepository.findByPackageNameAndSignature(
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
}
