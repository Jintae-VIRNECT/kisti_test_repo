package com.virnect.uaa.global.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.AuthenticationDetailsSource;

import com.virnect.security.UserAuthenticationDetails;

public class CommonAuthenticationDetailsSource
	implements AuthenticationDetailsSource<HttpServletRequest, UserAuthenticationDetails> {

	@Override
	public UserAuthenticationDetails buildDetails(HttpServletRequest request) {
		String remoteAddress = request.getRemoteAddr();
		HttpSession session = request.getSession(false);
		String sessionId = (session != null) ? session.getId() : null;
		return new UserAuthenticationDetails(remoteAddress, sessionId);
	}
}
