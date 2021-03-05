package com.virnect.serviceserver.global.config;

import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/*@Configuration
@EnableWebSocketMessageBroker*/
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    /*@Bean
    public WebSocketClient webSocketClient() {
        final WebSocketClient client = new StandardWebSocketClient();

        final WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        final StompSessionHandler sessionHandler = new MyStompSessionHandler();
        stompClient.connect("ws://localhost:8080", sessionHandler);
        return client;
    }*/

	/*@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws").withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app");
		registry.enableSimpleBroker("/topic");
	}*/
}
