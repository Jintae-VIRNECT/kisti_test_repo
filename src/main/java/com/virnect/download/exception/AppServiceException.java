package com.virnect.download.exception;

import com.virnect.download.global.error.ErrorCode;

/**
 * Project: PF-Download
 * DATE: 2020-08-06
 * AUTHOR: jeonghyeon.chang (johnmark)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public class AppServiceException extends RuntimeException {

    private ErrorCode errorCode;

    public AppServiceException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
