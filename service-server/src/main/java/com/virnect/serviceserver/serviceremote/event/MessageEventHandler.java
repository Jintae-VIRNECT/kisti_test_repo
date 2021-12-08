package com.virnect.serviceserver.serviceremote.event;

import java.util.Collections;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.serviceserver.serviceremote.application.ServiceSessionManager;

@Slf4j
@RequiredArgsConstructor
@Component
public class MessageEventHandler {

	private final ServiceSessionManager serviceSessionManager;

	@EventListener
	public void sendMessage(MessageEvent messageEvent) {
		JsonObject jsonObject = serviceSessionManager.generateMessage(
			messageEvent.getMessageRequest().getSessionId(),
			Collections.singletonList(messageEvent.getMessageRequest().getConnectionId()),
			messageEvent.getMessageRequest().getSignalType(),
			messageEvent.getMessageRequest().getMessage()
		);
		if (jsonObject.has("error")) {
			log.info("[SEND SIGNAL RESULT] : error({})", jsonObject.get("error").getAsString());
		}
	}

}
