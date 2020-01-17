package com.virnect.workspace.exception;

import com.virnect.workspace.global.error.ErrorCode;
import com.virnect.workspace.global.error.exception.BusinessException;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-14
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class InvalidParamsException extends BusinessException {
    public InvalidParamsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
