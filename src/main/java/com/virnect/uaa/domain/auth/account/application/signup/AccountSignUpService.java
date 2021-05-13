package com.virnect.uaa.domain.auth.account.application.signup;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.virnect.uaa.domain.auth.account.dto.request.EmailAuthRequest;
import com.virnect.uaa.domain.auth.account.dto.request.RegisterRequest;
import com.virnect.uaa.domain.auth.account.dto.response.EmailAuthResponse;
import com.virnect.uaa.domain.auth.account.dto.response.EmailVerificationResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OAuthTokenResponse;

public interface AccountSignUpService {

	OAuthTokenResponse signUp(RegisterRequest registerRequest, HttpServletRequest request, Locale locale);

	EmailAuthResponse emailAuthentication(EmailAuthRequest emailAuthRequest, Locale locale);

	EmailVerificationResponse emailAuthCodeVerification(String email, String code);
}
