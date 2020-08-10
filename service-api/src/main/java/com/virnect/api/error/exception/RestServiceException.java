package com.virnect.api.error.exception;

import com.virnect.api.error.ErrorCode;

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
