package com.virnect.content.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import lombok.RequiredArgsConstructor;

import com.virnect.content.global.websocket.CustomWebSocketHandlerDecoratorFactory;
import com.virnect.content.global.websocket.HandShakeInterceptor;
import com.virnect.content.global.websocket.StompErrorHandler;
import com.virnect.content.global.websocket.StompInboundInterceptor;
import com.virnect.content.global.websocket.StompOutboundInterceptor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
	private final StompInboundInterceptor stompInboundInterceptor;
	private final StompErrorHandler stompErrorHandler;
	private final CustomWebSocketHandlerDecoratorFactory webSocketHandlerDecoratorFactory;
	private final StompOutboundInterceptor stompOutboundInterceptor;

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
		registry.setApplicationDestinationPrefixes("/topic").setPathMatcher(new AntPathMatcher("."));
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(stompInboundInterceptor);
	}

	@Override
	public void configureClientOutboundChannel(ChannelRegistration registration) {
		registration.interceptors(stompOutboundInterceptor);
	}

	@Override
	public void configureWebSocketTransport(
		WebSocketTransportRegistration registry
	) {
		registry.setMessageSizeLimit(1024 * 1024);
		registry.setSendTimeLimit(60 * 10000);
		registry.setSendBufferSizeLimit(1024 * 1024);
		registry.setDecoratorFactories(webSocketHandlerDecoratorFactory);
	}


	@Bean
	public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
		ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
		container.setMaxTextMessageBufferSize(1048576); //(byte).1mb.
		return container;
	}
}
