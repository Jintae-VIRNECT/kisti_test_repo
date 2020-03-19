package com.virnect.content.global.error;

import com.virnect.content.exception.ContentServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-09
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Global Exception Handler Class
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ContentServiceException.class)
    public ResponseEntity<ErrorResponseMessage> contentServiceExceptionHandler(ContentServiceException e) {
        log.error("[CONTENT_SERVICE] - MESSAGE: [{}] , DATA: [{}]", e.getMessage(), e.getError().toString());
        return ResponseEntity.ok(new ErrorResponseMessage(e.getError()));
    }
}
