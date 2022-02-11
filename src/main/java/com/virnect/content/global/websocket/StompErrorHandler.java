package com.virnect.content.global.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import lombok.extern.slf4j.Slf4j;

import com.virnect.content.exception.WebSocketException;

@Component
@Slf4j
public class StompErrorHandler extends StompSubProtocolErrorHandler {

	public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
		if (ex instanceof WebSocketException) {
			return handleContentServiceException(clientMessage, ex);
		}
		return super.handleClientMessageProcessingError(clientMessage, ex);
	}

	private Message<byte[]> handleContentServiceException(Message<byte[]> clientMessage, Throwable ex) {
		log.info("Stomp Client Message : {}", clientMessage.toString());
		StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
		return MessageBuilder.createMessage(ex.getMessage().getBytes(), accessor.getMessageHeaders());
	}
}
