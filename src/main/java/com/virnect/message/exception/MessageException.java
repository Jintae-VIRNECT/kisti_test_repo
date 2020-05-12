package com.virnect.message.exception;

import com.virnect.message.global.error.ErrorCode;

/**
 * Project: PF-Message
 * DATE: 2020-02-12
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class MessageException extends RuntimeException {

    private ErrorCode errorCode;

    public MessageException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
