package com.virnect.uaa.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.warrenstrange.googleauth.GoogleAuthenticator;

import lombok.RequiredArgsConstructor;

import com.virnect.uaa.domain.auth.account.dao.CredentialRepository;
import com.virnect.uaa.global.common.TotpQRCodeGenerator;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.14
 */
@Configuration
@RequiredArgsConstructor
public class OTPAuthenticationConfiguration {
	private final CredentialRepository credentialRepository;

	@Bean
	public GoogleAuthenticator googleAuthenticator() {
		GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
		googleAuthenticator.setCredentialRepository(credentialRepository);
		return googleAuthenticator;
	}

	@Bean
	public TotpQRCodeGenerator totpQRCodeGenerator(GoogleAuthenticator googleAuthenticator) {
		return new TotpQRCodeGenerator(googleAuthenticator);
	}
}
