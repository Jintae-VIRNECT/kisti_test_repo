package com.virnect.uaa.domain.user.error.exception;

import com.virnect.uaa.domain.user.error.UserAccountErrorCode;

/**
 * Project: user
 * DATE: 2020-01-13
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public class BusinessException extends RuntimeException {
	private final UserAccountErrorCode error;

	public BusinessException(String message, UserAccountErrorCode error) {
		super(message);
		this.error = error;
	}

	public BusinessException(UserAccountErrorCode error) {
		super(error.getMessage());
		this.error = error;
	}

	public UserAccountErrorCode getError() {
		return error;
	}
}
