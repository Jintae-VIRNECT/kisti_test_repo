package com.virnect.content.global.websocket;

import java.nio.ByteBuffer;
import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompDecoder;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomWebSocketHandlerDecoratorFactory implements WebSocketHandlerDecoratorFactory {
	@Override
	public WebSocketHandler decorate(WebSocketHandler handler) {
		return new WebSocketHandlerDecorator(handler) {
			@Override
			public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
				log.info("[WEBSOCKET_MESSAGE] CONNECTION SUCCESS! SESSION_ID : {}", session.getId());
				super.afterConnectionEstablished(session);
			}

			@Override
			public void handleMessage(WebSocketSession session, WebSocketMessage<?> webSocketMessage) throws Exception {
				if (webSocketMessage instanceof TextMessage) {
					ByteBuffer byteBuffer = ByteBuffer.wrap(((TextMessage)webSocketMessage).asBytes());
					List<Message<byte[]>> messages = new StompDecoder().decode(byteBuffer);
					for (Message<byte[]> message : messages) {
						StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
							message, StompHeaderAccessor.class);
						log.info("[WEBSOCKET_MESSAGE] COMMAND : {}, SESSION_ID : {}, PAYLOAD LENGTH : {}",
							accessor.getCommand(), session.getId(), webSocketMessage.getPayloadLength()
						);
					}
				}
				super.handleMessage(session, webSocketMessage);
			}

			@Override
			public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
				log.info("[WEBSOCKET_MESSAGE] CONNECTION CLOSED! SESSION_ID : {}, STATUS : {}", session.getId(),
					closeStatus
				);
				super.afterConnectionClosed(session, closeStatus);
			}
		};
	}

}
