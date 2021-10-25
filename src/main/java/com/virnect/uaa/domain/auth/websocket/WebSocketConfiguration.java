package com.virnect.uaa.domain.auth.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketConfigurer {
	private final WebSocketHandler webSocketHandler;

	@Override
	public void registerWebSocketHandlers(
		WebSocketHandlerRegistry registry
	) {
		registry.addHandler(webSocketHandler, "/auth/status")
			.setAllowedOrigins("*");
	}
}
