package com.virnect.uaa.domain.user.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.user.exception.UserServiceException;
import com.virnect.uaa.global.common.ErrorResponseMessage;

/**
 * Project: PF-User
 * DATE: 2021-04-22
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: User Service Global Exception Handler
 */

@Slf4j
@ControllerAdvice(basePackages = {"com.virnect.uaa.domain.user"})
public class UserServiceGlobalExceptionHandler {
	@ExceptionHandler(UserServiceException.class)
	public ResponseEntity<ErrorResponseMessage> userServiceException(UserServiceException e) {
		log.error("[USER_SERVICE - EXCEPTION] - MESSAGE: [{}] , DATA: [{}]", e.getMessage(), e.getError());
		return ResponseEntity.ok(ErrorResponseMessage.parseError(e.getError()));
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorResponseMessage> maxUploadSizeExceededException(MaxUploadSizeExceededException e) {
		log.error(e.getMessage());
		log.error("프로필 이미지 파일 최대 용량 에러");
		return ResponseEntity.ok(
			ErrorResponseMessage.parseError(UserAccountErrorCode.ERR_USER_PROFILE_IMAGE_SIZE_LIMIT));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseMessage> generalExceptionHandler(Exception e) {
		log.error("GLOBAL EXCEPTION: ", e);
		return ResponseEntity.ok(ErrorResponseMessage.parseError(UserAccountErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
	}
}
