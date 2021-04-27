package com.virnect.uaa.global.common;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.error.AuthenticationErrorCode;
import com.virnect.uaa.domain.auth.account.error.exception.UserAuthenticationServiceException;

@Slf4j
@RequiredArgsConstructor
public class TotpQRCodeGenerator {
	private static final String TOTP_ISSUER = "VIRNECT";
	private final GoogleAuthenticator googleAuthenticator;
	private final QRCodeWriter qrCodeWriter = new QRCodeWriter();

	public String generateOtpAuthUrl(String email) {
		GoogleAuthenticatorKey totpAuthenticationKey = googleAuthenticator.createCredentials(email);
		log.info("OtpQRAuthenticator - generateOtpAuthUrl Done.");
		return GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(TOTP_ISSUER, email, totpAuthenticationKey);
	}

	public String generateQRCodeFromOtpAuthUrl(String otpAuthUrl) {
		try {
			log.info("OtpQRAuthenticator - OTP AUTH URL : [{}]", otpAuthUrl);
			BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthUrl, BarcodeFormat.QR_CODE, 500, 500);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(bitMatrix, "png", outputStream);
			return Base64.getEncoder().encodeToString(outputStream.toByteArray());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_OTP_QR_CODE_CREATE);
		}
	}

	public boolean totpLoginAuthentication(String email, int code) {
		return googleAuthenticator.authorizeUser(email, code);
	}
}
