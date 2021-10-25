package com.virnect.uaa.domain.auth.account.api;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.application.token.AccountTokenService;
import com.virnect.uaa.domain.auth.account.dto.request.TokenRefreshRequest;
import com.virnect.uaa.domain.auth.account.dto.response.RefreshTokenResponse;
import com.virnect.uaa.domain.auth.common.error.AuthenticationErrorCode;
import com.virnect.uaa.domain.auth.common.exception.UserAuthenticationServiceException;
import com.virnect.uaa.global.common.ApiResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@Api(value = "인증 토큰 관련 API 컨트롤러")
@RequestMapping("/auth")
public class AccountOAuthTokenController {
	private static final String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
	private final AccountTokenService accountTokenService;

	@ApiOperation(value = "인증 토큰 재발급")
	@PostMapping("/oauth/token")
	public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshTokenRequestHandler(
		@RequestBody @Valid TokenRefreshRequest tokenRefreshRequest, HttpServletRequest request, BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		RefreshTokenResponse responseMessage = accountTokenService.refreshAccessToken(
			tokenRefreshRequest, request);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}
}
