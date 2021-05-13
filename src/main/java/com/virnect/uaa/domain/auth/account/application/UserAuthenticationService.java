package com.virnect.uaa.domain.auth.account.application;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.virnect.uaa.domain.auth.account.dto.request.EmailAuthRequest;
import com.virnect.uaa.domain.auth.account.dto.request.LoginRequest;
import com.virnect.uaa.domain.auth.account.dto.request.LogoutRequest;
import com.virnect.uaa.domain.auth.account.dto.request.OTPLoginRequest;
import com.virnect.uaa.domain.auth.account.dto.request.OTPQRGenerateRequest;
import com.virnect.uaa.domain.auth.account.dto.request.RegisterRequest;
import com.virnect.uaa.domain.auth.account.dto.request.TokenRefreshRequest;
import com.virnect.uaa.domain.auth.account.dto.response.EmailAuthResponse;
import com.virnect.uaa.domain.auth.account.dto.response.EmailVerificationResponse;
import com.virnect.uaa.domain.auth.account.dto.response.LogoutResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OAuthTokenResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OTPQRGenerateResponse;
import com.virnect.uaa.domain.auth.account.dto.response.RefreshTokenResponse;

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
	EmailAuthResponse emailAuthorization(EmailAuthRequest emailAuthRequest, Locale locale);

	// 이메일 인증 코드 확인
	EmailVerificationResponse emailVerificationCodeCheck(String code, String email);

	// QR 로그인용 QR 코드 발급
	OTPQRGenerateResponse otpQRCodeGenerate(OTPQRGenerateRequest otpQRGenerateRequest);

	// QR 로그인
	OAuthTokenResponse otpLoginHandler(OTPLoginRequest otpLoginRequest, HttpServletRequest request);

	// 인증 토큰 재발급
	RefreshTokenResponse generateNewAccessToken(TokenRefreshRequest tokenRefreshRequest, HttpServletRequest request);
}
