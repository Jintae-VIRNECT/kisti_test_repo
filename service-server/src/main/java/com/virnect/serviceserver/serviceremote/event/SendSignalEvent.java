package com.virnect.serviceserver.serviceremote.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

import com.virnect.data.dto.push.SendSignalRequest;

@Getter
public class SendSignalEvent extends ApplicationEvent {

	private SendSignalRequest sendSignalRequest;

	/**
	 * Create a new {@code ApplicationEvent}.
	 * @param source the object on which the event initially occurred or with
	 * which the event is associated (never {@code null})
	 */
	public SendSignalEvent(Object source, SendSignalRequest sendSignalRequest) {
		super(source);
		this.sendSignalRequest = sendSignalRequest;
	}
}
