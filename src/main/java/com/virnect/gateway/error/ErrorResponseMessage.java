package com.virnect.gateway.error;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErrorResponseMessage {
	private int code;
	private String message;
	private Map<String, Object> data;

	public ErrorResponseMessage(final ErrorCode error) {
		this.code = error.getCode();
		this.message = error.getMessage();
		data = new HashMap<>();
	}
}
