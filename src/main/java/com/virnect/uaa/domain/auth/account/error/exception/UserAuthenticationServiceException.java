package com.virnect.uaa.domain.auth.account.error.exception;

import com.virnect.uaa.domain.auth.account.error.AuthenticationErrorCode;

/**
 * @project: PF-Auth
 * @author: jeonghyeon.chang (johnmark)
 * @email: practice1356@gmail.com
 * @since: 2020.03.09
 * @description: User Authentication Service Business RuntimeException Class
 */
public class UserAuthenticationServiceException extends RuntimeException {
	private final AuthenticationErrorCode error;

	public UserAuthenticationServiceException(AuthenticationErrorCode error) {
		super(error.getMessage());
		this.error = error;
	}

	public AuthenticationErrorCode getError() {
		return error;
	}
}
