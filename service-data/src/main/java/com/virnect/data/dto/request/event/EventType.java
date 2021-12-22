package com.virnect.data.dto.request.event;

public enum EventType {

	DELETED_ACCOUNT("deletedAccount");

	private String message;

	EventType(final String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
