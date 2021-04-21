package com.virnect.uaa.domain.user.error.exception;

import com.virnect.user.global.error.ErrorCode;

/**
 * Project: user
 * DATE: 2020-01-13
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public class InvalidRequestParameter extends BusinessException {
	public InvalidRequestParameter(String message) {
		super(message, ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
	}

	public InvalidRequestParameter(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
