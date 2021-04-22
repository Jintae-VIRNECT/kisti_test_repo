package com.virnect.uaa.domain.auth.account.error.exception;

import com.virnect.uaa.domain.auth.account.error.AuthenticationErrorCode;

public class AccountPasswordNotInitializedException extends RuntimeException {
	private final String sessionCode;
	private final String email;
	private final AuthenticationErrorCode error;

	public AccountPasswordNotInitializedException(AuthenticationErrorCode error, String sessionCode, String email) {
		this.error = error;
		this.sessionCode = sessionCode;
		this.email = email;
	}

	public AuthenticationErrorCode getError() {
		return error;
	}

	public String getSessionCode() {
		return sessionCode;
	}

	public String getEmail() {
		return email;
	}
}
