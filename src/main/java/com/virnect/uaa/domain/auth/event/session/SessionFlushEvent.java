package com.virnect.uaa.domain.auth.event.session;

import com.virnect.security.UserDetailsImpl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.session.SessionInformation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
