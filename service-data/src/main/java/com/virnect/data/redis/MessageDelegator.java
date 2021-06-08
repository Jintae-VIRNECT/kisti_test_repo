package com.virnect.data.redis;

import java.nio.charset.StandardCharsets;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.redis.application.AccessStatusService;
import com.virnect.data.redis.domain.AccessType;
import com.virnect.data.redis.domain.StatusMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageDelegator implements MessageListener {

	private final ObjectMapper objectMapper;
	private final AccessStatusService accessStatusService;
	private final RedisTemplate redisTemplate;

	private static final String LOGIN_STATUS = "Login";
	private static final String LOGOUT_STATUS = "Logout";

	@Override
	public void onMessage(Message message, byte[] pattern) {
		System.out.println(new String(message.getBody(), StandardCharsets.UTF_8));
		try {
			String body = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
			StatusMessage statusMessage = objectMapper.readValue(body, StatusMessage.class);
			switch (statusMessage.getStatus()) {
				case LOGIN_STATUS :
					log.info("[REDIS:POST] login uuid : " + statusMessage.getUserUUID());
					accessStatusService.saveAccessStatus(
						statusMessage.getWorkspaceId() + "_" + statusMessage.getUserUUID(),
						AccessType.LOGIN,
						statusMessage.getUserUUID()
					);
					break;
				case LOGOUT_STATUS :
					log.info("[REDIS:POST] logout uuid : " + statusMessage.getUserUUID());
					accessStatusService.saveAccessStatus(
						statusMessage.getWorkspaceId() + "_" + statusMessage.getUserUUID(),
						AccessType.LOGOUT,
						statusMessage.getUserUUID()
					);
					break;
			}
		} catch (Exception e) {
			log.info("[ERROR]" + e.getMessage());
		}
	}
}


