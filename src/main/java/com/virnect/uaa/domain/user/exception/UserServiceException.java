package com.virnect.uaa.domain.user.exception;

import com.virnect.uaa.domain.user.error.UserAccountErrorCode;
import com.virnect.uaa.domain.user.error.exception.BusinessException;

/**
 * Project: user
 * DATE: 2020-01-08
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public class UserServiceException extends BusinessException {
	private final UserAccountErrorCode error;

	public UserServiceException(UserAccountErrorCode error) {
		super(error);
		this.error = error;
	}

	public UserAccountErrorCode getError() {
		return this.error;
	}

}
