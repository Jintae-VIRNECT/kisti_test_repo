package com.virnect.content.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;

import com.virnect.content.global.websocket.HandShakeInterceptor;
import com.virnect.content.global.websocket.StompChannelInterceptor;
import com.virnect.content.global.websocket.StompErrorHandler;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
	private final StompChannelInterceptor stompChannelInterceptor;
	private final StompErrorHandler stompErrorHandler;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.setErrorHandler(stompErrorHandler)
			.addEndpoint("/websocket")
			.setAllowedOrigins("*")
			.addInterceptors(new HandShakeInterceptor());
		registry.setErrorHandler(stompErrorHandler)
			.addEndpoint("/websocket")
			.addInterceptors(new HandShakeInterceptor())
			.withSockJS();

	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/topic");
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(stompChannelInterceptor);
	}
}
