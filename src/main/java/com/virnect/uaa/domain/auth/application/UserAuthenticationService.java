package com.virnect.uaa.domain.auth.application;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.virnect.uaa.domain.auth.dto.user.request.EmailAuthRequest;
import com.virnect.uaa.domain.auth.dto.user.request.LoginRequest;
import com.virnect.uaa.domain.auth.dto.user.request.LogoutRequest;
import com.virnect.uaa.domain.auth.dto.user.request.OTPLoginRequest;
import com.virnect.uaa.domain.auth.dto.user.request.OTPQRGenerateRequest;
import com.virnect.uaa.domain.auth.dto.user.request.RegisterRequest;
import com.virnect.uaa.domain.auth.dto.user.request.TokenRefreshRequest;
import com.virnect.uaa.domain.auth.dto.user.response.EmailAuthenticationResponse;
import com.virnect.uaa.domain.auth.dto.user.response.EmailVerificationResult;
import com.virnect.uaa.domain.auth.dto.user.response.LogoutResponse;
import com.virnect.uaa.domain.auth.dto.user.response.OAuthTokenResponse;
import com.virnect.uaa.domain.auth.dto.user.response.OTPQRGenerateResponse;
import com.virnect.uaa.domain.auth.dto.user.response.RefreshTokenResponse;

public interface UserAuthenticationService {
	// 회원가입
	OAuthTokenResponse register(RegisterRequest registerRequest, HttpServletRequest request, Locale locale);

	// 로그인
	OAuthTokenResponse loginHandler(
		LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response
	);

	// 로그아웃
	LogoutResponse logout(HttpServletRequest request, HttpServletResponse response, LogoutRequest logoutRequest);

	// 이메일 인증
	EmailAuthenticationResponse emailAuthorization(EmailAuthRequest emailAuthRequest, Locale locale);

	// 이메일 인증 코드 확인
	EmailVerificationResult emailVerificationCodeCheck(String code, String email);

	// QR 로그인용 QR 코드 발급
	OTPQRGenerateResponse otpQRCodeGenerate(OTPQRGenerateRequest otpQRGenerateRequest);

	// QR 로그인
	OAuthTokenResponse otpLoginHandler(OTPLoginRequest otpLoginRequest, HttpServletRequest request);

	// 인증 토큰 재발급
	RefreshTokenResponse generateNewAccessToken(TokenRefreshRequest tokenRefreshRequest, HttpServletRequest request);
}
