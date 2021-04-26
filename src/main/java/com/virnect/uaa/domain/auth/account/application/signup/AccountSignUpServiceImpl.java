package com.virnect.uaa.domain.auth.account.application.signup;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.dto.request.EmailAuthRequest;
import com.virnect.uaa.domain.auth.account.dto.request.RegisterRequest;
import com.virnect.uaa.domain.auth.account.dto.response.EmailAuthenticationResponse;
import com.virnect.uaa.domain.auth.account.dto.response.EmailVerificationResponse;
import com.virnect.uaa.domain.auth.account.dto.response.OAuthTokenResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSignUpServiceImpl implements AccountSignUpService {

	@Override
	public OAuthTokenResponse signUp(
		RegisterRequest registerRequest,
		HttpServletRequest request,
		Locale locale
	) {
		return null;
	}

	@Override
	public EmailAuthenticationResponse emailAuthentication(
		EmailAuthRequest emailAuthRequest,
		Locale locale
	) {
		return null;
	}

	@Override
	public EmailVerificationResponse emailAuthenticationCodeVerification(
		String email,
		String code
	) {
		return null;
	}
}
