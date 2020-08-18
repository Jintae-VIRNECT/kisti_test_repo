package com.virnect.download.api;

import com.virnect.download.application.AppService;
import com.virnect.download.dto.request.AppSigningKeyRegisterRequest;
import com.virnect.download.dto.request.AppUploadRequest;
import com.virnect.download.dto.response.AppDetailInfoResponse;
import com.virnect.download.dto.response.AppSigningKetRegisterResponse;
import com.virnect.download.dto.response.AppUploadResponse;
import com.virnect.download.exception.AppServiceException;
import com.virnect.download.global.common.ApiResponse;
import com.virnect.download.global.error.ErrorCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
@RequestMapping("/app")
@RequiredArgsConstructor
public class AppController {
    private final AppService appService;
    private static final String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";

    @ApiOperation(value = "앱 등록 API")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AppUploadResponse>> appUploadRequestHandler(@ModelAttribute @Valid AppUploadRequest appUploadRequest, BindingResult result) {
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
    public ResponseEntity<ApiResponse<AppDetailInfoResponse>> getLatestAppDetailInfoRequestHandler(@RequestParam("packageName") String packageName) {
        if (StringUtils.isEmpty(packageName)) {
            throw new AppServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<AppDetailInfoResponse> responseMessage = appService.getLatestAppInfoByPackageName(packageName);
        return ResponseEntity.ok(responseMessage);
    }


    @ApiOperation(value = "앱 서명키 등록")
    @PostMapping("/signingkey")
    public ResponseEntity<ApiResponse<AppSigningKetRegisterResponse>> registerAppSigingKey(@RequestBody AppSigningKeyRegisterRequest signingKeyRegisterRequest, BindingResult result) {
        log.info("Request: {}", signingKeyRegisterRequest.toString());
        if (result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new AppServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<AppSigningKetRegisterResponse> responseMessage = appService.registerAppSigningKey(signingKeyRegisterRequest);
        return ResponseEntity.ok(responseMessage);
    }
}
