package com.virnect.uaa.domain.auth.device.application;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vdurmont.semver4j.Semver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.common.error.AuthenticationErrorCode;
import com.virnect.uaa.domain.auth.common.exception.DeviceAuthenticationServiceException;
import com.virnect.uaa.domain.auth.device.dao.DeviceAccessLogRepository;
import com.virnect.uaa.domain.auth.device.dao.DeviceAuthRepository;
import com.virnect.uaa.domain.auth.device.dao.DeviceRepository;
import com.virnect.uaa.domain.auth.device.domain.Device;
import com.virnect.uaa.domain.auth.device.domain.DeviceAccessLog;
import com.virnect.uaa.domain.auth.device.domain.DeviceAuth;
import com.virnect.uaa.domain.auth.device.dto.request.DeviceAuthenticationInfo;
import com.virnect.uaa.domain.auth.device.dto.request.DeviceAuthenticationRequest;
import com.virnect.uaa.domain.auth.device.dto.response.DeviceAuthenticationResponse;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.global.common.AES256Utils;
import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.infra.rest.app.AppRestService;
import com.virnect.uaa.infra.rest.app.dto.AppDetailInfoResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceAuthenticationService {
	private static final long DEVICE_AUTH_INFORMATION_EXPIRED_SECONDS = Duration.ofHours(24).getSeconds();
	private static final String DEVICE_AUTH_KEY_HEADER_NAME = "deviceAuthKey";
	private static final String PREVIOUS_APP_KEY = "xBgrF92cBDdbkxbgf3fvTVLX_ykXmM53JfLaoVNAw5o";
	private final AppRestService appRestService;
	private final DeviceRepository deviceRepository;
	private final DeviceAuthRepository deviceAuthRepository;
	private final DeviceAccessLogRepository deviceAccessLogRepository;
	private final ObjectMapper objectMapper;

	@Value("${version-code-compare:true}")
	private boolean isVersionCodeCompare;

	/**
	 * 기기 및 앱 인증 처리
	 *
	 * @param deviceAuthenticationRequest - 기기 및 앱 인증 요청 정보
	 * @return - 기기 및 앱 인증 정보
	 */
	public ApiResponse<DeviceAuthenticationResponse> deviceAppIntegrityCheck(
		DeviceAuthenticationRequest deviceAuthenticationRequest
	) {
		log.info("[DEVICE_AUTHENTICATION_INFO][ENCODED] - {}", deviceAuthenticationRequest.getData());

		boolean isPreviousApp = false;

		// 1. 어플리케이션 패키지 명으로 앱 정보 조회
		ApiResponse<AppDetailInfoResponse> appRestMessage = appRestService.getLatestAppInformationByPackageName(
			deviceAuthenticationRequest.getPackageName());
		DeviceAuthenticationResponse authenticationResponse;

		// 1-1. 앱 정보가 없는 경우, 예외 발생
		if (appRestMessage.getData().getUuid().isEmpty()) {
			log.error("Device authentication information not found");
			throw new DeviceAuthenticationServiceException(AuthenticationErrorCode.ERR_APP_AUTHENTICATION);
		}

		// 2. 암호화 된 기기 정보 조회 및 복호화
		AppDetailInfoResponse appInformation = appRestMessage.getData();
		log.info("[LATEST_APP_INFORMATION] - {}", appInformation.toString());

		String decodedDeviceInformation;

		decodedDeviceInformation = AES256Utils.decrypt(
			appInformation.getSigningKey().substring(0, 32), deviceAuthenticationRequest.getData());

		if (StringUtils.isEmpty(decodedDeviceInformation)) {
			decodedDeviceInformation = AES256Utils.decrypt(
				PREVIOUS_APP_KEY.substring(0, 32), deviceAuthenticationRequest.getData()
			);
			isPreviousApp = true;
			log.info(
				"[PREVIOUS_APP_DECRYPT_PROCESS_START] KEY: = [{}] , isPreviousAPP: [{}]", PREVIOUS_APP_KEY,
				true
			);
		}

		if (decodedDeviceInformation == null) {
			throw new DeviceAuthenticationServiceException(AuthenticationErrorCode.ERR_APP_AUTHENTICATION);
		}

		// 3. 기기 정보가 없는 경우, 예외 발생
		if (decodedDeviceInformation.isEmpty()) {
			log.error("decoded device information not exist");
			throw new DeviceAuthenticationServiceException(AuthenticationErrorCode.ERR_APP_INFORMATION_DECRYPT);
		}

		log.info("[DEVICE_AUTHENTICATION_INFO][DECODE] - {}", decodedDeviceInformation);

		DeviceAuthenticationInfo deviceAuthenticationInfo;
		try {
			deviceAuthenticationInfo = objectMapper.readValue(decodedDeviceInformation, DeviceAuthenticationInfo.class);
		} catch (JsonProcessingException e) {
			log.error("Device authentication information json serialized fail.");
			throw new DeviceAuthenticationServiceException(AuthenticationErrorCode.ERR_APP_INFORMATION_DECRYPT);
		}

		log.info("[DEVICE_AUTHENTICATION_INFO] - {}", deviceAuthenticationInfo.toString());

		// 4. 기기 정보에, 기기 식별 아이디가 없는 경우 신규 기기로 등록 진행
		Device newDevice = Device.builder()
			.uuid(UUID.randomUUID().toString())
			.deviceType(deviceAuthenticationInfo.getDeviceType())
			.appName(appInformation.getProductName())
			.appVersion(deviceAuthenticationInfo.getVersionName())
			.manufacture(deviceAuthenticationInfo.getManufacture())
			.model(deviceAuthenticationInfo.getModel())
			.build();
		deviceRepository.save(newDevice);
		// 4-1. 신규 등록 기기 정보로 기기 및 앱 인증 정보 생성
		authenticationResponse = getDeviceAuthenticationResponse(
			newDevice.getUuid(), appInformation, deviceAuthenticationInfo);

		// 6. 기기 인증 및 데이터 암복호화 정보 생성 및 레디스에 저장
		DeviceAuth deviceAuthInfo = DeviceAuth.builder()
			.appName(appInformation.getProductName())
			.appVersionCode(deviceAuthenticationInfo.getVersionCode())
			.appVersionName(deviceAuthenticationInfo.getVersionName())
			.deviceAuthKey(authenticationResponse.getDeviceAuthKey())
			.deviceId(authenticationResponse.getDeviceId())
			.deviceType(deviceAuthenticationInfo.getDeviceType())
			.secretKey(authenticationResponse.getSecretKey())
			.expiredAt(DEVICE_AUTH_INFORMATION_EXPIRED_SECONDS)
			.build();

		deviceAuthRepository.save(deviceAuthInfo);

		log.info("[DEVICE_AUTH_INFO_SAVE] - {}", deviceAuthInfo.toString());

		// 필터에서 데이터 암호화를 하기 위해 암호화 키 설정
		// 만약 이전 업데이트 전 앱 키로 복호화 된 앱 정보라면
		if (isPreviousApp) {
			log.info("[ENCRYPT BY PREVIOUS APP KEY] -> [{}]", PREVIOUS_APP_KEY.substring(0, 32));
			authenticationResponse.setAppSignature(PREVIOUS_APP_KEY.substring(0, 32));
			return new ApiResponse<>(authenticationResponse);
		}

		// 최신 앱 버전인경우
		authenticationResponse.setAppSignature(appInformation.getSigningKey().substring(0, 32));
		log.info("[ENCRYPT BY LATEST APP KEY] -> [{}]", appInformation.getSigningKey().substring(0, 32));
		return new ApiResponse<>(authenticationResponse);
	}

	/**
	 * 기기 및 앱 인증 정보 생성
	 *
	 * @param deviceId                - 기기 식별 아이디
	 * @param latestAppInformation    - 패키지 정보 기반 최신 앱 정보
	 * @param deviceDetailInformation - 기기 정보
	 * @return - 기기 인증 정보
	 */
	private DeviceAuthenticationResponse getDeviceAuthenticationResponse(
		String deviceId, AppDetailInfoResponse latestAppInformation, DeviceAuthenticationInfo deviceDetailInformation
	) {
		boolean isVersionUp;
		log.info("-> VersionCodeCompare Active: {}", isVersionCodeCompare);
		if (isVersionCodeCompare) {
			isVersionUp = deviceDetailInformation.getVersionCode() < latestAppInformation.getVersionCode();
			log.info(
				"-> version code compare. {} < {} = {}", deviceDetailInformation.getVersionCode(),
				latestAppInformation.getVersionCode(), isVersionUp
			);
		} else {
			isVersionUp = new Semver(deviceDetailInformation.getVersionName()).isLowerThan(
				latestAppInformation.getVersion());
			log.info(
				"-> version name compare. {} < {} = {}", deviceDetailInformation.getVersionName(),
				latestAppInformation.getVersion(), isVersionUp
			);
		}
		String deviceAuthKey = RandomStringUtils.randomAlphanumeric(15);
		String secretKey = RandomStringUtils.randomAlphanumeric(32);
		DeviceAuthenticationResponse authenticationResponse = new DeviceAuthenticationResponse();
		authenticationResponse.setDeviceAuthKey(deviceAuthKey);
		authenticationResponse.setSecretKey(secretKey);
		authenticationResponse.setDeviceId(deviceId);
		authenticationResponse.setLatestAppVersionCode(latestAppInformation.getVersionCode());
		authenticationResponse.setLatestAppVersionName(latestAppInformation.getVersion());
		authenticationResponse.setVersionUp(isVersionUp);
		authenticationResponse.setUpdateRequired(latestAppInformation.isUpdateRequired());
		authenticationResponse.setAppUpdateUrl(latestAppInformation.getAppUrl());

		log.info(
			"[DEVICE_VERSION_UP][{}] CURRENT:[{}] < LATEST:[{}] = {}",
			deviceDetailInformation.getPackageName(),
			deviceDetailInformation.getVersionName(),
			latestAppInformation.getVersion(),
			isVersionUp
		);

		return authenticationResponse;
	}

	public void saveUserDeviceAccessLog(User user, HttpServletRequest request) {
		String deviceAuthKey = request.getHeader(DEVICE_AUTH_KEY_HEADER_NAME);
		if (deviceAuthKey == null || deviceAuthKey.isEmpty()) {
			return;
		}

		DeviceAuth deviceAuth = deviceAuthRepository.findByDeviceAuthKey(deviceAuthKey);

		if (deviceAuth == null) {
			log.error("Device authentication information not found");
			return;
		}

		Optional<Device> device = deviceRepository.findByUuid(deviceAuth.getDeviceId());

		if (!device.isPresent()) {
			log.error("Device information not found");
			return;
		}

		DeviceAccessLog deviceAccessLog = DeviceAccessLog
			.builder()
			.device(device.get())
			.email(user.getEmail())
			.userName(user.getName())
			.userUUID(user.getUuid())
			.userRegisteredDate(user.getCreatedDate())
			.build();

		log.info("[NEW_USER_DEVICE_ACCESS_LOG_SAVED]");
		deviceAccessLogRepository.save(deviceAccessLog);
	}
}
