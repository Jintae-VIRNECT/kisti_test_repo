package com.virnect.login.exception;

import com.virnect.login.global.ErrorCode;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Login
 * @email practice1356@gmail.com
 * @description Account Service business exception cass
 * @since 2020.03.20
 */
public class AccountServiceException extends RuntimeException {
    private ErrorCode error;

    public AccountServiceException(ErrorCode error) {
        super(error.getMessage());
        this.error = error;
    }

    public ErrorCode getError() {
        return error;
    }
}
