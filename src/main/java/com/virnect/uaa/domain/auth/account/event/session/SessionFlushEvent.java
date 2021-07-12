package com.virnect.uaa.domain.auth.account.event.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.session.SessionInformation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.security.UserDetailsImpl;

/**
 * Project: PF-Auth
 * DATE: 2021-03-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class SessionFlushEvent {
	private final UserDetailsImpl userDetails;
	private final SessionInformation session;
	private final HttpServletRequest request;
	private final HttpServletResponse response;

	@Override
	public String toString() {
		return "SessionFlushEvent{" +
			"session=" + session +
			'}';
	}
}
