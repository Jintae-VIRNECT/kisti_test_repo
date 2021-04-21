package com.virnect.uaa.domain.auth.error.exception;

import com.virnect.uaa.domain.auth.error.AuthenticationErrorCode;

/**
 * @project: PF-Auth
 * @author: jeonghyeon.chang (johnmark)
 * @email: practice1356@gmail.com
 * @since: 2020.03.09
 * @description: Device Authentication Service Business RuntimeException Class
 */
public class DeviceAuthenticationServiceException extends RuntimeException {
	private final AuthenticationErrorCode error;

	public DeviceAuthenticationServiceException(AuthenticationErrorCode error) {
		super(error.getMessage());
		this.error = error;
	}

	public AuthenticationErrorCode getError() {
		return error;
	}
}
