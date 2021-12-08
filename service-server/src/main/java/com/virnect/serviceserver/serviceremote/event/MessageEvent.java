package com.virnect.serviceserver.serviceremote.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class MessageEvent extends ApplicationEvent {

	private MessageRequest messageRequest;

	/**
	 * Create a new {@code ApplicationEvent}.
	 * @param source the object on which the event initially occurred or with
	 * which the event is associated (never {@code null})
	 */
	public MessageEvent(
		Object source,
		MessageRequest messageRequest
	) {
		super(source);
		this.messageRequest = messageRequest;
	}
}
