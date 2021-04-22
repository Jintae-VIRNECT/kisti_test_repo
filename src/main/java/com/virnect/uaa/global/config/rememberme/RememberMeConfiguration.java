package com.virnect.uaa.global.config.rememberme;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.dao.RememberMeTokenRepository;
import com.virnect.uaa.global.security.user.UserDetailsServiceImpl;

/**
 * Project: PF-Auth
 * DATE: 2021-03-04
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RememberMeConfiguration {
	private final RememberMeCookieProperty rememberMeCookieProperty;
	private final RememberMeTokenRepository rememberMeTokenRepository;
	private final UserDetailsServiceImpl userDetailsService;

	@PostConstruct
	public void init() {
		log.info("RememberMe Cookie - Configuration Properties: [{}]", rememberMeCookieProperty);
	}

	@Bean
	public PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices() {
		PersistentTokenBasedRememberMeServices tokenBasedRememberMeServices = new PersistentTokenBasedRememberMeServices(
			rememberMeCookieProperty.getName(), userDetailsService, persistentTokenRepository()
		);
		tokenBasedRememberMeServices.setTokenValiditySeconds(rememberMeCookieProperty.getValidSeconds());
		tokenBasedRememberMeServices.setCookieName(rememberMeCookieProperty.getName());

		if (rememberMeCookieProperty.isDomainEnabled()) {
			tokenBasedRememberMeServices.setCookieDomain(rememberMeCookieProperty.getDomain());
		}

		tokenBasedRememberMeServices.setParameter(rememberMeCookieProperty.getRememberMeParameter());
		tokenBasedRememberMeServices.setUseSecureCookie(rememberMeCookieProperty.isSecureCookieEnabled());
		return tokenBasedRememberMeServices;
	}

	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		return new CustomPersistentTokenRepository(rememberMeTokenRepository);
	}
}
