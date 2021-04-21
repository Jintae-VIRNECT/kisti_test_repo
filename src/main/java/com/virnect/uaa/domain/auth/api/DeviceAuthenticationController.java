package com.virnect.uaa.domain.auth.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.application.DeviceAuthenticationService;
import com.virnect.uaa.domain.auth.dto.device.request.DeviceAuthenticationRequest;
import com.virnect.uaa.domain.auth.dto.device.response.DeviceAuthenticationResponse;
import com.virnect.uaa.domain.auth.error.exception.UserAuthenticationServiceException;
import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.domain.auth.error.AuthenticationErrorCode;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class DeviceAuthenticationController {
	private static final String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
	private final DeviceAuthenticationService deviceAuthenticationService;

	@ApiOperation(value = "디바이스 앱 인증 (exe 앱 지원 불가)")
	@PostMapping("/app")
	public ResponseEntity<ApiResponse<DeviceAuthenticationResponse>> applicationAuthenticationRequestHandler(
		@RequestBody DeviceAuthenticationRequest deviceAuthenticationRequest, BindingResult result
	) {
		if (result.hasErrors()) {
			if (result.hasErrors()) {
				result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			}
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<DeviceAuthenticationResponse> responseMessage = deviceAuthenticationService.deviceAppIntegrityCheck(
			deviceAuthenticationRequest);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("apiName", "/auth/app");
		headers.set("encrypt", "true");
		return ResponseEntity.ok().headers(headers).body(responseMessage);
	}
}