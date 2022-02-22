package com.virnect.data.error.exception;

import com.virnect.data.error.ErrorCode;

public class RemoteServiceException extends RuntimeException {
	private final ErrorCode errorCode;

	public RemoteServiceException(ErrorCode error) {
		this.errorCode = error;
	}

	public ErrorCode getErrorCode() {
		return this.errorCode;
	}
}
