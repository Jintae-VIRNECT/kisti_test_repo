package com.virnect.process.global.error;

import com.virnect.process.exception.ProcessServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-09
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Global Exception Handler Class
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProcessServiceException.class)
    public ResponseEntity<ErrorResponseMessage> processServiceExceptionHandler(ProcessServiceException e) {
        log.error("[PROCESS_SERVICE] - MESSAGE: [{}] , DATA: [{}]", e.getMessage(), e.getError().toString());
        return ResponseEntity.ok(new ErrorResponseMessage(e.getError()));
    }
}
