package com.virnect.uaa.domain.auth.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import com.virnect.uaa.domain.auth.websocket.dao.AccessStatusRepository;
import com.virnect.uaa.global.config.redis.RedisPublisher;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketConfigurer {
	private final RedisPublisher redisPublisher;
	private final RedisTemplate<String, Object> redisTemplate;
	private final AccessStatusRepository accessStatusRepository;

	@Override
	public void registerWebSocketHandlers(
		WebSocketHandlerRegistry registry
	) {
		registry.addHandler(webSocketHandler(redisPublisher, accessStatusRepository), "/auth/status")
			.setAllowedOrigins("*");
	}

	@Bean
	public WebSocketHandler webSocketHandler(
		RedisPublisher redisPublisher, AccessStatusRepository accessStatusRepository
	) {
		ObjectMapper objectMapper = new ObjectMapper();
		return new WebSocketHandler(objectMapper, redisPublisher, redisTemplate, accessStatusRepository);
	}
}
