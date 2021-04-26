package com.virnect.uaa.domain.auth.account.application.signup;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.virnect.uaa.domain.auth.account.dto.request.EmailAuthRequest;
import com.virnect.uaa.domain.auth.account.dto.request.RegisterRequest;
import com.virnect.uaa.domain.auth.account.dto.response.EmailAuthenticationResponse;
import com.virnect.uaa.domain.auth.account.dto.response.EmailVerificationResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OAuthTokenResponse;

public interface AccountSignUpService {

	OAuthTokenResponse signUp(RegisterRequest registerRequest, HttpServletRequest request, Locale locale);

	EmailAuthenticationResponse emailAuthentication(EmailAuthRequest emailAuthRequest, Locale locale);

	EmailVerificationResponse emailAuthenticationCodeVerification(String email, String code);
}
