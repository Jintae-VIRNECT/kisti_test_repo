package com.virnect.uaa.domain.auth.account.api;

import java.beans.PropertyEditorSupport;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.uaa.domain.auth.account.application.UserAuthenticationService;
import com.virnect.uaa.domain.auth.account.dto.request.EmailAuthRequest;
import com.virnect.uaa.domain.auth.account.dto.request.LoginRequest;
import com.virnect.uaa.domain.auth.account.dto.request.LogoutRequest;
import com.virnect.uaa.domain.auth.account.dto.request.OTPLoginRequest;
import com.virnect.uaa.domain.auth.account.dto.request.OTPQRGenerateRequest;
import com.virnect.uaa.domain.auth.account.dto.request.RegisterRequest;
import com.virnect.uaa.domain.auth.account.dto.request.TokenRefreshRequest;
import com.virnect.uaa.domain.auth.account.dto.response.EmailAuthenticationResponse;
import com.virnect.uaa.domain.auth.account.dto.response.EmailVerificationResult;
import com.virnect.uaa.domain.auth.account.dto.response.LogoutResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OAuthTokenResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OTPQRGenerateResponse;
import com.virnect.uaa.domain.auth.account.dto.response.RefreshTokenResponse;
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
@Api(value = "인증 API 컨트롤러 , /auth 로 요청이 시작됩니다.")
@RequestMapping("/auth")
public class UserAuthenticationController {
	private static final String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
	private final UserAuthenticationService userAuthenticationService;

	@InitBinder("registerRequest")
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(MultipartFile.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(null);
			}
		});
	}

	@ApiOperation(value = "회원가입 API")
	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<OAuthTokenResponse>> registerRequestHandler(
		@ModelAttribute @Valid RegisterRequest registerRequest, BindingResult result,
		HttpServletRequest request, @ApiIgnore Locale locale
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		log.info("SignUp Request: [{}]", registerRequest.toString());
		OAuthTokenResponse registerResponse = userAuthenticationService.register(
			registerRequest, request, locale
		);
		return ResponseEntity.ok(new ApiResponse<>(registerResponse));
	}

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
		OAuthTokenResponse loginResponse = userAuthenticationService.loginHandler(
			loginRequest, request, response);
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
		LogoutResponse logoutResponse = userAuthenticationService.logout(request, response, logoutRequest);
		return ResponseEntity.ok(new ApiResponse<>(logoutResponse));
	}

	@ApiOperation(value = "이메일 인증 API")
	@PostMapping("/email")
	public ResponseEntity<ApiResponse<EmailAuthenticationResponse>> emailAuthorizationRequestHandler(
		@RequestBody @Valid EmailAuthRequest emailAuthRequest, Locale locale, BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		EmailAuthenticationResponse emailAuthorizationResponse = userAuthenticationService.emailAuthorization(
			emailAuthRequest, locale);
		return ResponseEntity.ok(new ApiResponse<>(emailAuthorizationResponse));
	}

	@ApiOperation(value = "이메일 인증 코드 확인 API")
	@GetMapping("/verification")
	public ResponseEntity<ApiResponse<EmailVerificationResult>> getRegisterSessionCodeRequestHandler(
		@RequestParam(name = "code") String code, @RequestParam("email") String email
	) {
		if (StringUtils.isEmpty(code) || StringUtils.isEmpty(email)) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		EmailVerificationResult verificationCodeCheckResult = userAuthenticationService.emailVerificationCodeCheck(
			code, email);
		return ResponseEntity.ok(new ApiResponse<>(verificationCodeCheckResult));
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
		OTPQRGenerateResponse responseMessage = userAuthenticationService.otpQRCodeGenerate(otpQRGenerateRequest);
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
		OAuthTokenResponse responseMessage = userAuthenticationService.otpLoginHandler(otpLoginRequest, request);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(value = "인증 토큰 재발급")
	@PostMapping("/oauth/token")
	public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshTokenRequestHandler(
		@RequestBody @Valid TokenRefreshRequest tokenRefreshRequest, HttpServletRequest request, BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		RefreshTokenResponse responseMessage = userAuthenticationService.generateNewAccessToken(
			tokenRefreshRequest, request);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}
}
