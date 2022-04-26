package com.virnect.content.exception;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

public class WebSocketException extends MessagingException {
	public WebSocketException(Message<?> message) {
		super(message);
	}

	public WebSocketException(String description) {
		super(description);
	}

	public WebSocketException(String description, Throwable cause) {
		super(description, cause);
	}

	public WebSocketException(Message<?> message, String description) {
		super(message, description);
	}

	public WebSocketException(Message<?> message, Throwable cause) {
		super(message, cause);
	}

	public WebSocketException(Message<?> message, String description, Throwable cause) {
		super(message, description, cause);
	}
}
