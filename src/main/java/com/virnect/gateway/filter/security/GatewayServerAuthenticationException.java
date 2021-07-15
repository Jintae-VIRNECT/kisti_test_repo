package com.virnect.gateway.filter.security;

import lombok.Getter;
import lombok.Setter;

import com.virnect.gateway.error.ErrorCode;

@Getter
@Setter
public class GatewayServerAuthenticationException extends RuntimeException {
	private String message;

	public GatewayServerAuthenticationException(ErrorCode error) {
		super(error.getMessage());
		this.message = error.getMessage();
	}
}
