package com.virnect.uaa.global.config.rememberme;

import java.util.Date;

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.dao.RememberMeTokenRepository;
import com.virnect.uaa.domain.auth.account.domain.RememberMeToken;

/**
 * Project: PF-Auth
 * DATE: 2021-03-04
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@RequiredArgsConstructor
public class CustomPersistentTokenRepository implements PersistentTokenRepository {
	private final RememberMeTokenRepository rememberMeTokenRepository;

	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		RememberMeToken rememberToken = new RememberMeToken(
			token.getUsername(), token.getSeries(), token.getTokenValue(), token.getDate());
		rememberMeTokenRepository.save(rememberToken);
	}

	@Override
	public void updateToken(String series, String tokenValue, Date date) {
		RememberMeToken rememberMeToken = rememberMeTokenRepository.findById(series).orElseThrow(() ->
			new RememberMeAuthenticationException(
				"Update RememberMe Token - remember me token not found by series [" + series + "]"));
		rememberMeToken.setTokenValue(tokenValue);
		rememberMeToken.setDate(date);
		rememberMeTokenRepository.save(rememberMeToken);
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String series) {
		RememberMeToken rememberMeToken = rememberMeTokenRepository.findById(series).orElseThrow(() ->
			new RememberMeAuthenticationException(
				"Get Token For Series - remember me token not found by series [" + series + "]"));
		return new PersistentRememberMeToken(
			rememberMeToken.getUsername()
			, series
			, rememberMeToken.getTokenValue()
			, rememberMeToken.getDate());
	}

	@Override
	public void removeUserTokens(String username) {
		rememberMeTokenRepository.findAll().forEach(rememberMeToken -> {
			if (username.equals(rememberMeToken.getUsername())) {
				rememberMeTokenRepository.delete(rememberMeToken);
			}
		});
	}
}
