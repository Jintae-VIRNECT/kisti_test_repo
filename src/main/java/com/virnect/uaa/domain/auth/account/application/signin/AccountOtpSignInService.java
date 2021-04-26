package com.virnect.uaa.domain.auth.account.application.signin;

import javax.servlet.http.HttpServletRequest;

import com.virnect.uaa.domain.auth.account.dto.request.OTPLoginRequest;
import com.virnect.uaa.domain.auth.account.dto.request.OTPQRGenerateRequest;
import com.virnect.uaa.domain.auth.account.dto.response.OAuthTokenResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OTPQRGenerateResponse;

public interface AccountOtpSignInService {

	OTPQRGenerateResponse loginQrCodeGenerate(OTPQRGenerateRequest otpQrCodeGenerateRequest);

	OAuthTokenResponse qrCodeLogin(OTPLoginRequest otpLoginRequest, HttpServletRequest request);
}
