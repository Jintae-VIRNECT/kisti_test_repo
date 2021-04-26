package com.virnect.uaa.domain.auth.account.application.signin;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.dto.request.OTPLoginRequest;
import com.virnect.uaa.domain.auth.account.dto.request.OTPQRGenerateRequest;
import com.virnect.uaa.domain.auth.account.dto.response.OAuthTokenResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OTPQRGenerateResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountOtpSIgnInServiceImpl implements AccountOtpSignInService{

	@Override
	public OTPQRGenerateResponse loginQrCodeGenerate(
		OTPQRGenerateRequest otpQrCodeGenerateRequest
	) {
		return null;
	}

	@Override
	public OAuthTokenResponse qrCodeLogin(
		OTPLoginRequest otpLoginRequest,
		HttpServletRequest request
	) {
		return null;
	}
}
