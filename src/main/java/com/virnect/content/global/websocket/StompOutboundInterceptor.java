package com.virnect.content.global.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StompOutboundInterceptor implements ChannelInterceptor {
	@Override
	public Message<?> preSend(
		Message<?> message, MessageChannel channel
	) {
		SimpMessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
			message, SimpMessageHeaderAccessor.class);

		MessageHeaders messageHeaders = accessor.getMessageHeaders();
		Object nativeHeaders = messageHeaders.get(NativeMessageHeaderAccessor.NATIVE_HEADERS);

		log.info(
			"[OUTBOUND][STOMP_MESSAGE] COMMAND : {}, SESSION_ID : {}, NATIVE_HEADERS : {}", accessor.getMessageType(),
			accessor.getSessionId(), nativeHeaders
		);
		return ChannelInterceptor.super.preSend(message, channel);
	}
}
