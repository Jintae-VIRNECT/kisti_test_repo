package com.virnect.content.exception;

import com.virnect.content.global.error.ErrorCode;
import com.virnect.content.global.error.exception.BusinessException;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-22
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Content Service Exception Class
 */
public class ContentServiceException extends BusinessException {
    ErrorCode error;

    public ContentServiceException(ErrorCode error) {
        super(error);
        this.error = error;
    }

    public ErrorCode getError() {
        return error;
    }
}
