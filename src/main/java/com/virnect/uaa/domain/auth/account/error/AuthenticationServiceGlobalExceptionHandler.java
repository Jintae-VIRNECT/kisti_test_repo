package com.virnect.uaa.domain.auth.account.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.error.exception.AccountPasswordNotInitializedException;
import com.virnect.uaa.domain.auth.account.error.exception.DeviceAuthenticationServiceException;
import com.virnect.uaa.domain.auth.account.error.exception.LoginFailException;
import com.virnect.uaa.domain.auth.account.error.exception.UserAuthenticationServiceException;
import com.virnect.uaa.global.common.ErrorResponseMessage;

/**
 * @author jeonghyeon.chang (johnmark)
 * @email practice1356@gmail.com
 * @since 2020.03.01
 */

@Slf4j
@ControllerAdvice(basePackages = {"com.virnect.uaa.domain.auth"})
public class AuthenticationServiceGlobalExceptionHandler {
	@ExceptionHandler(UserAuthenticationServiceException.class)
	public ResponseEntity<ErrorResponseMessage> userAuthenticationServiceExceptionHandler(
		UserAuthenticationServiceException e
	) {
		log.error("[ERR_USER_AUTHENTICATION_SERVICE] - {}", e.getError());
		return ResponseEntity.ok(ErrorResponseMessage.parseError(e.getError()));
	}

	@ExceptionHandler(LoginFailException.class)
	public ResponseEntity<ErrorResponseMessage> loginFailureExceptionHandler(LoginFailException e) {
		ErrorResponseMessage errorResponseMessage = ErrorResponseMessage.parseError(e.getError());
		errorResponseMessage.getData().put("failCount", e.getFailCount());
		return ResponseEntity.ok(errorResponseMessage);
	}

	@ExceptionHandler(DeviceAuthenticationServiceException.class)
	public ResponseEntity<ErrorResponseMessage> deviceAuthenticationServiceExceptionHandler(
		DeviceAuthenticationServiceException e
	) {
		log.error("[ERR_DEVICE_AUTHENTICATION_SERVICE] - {}", e.getError());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("apiName", "/auth/app");
		headers.set("encrypt", "false");
		return ResponseEntity.ok().headers(headers).body(ErrorResponseMessage.parseError(e.getError()));
	}

	@ExceptionHandler({AccountPasswordNotInitializedException.class})
	public ResponseEntity<ErrorResponseMessage> accountPasswordInitializedResponseResponseEntity(
		AccountPasswordNotInitializedException e
	) {
		ErrorResponseMessage errorResponseMessage = ErrorResponseMessage.parseError(e.getError());
		errorResponseMessage.getData().put("sessionCode", e.getSessionCode());
		errorResponseMessage.getData().put("email", e.getEmail());
		return ResponseEntity.ok(errorResponseMessage);
	}
}
