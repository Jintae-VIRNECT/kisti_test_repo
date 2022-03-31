package com.virnect.download.global.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

import com.virnect.download.exception.AppServiceException;
import com.virnect.download.exception.DownloadException;

/**
 * Project: PF-Admin
 * DATE: 2020-02-20
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
		log.error("globalException. ", e);
		return new ResponseEntity<>(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DownloadException.class)
	protected ResponseEntity<ErrorResponseMessage> handleDownloadException(final DownloadException e) {
		log.error("handleDownloadException. {}", e.getMessage());
		return ResponseEntity.ok(new ErrorResponseMessage(e.getErrorCode()));
	}

	@ExceptionHandler(AppServiceException.class)
	protected ResponseEntity<ErrorResponseMessage> handleUploadServiceException(final AppServiceException e) {
		log.error("handleAppServiceException. {}", e);
		return ResponseEntity.ok(new ErrorResponseMessage(e.getErrorCode()));
	}
}
