package com.virnect.gateway.filter.security.session;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionNotFoundException extends RuntimeException {
	private String message;

	public SessionNotFoundException(String message) {
		super(message);
		this.message = message;
	}

	@Override
	public String toString() {
		return "SessionNotFoundException{" +
			"message='" + message + '\'' +
			'}';
	}
}
