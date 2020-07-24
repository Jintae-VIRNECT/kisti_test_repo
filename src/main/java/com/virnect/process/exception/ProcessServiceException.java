package com.virnect.process.exception;

import com.virnect.process.global.error.ErrorCode;
import com.virnect.process.global.error.exception.BusinessException;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-22
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Process server Business Exception
 */
public class ProcessServiceException extends BusinessException {
    private ErrorCode error;

    public ProcessServiceException(ErrorCode error) {
        super(error);
        this.error = error;
    }

    public ErrorCode getError() {
        return this.error;
    }
}
