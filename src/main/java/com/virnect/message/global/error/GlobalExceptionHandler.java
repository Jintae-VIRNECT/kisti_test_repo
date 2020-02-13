package com.virnect.message.global.error;

import com.virnect.message.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Project: PF-Message
 * DATE: 2020-02-13
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity exception(Exception e) {
        log.error("Exception", e);
        e.printStackTrace();
        return ResponseEntity.ok(new ErrorResponseMessage(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponseMessage> handleBusinessException(final BusinessException e) {
        log.error("BusinessException", e);
        e.printStackTrace();
        return ResponseEntity.ok(new ErrorResponseMessage(e.getErrorCode()));
    }
}
