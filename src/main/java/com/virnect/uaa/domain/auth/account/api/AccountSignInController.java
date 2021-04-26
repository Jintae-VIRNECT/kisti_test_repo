package com.virnect.uaa.domain.auth.account.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.application.signin.AccountOtpSignInService;
import com.virnect.uaa.domain.auth.account.application.signin.AccountSignInService;
import com.virnect.uaa.domain.auth.account.dto.request.LoginRequest;
import com.virnect.uaa.domain.auth.account.dto.request.LogoutRequest;
import com.virnect.uaa.domain.auth.account.dto.request.OTPLoginRequest;
import com.virnect.uaa.domain.auth.account.dto.request.OTPQRGenerateRequest;
import com.virnect.uaa.domain.auth.account.dto.response.LogoutResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OAuthTokenResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OTPQRGenerateResponse;
import com.virnect.uaa.domain.auth.account.error.AuthenticationErrorCode;
import com.virnect.uaa.domain.auth.account.error.exception.UserAuthenticationServiceException;
import com.virnect.uaa.global.common.ApiResponse;

/**
 * @project: PF-Auth
 * @author: jeonghyeon.chang (johnmark)
 * @email: practice1356@gmail.com
 * @since: 2020.03.09
 * @description: Auth API Controller Class
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Api(value = "로그인 로그아웃 관련 API 컨트롤러")
@RequestMapping("/auth")
public class AccountSignInController {
	private static final String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
	private final AccountSignInService accountSignInService;
	private final AccountOtpSignInService accountOtpSignInService;

	@ApiOperation(value = "로그인 API")
	@PostMapping("/signin")
	public ResponseEntity<ApiResponse<OAuthTokenResponse>> loginRequestHandler(
		HttpServletRequest request, HttpServletResponse response, @RequestBody @Valid LoginRequest loginRequest,
		@RequestHeader(value = "client", required = false, defaultValue = "NONE") String client,
		BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		log.info("CLIENT: {} , LOGIN: {}", client, loginRequest);
		OAuthTokenResponse loginResponse = accountSignInService.login(loginRequest, request, response);
		log.info("Result: {}", loginResponse.toString());
		return ResponseEntity.ok(new ApiResponse<>(loginResponse));
	}

	@ApiOperation(value = "로그아웃 API")
	@PostMapping("/signout")
	public ResponseEntity<ApiResponse<LogoutResponse>> logoutRequestHandler(
		@RequestBody @Valid LogoutRequest logoutRequest, HttpServletRequest request,
		HttpServletResponse response, BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		LogoutResponse logoutResponse = accountSignInService.logout(logoutRequest, request, response);
		return ResponseEntity.ok(new ApiResponse<>(logoutResponse));
	}

	@ApiOperation(value = "QR 로그인용 QR 발급")
	@PostMapping("/otp/qr")
	public ResponseEntity<ApiResponse<OTPQRGenerateResponse>> otpQRGenerateRequestHandler(
		@RequestBody @Valid OTPQRGenerateRequest otpQRGenerateRequest, BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		OTPQRGenerateResponse responseMessage = accountOtpSignInService.loginQrCodeGenerate(otpQRGenerateRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(value = "QR 로그인")
	@PostMapping("/signin/otp")
	public ResponseEntity<ApiResponse<OAuthTokenResponse>> otpLoginRequestHandler(
		@RequestBody @Valid OTPLoginRequest otpLoginRequest, HttpServletRequest request, BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		OAuthTokenResponse responseMessage = accountOtpSignInService.qrCodeLogin(otpLoginRequest, request);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

}
