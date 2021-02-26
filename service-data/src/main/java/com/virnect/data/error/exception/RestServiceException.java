package com.virnect.data.error.exception;

import com.virnect.data.error.ErrorCode;

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
