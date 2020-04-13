package com.virnect.download.exception;

import com.virnect.download.global.error.ErrorCode;

/**
 * Project: base
 * DATE: 2020-01-07
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Custom Exception extends from RuntimeException class
 */
public class DownloadException extends RuntimeException {

    private ErrorCode errorCode;

    public DownloadException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
