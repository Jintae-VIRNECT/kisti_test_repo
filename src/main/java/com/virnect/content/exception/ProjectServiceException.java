package com.virnect.content.exception;

import com.virnect.content.global.error.ErrorCode;
import com.virnect.content.global.error.exception.BusinessException;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-23
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class ProjectServiceException extends BusinessException {
	private final ErrorCode errorCode;

	public ProjectServiceException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public ErrorCode getError() {
		return errorCode;
	}
}
