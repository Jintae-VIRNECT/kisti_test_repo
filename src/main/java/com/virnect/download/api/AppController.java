package com.virnect.download.api;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.download.application.AppService;
import com.virnect.download.dto.request.AppInfoUpdateRequest;
import com.virnect.download.dto.request.AppSigningKeyRegisterRequest;
import com.virnect.download.dto.request.AppUploadRequest;
import com.virnect.download.dto.response.AppDetailInfoResponse;
import com.virnect.download.dto.response.AppInfoListResponse;
import com.virnect.download.dto.response.AppSigningKetRegisterResponse;
import com.virnect.download.dto.response.AppUploadResponse;
import com.virnect.download.exception.AppServiceException;
import com.virnect.download.global.common.ApiResponse;
import com.virnect.download.global.error.ErrorCode;

/**
 * Project: PF-Download
 * DATE: 2020-08-06
 * AUTHOR: jeonghyeon.chang (johnmark)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: application upload and registration controller
 */
@Api
@Slf4j
@RestController
@RequestMapping("/download/app")
@RequiredArgsConstructor
public class AppController {
	private static final String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
	private final AppService appService;

	@ApiOperation(value = "앱 등록 API")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "uploadAppFile", value = "업로드 앱 파일", paramType = "form", dataType = "__file")
	})
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<AppUploadResponse>> appUploadRequestHandler(
		@ModelAttribute @Valid AppUploadRequest appUploadRequest, BindingResult result
	) {
		log.info("Request: {}", appUploadRequest.toString());
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new AppServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<AppUploadResponse> responseMessage = appService.applicationUploadAndRegister(appUploadRequest);
		return ResponseEntity.ok(responseMessage);
	}

	@ApiOperation(value = "최신 앱 정보 조회")
	@GetMapping("/latest")
	public ResponseEntity<ApiResponse<AppDetailInfoResponse>> getLatestAppDetailInfoRequestHandler(
		@RequestParam("packageName") String packageName
	) {
		if (StringUtils.isEmpty(packageName)) {
			throw new AppServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<AppDetailInfoResponse> responseMessage = appService.getLatestAppInfoByPackageName(packageName);
		return ResponseEntity.ok(responseMessage);
	}

	@ApiOperation(value = "앱 서명키 등록")
	@PostMapping("/signingkey")
	public ResponseEntity<ApiResponse<AppSigningKetRegisterResponse>> registerAppSigingKey(
		@RequestBody AppSigningKeyRegisterRequest signingKeyRegisterRequest, BindingResult result
	) {
		log.info("Request: {}", signingKeyRegisterRequest.toString());
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new AppServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<AppSigningKetRegisterResponse> responseMessage = appService.registerAppSigningKey(
			signingKeyRegisterRequest);
		return ResponseEntity.ok(responseMessage);
	}

	@ApiOperation(value = "앱 정보 수정")
	@PostMapping("/{appUUID}/info")
	public ResponseEntity<ApiResponse<AppDetailInfoResponse>> updateAppStatus(
		AppInfoUpdateRequest appInfoUpdateRequest, BindingResult result
	) {
		if (result.hasErrors()) {
			throw new AppServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<AppDetailInfoResponse> responseMessage = appService.appInfoUpdate(appInfoUpdateRequest);
		return ResponseEntity.ok(responseMessage);
	}

	@ApiOperation(value = "앱 정보 조회")
	@GetMapping("/list")
	public ResponseEntity<ApiResponse<AppInfoListResponse>> getAllAppInfoList(){
		ApiResponse<AppInfoListResponse> responseMessage = appService.getAllAppInfo();
		return ResponseEntity.ok(responseMessage);
	}
}
