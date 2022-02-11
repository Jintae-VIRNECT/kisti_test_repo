package com.virnect.content.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import com.virnect.content.global.security.TokenProvider;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({TestRabbitmqConfiguration.class, TestRedisConfiguration.class})
@DirtiesContext(classMode = BEFORE_CLASS)
class WebSocketControllerTest {
	@LocalServerPort
	private int port;
	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private TokenProvider tokenProvider;

	@Value("${security.jwt-config.secret}")
	private String secret;

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

		StompHeaders stompHeaders = new StompHeaders();
		String token = this.createAccessToken();

		stompHeaders.add("Authorization", "Bearer " + token);
		assertDoesNotThrow(() -> {
			StompSession stompSession = stompClient.connect(
					connectUrl, new WebSocketHttpHeaders(), stompHeaders, new StompSessionHandler())
				.get(1, TimeUnit.SECONDS);
			stompSession.send(destination, payload);
		});

		String value = redisTemplate.opsForValue().get("liveShare:" + roomId);
		assertEquals(value, payload);
	}

	private String createAccessToken() {
		Date now = new Date();
		Date expireDate = new Date((now.getTime() + 3900));

		return Jwts.builder()
			.setSubject("userName")
			.claim("userId", "userId")
			.claim("uuid", "uuid")
			.claim("name", "name")
			.claim("authorities", new ArrayList<>())
			.claim("jwtId", "jwtId")
			.setIssuedAt(now)
			.setExpiration(expireDate)
			.setIssuer("issuer")
			.signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret.getBytes()))
			.compact();
	}

	static class StompSessionHandler extends StompSessionHandlerAdapter {
	}

}
