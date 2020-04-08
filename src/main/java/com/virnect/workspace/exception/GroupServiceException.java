package com.virnect.workspace.exception;

import com.virnect.workspace.global.error.ErrorCode;

/**
 * Project: PF-Workspace
 * DATE: 2020-03-05
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class GroupServiceException extends RuntimeException {

    private ErrorCode errorCode;

    public GroupServiceException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
