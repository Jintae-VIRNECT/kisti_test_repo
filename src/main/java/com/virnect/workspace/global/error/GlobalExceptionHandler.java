package com.virnect.workspace.global.error;

import com.virnect.workspace.global.common.ResponseMessage;
import com.virnect.workspace.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-09
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 기타 모든 예외 에러
    @ExceptionHandler(Exception.class)
    public ResponseEntity exception(Exception e) {
        log.error(e.getMessage());
        e.printStackTrace();
        return new ResponseEntity<>(ErrorCode.SOME_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponseMessage> handleBusinessException(final BusinessException e) {
        log.error("handleBusinessException", e);
        return ResponseEntity.ok(new ErrorResponseMessage(ErrorCode.SOME_ERROR_MESSAGE));
    }
}
