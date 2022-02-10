package com.virnect.content.api;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({TestRabbitmqConfiguration.class, TestRedisConfiguration.class})
class WebSocketControllerTest {
	@LocalServerPort
	private int port;
	@Autowired
	private StringRedisTemplate redisTemplate;

	@Test
	@DisplayName("publish content write message")
	void publishContentWriteMessage() {
		StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
		WebSocketStompClient stompClient = new WebSocketStompClient(standardWebSocketClient);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		String contentUUID = "3ac931f7-5b3b-4807-ac6e-61ae5d138204";
		String roomId = "1";
		String destination = String.format("/topic/api/contents/%s/room/%s", contentUUID, roomId);
		String connectUrl = String.format("ws://localhost:%d/websocket", port);
		String payload = "payload";

		assertDoesNotThrow(() -> {
			StompSession stompSession = stompClient.connect(connectUrl, new StompSessionHandler())
				.get(1, TimeUnit.SECONDS);
			stompSession.send(destination, payload);
		});

		String value = redisTemplate.opsForValue().get("liveShare:" + roomId);
		assertEquals(value, payload);
	}

	static class StompSessionHandler extends StompSessionHandlerAdapter {
	}
}
