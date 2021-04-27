package com.virnect.uaa.domain.auth.account.application.signin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.virnect.uaa.domain.auth.account.dto.request.LoginRequest;
import com.virnect.uaa.domain.auth.account.dto.request.LogoutRequest;
import com.virnect.uaa.domain.auth.account.dto.request.OTPLoginRequest;
import com.virnect.uaa.domain.auth.account.dto.request.OTPQRGenerateRequest;
import com.virnect.uaa.domain.auth.account.dto.response.LogoutResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OAuthTokenResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OTPQRGenerateResponse;

public interface AccountSignInService {

	OAuthTokenResponse login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response);

	LogoutResponse logout(LogoutRequest logoutRequest, HttpServletRequest request, HttpServletResponse response);

	OTPQRGenerateResponse loginQrCodeGenerate(OTPQRGenerateRequest otpQrCodeGenerateRequest);

	OAuthTokenResponse qrCodeLogin(OTPLoginRequest otpLoginRequest, HttpServletRequest request);
}
