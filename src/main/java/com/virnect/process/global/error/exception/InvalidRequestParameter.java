package com.virnect.process.global.error.exception;

import com.virnect.process.global.error.ErrorCode;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-15
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
