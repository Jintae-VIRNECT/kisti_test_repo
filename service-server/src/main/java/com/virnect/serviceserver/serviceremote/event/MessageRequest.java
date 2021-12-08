package com.virnect.serviceserver.serviceremote.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {

	private String sessionId;
	private String connectionId;
	private String signalType;
	private String message;

	@Builder
	public MessageRequest(String sessionId, String connectionId, String signalType, String message) {
		this.sessionId = sessionId;
		this.connectionId = connectionId;
		this.signalType = signalType;
		this.message = message;
	}
}
