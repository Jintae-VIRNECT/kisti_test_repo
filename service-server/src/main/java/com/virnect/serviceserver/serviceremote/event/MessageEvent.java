package com.virnect.serviceserver.serviceremote.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

import com.virnect.data.dto.request.guest.EventRequest;

@Getter
public class MessageEvent extends ApplicationEvent {

	private String signalType;
	private EventRequest eventRequest;

	/**
	 * Create a new {@code ApplicationEvent}.
	 * @param source the object on which the event initially occurred or with
	 * which the event is associated (never {@code null})
	 */
	public MessageEvent(
		Object source,
		String signalType,
		EventRequest eventRequest
	) {
		super(source);
		this.signalType = signalType;
		this.eventRequest = eventRequest;
	}
}
