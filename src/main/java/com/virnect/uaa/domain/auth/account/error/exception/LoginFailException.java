package com.virnect.uaa.domain.auth.account.error.exception;

import com.virnect.uaa.domain.auth.account.error.AuthenticationErrorCode;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description Login Fail Exception Class
 * @since 2020.05.08
 */

public class LoginFailException extends RuntimeException {
	private final AuthenticationErrorCode error;
	private final int failCount;

	public LoginFailException(AuthenticationErrorCode error, int failCount) {
		super(error.getMessage());
		this.error = error;
		this.failCount = failCount;
	}

	public AuthenticationErrorCode getError() {
		return error;
	}

	public int getFailCount() {
		return failCount;
	}
}
