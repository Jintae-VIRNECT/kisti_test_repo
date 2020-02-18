package com.virnect.workspace.exception;

import com.virnect.workspace.global.error.ErrorCode;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-14
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class BusinessException extends RuntimeException {

    private ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
