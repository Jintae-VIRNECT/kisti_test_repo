package com.virnect.content.global.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.exception.WebSocketException;
import com.virnect.content.global.error.ErrorCode;
import com.virnect.content.global.security.TokenProvider;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompChannelInterceptor implements ChannelInterceptor {
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	private final TokenProvider tokenProvider;

	@Override
	public Message<?> preSend(
		Message<?> message, MessageChannel channel
	) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

		MessageHeaders messageHeaders = accessor.getMessageHeaders();
		Object nativeHeaders = messageHeaders.get(NativeMessageHeaderAccessor.NATIVE_HEADERS);

		log.info(
			"[STOMP_MESSAGE] COMMAND : {}, SESSION_ID : {}, NATIVE_HEADERS : {}", accessor.getCommand(),
			accessor.getSessionId(), nativeHeaders
		);

		String token = tokenProvider.getTokenByStompHeader(accessor);

		if (StringUtils.isEmpty(token)) {
			log.error("Token is Empty. message : {}", message);
			throw new WebSocketException(ErrorCode.ERR_API_AUTHENTICATION.getMessage());
		}

		if (!tokenProvider.validateToken(token)) {
			log.error("Invalid token Error. message : {}", message);
			throw new WebSocketException(ErrorCode.ERR_API_AUTHENTICATION.getMessage());
		}

		return message;
	}

}
