package com.virnect.service.error.exception;


import com.virnect.service.error.ErrorCode;

public class RestServiceException extends BusinessException {
    private ErrorCode error;

    public RestServiceException(ErrorCode error) {
        super(error);
        this.error = error;
    }

    public ErrorCode getError() {
        return this.error;
    }
}
