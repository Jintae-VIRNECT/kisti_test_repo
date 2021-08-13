package com.virnect.uaa.domain.auth.account.api;

import java.beans.PropertyEditorSupport;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.uaa.domain.auth.account.application.signup.AccountSignUpService;
import com.virnect.uaa.domain.auth.account.dto.request.EmailAuthRequest;
import com.virnect.uaa.domain.auth.account.dto.request.RegisterRequest;
import com.virnect.uaa.domain.auth.account.dto.response.EmailAuthResponse;
import com.virnect.uaa.domain.auth.account.dto.response.EmailVerificationResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OAuthTokenResponse;
import com.virnect.uaa.domain.auth.common.error.AuthenticationErrorCode;
import com.virnect.uaa.domain.auth.common.exception.UserAuthenticationServiceException;
import com.virnect.uaa.global.common.ApiResponse;

@Slf4j
@RestController
@Api(value = "회원가입 관련 인증 컨트롤러")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AccountSignUpController {
	private static final String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
	private final AccountSignUpService accountSignUpService;

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
		OAuthTokenResponse registerResponse = accountSignUpService.signUp(registerRequest, request, locale);
		return ResponseEntity.ok(new ApiResponse<>(registerResponse));
	}

	@ApiOperation(value = "이메일 인증 API")
	@PostMapping("/email")
	public ResponseEntity<ApiResponse<EmailAuthResponse>> emailAuthorizationRequestHandler(
		@RequestBody @Valid EmailAuthRequest emailAuthRequest, Locale locale, BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		EmailAuthResponse emailAuthResponse = accountSignUpService.emailAuthentication(emailAuthRequest, locale);
		return ResponseEntity.ok(new ApiResponse<>(emailAuthResponse));
	}

	@ApiOperation(value = "이메일 인증 코드 확인 API")
	@GetMapping("/verification")
	public ResponseEntity<ApiResponse<EmailVerificationResponse>> getRegisterSessionCodeRequestHandler(
		@RequestParam(name = "code") String code, @RequestParam("email") String email
	) {
		if (StringUtils.isEmpty(code) || StringUtils.isEmpty(email)) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		EmailVerificationResponse authCodeVerification = accountSignUpService.emailAuthCodeVerification(code, email);
		return ResponseEntity.ok(new ApiResponse<>(authCodeVerification));
	}

}
