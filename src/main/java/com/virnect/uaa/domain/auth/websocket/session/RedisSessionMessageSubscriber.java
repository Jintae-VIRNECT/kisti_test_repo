package com.virnect.uaa.domain.auth.websocket.session;

import static com.virnect.data.redis.domain.AccessType.*;
import static com.virnect.uaa.domain.auth.websocket.LogEventType.*;
import static com.virnect.uaa.domain.auth.websocket.WebSocketLogFormat.*;
import static com.virnect.uaa.domain.auth.websocket.message.WebSocketResponseCode.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.redis.domain.AccessStatus;
import com.virnect.data.redis.domain.ForceLogoutMessage;
import com.virnect.uaa.domain.auth.websocket.ClientSessionInfo;
import com.virnect.uaa.domain.auth.websocket.dao.AccessStatusRepository;
import com.virnect.uaa.domain.auth.websocket.message.WebSocketResponseMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSessionMessageSubscriber implements MessageListener {
	private final AccessStatusRepository accessStatusRepository;
	private final SessionManager<ClientSessionInfo> sessionManager;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String messages = redisTemplate.getStringSerializer().deserialize(message.getBody());

		log.info("[RedisEventSubscribe][force-logout] - {}", messages);

		ForceLogoutMessage forceLogoutMessage;
		try {
			forceLogoutMessage = objectMapper.readValue(messages, ForceLogoutMessage.class);
		} catch (Exception exception) {
			log.error(messages);
			log.error(exception.getMessage(), exception);
			return;
		}

		for (String targetUserId : forceLogoutMessage.getTargetUserIds()) {
			String forceLogoutUserAccessKey = forceLogoutMessage.getWorkspaceId() + "_" + targetUserId;
			log.info("[RedisEventSubscribe][force-logout][Target] - {}", forceLogoutUserAccessKey);

			// find target user web socket
			String sessionId = sessionManager.findUserCurrentSessionId(forceLogoutUserAccessKey);

			Optional<WebSocketSession> targetUserSessionInfo = sessionManager.findWebSocketSessionBySessionId(
				sessionId);

			if (!targetUserSessionInfo.isPresent()) {
				log.error(
					"[RedisEventSubscribe][force-logout][USER_SESSION_NOT_FOUND] - {}", forceLogoutUserAccessKey);
				Optional<AccessStatus> userAccessStatusInformation = accessStatusRepository.findById(
					forceLogoutUserAccessKey);

				if (userAccessStatusInformation.isPresent()) {
					AccessStatus accessStatus = userAccessStatusInformation.get();
					log.error(
						"[RedisEventSubscribe][force-logout][USER_SESSION_NOT_FOUND] - But Found AccessStatus {}",
						accessStatus
					);
					accessStatus.setAccessType(LOGOUT);
					accessStatusRepository.save(accessStatus);
					log.error(
						"[RedisEventSubscribe][force-logout][USER_SESSION_NOT_FOUND] - But Found AccessStatus And Update To {}",
						accessStatus
					);
				}
				continue;
			}

			try {
				WebSocketSession targetUserSession = targetUserSessionInfo.get();
				WebSocketResponseMessage forceLogoutResponse = forceLogoutResponse(
					forceLogoutMessage.getWorkspaceId(),
					targetUserId
				);

				String forceLogoutResponseMessage = messageConvertToText(forceLogoutResponse);
				targetUserSession.sendMessage(new TextMessage(forceLogoutResponseMessage));
				log.info(
					WEB_SOCKET_LOG_FORMAT, FORCE_LOGOUT_EVENT, sessionId,
					"Send Force Logout Message " + forceLogoutResponseMessage
				);
				targetUserSession.close();
			} catch (Exception exception) {
				log.info(WEB_SOCKET_LOG_FORMAT, FORCE_LOGOUT_EVENT, "NONE", exception.getMessage());
			}
		}
	}

	private String messageConvertToText(WebSocketResponseMessage webSocketResponseMessage) throws
		JsonProcessingException {
		return objectMapper.writeValueAsString(webSocketResponseMessage);
	}

	private WebSocketResponseMessage forceLogoutResponse(
		final String workspaceId, final String targetUserId
	) {
		// Create Socket Connect Success Response
		Map<String, Object> payload = new HashMap<>();
		payload.put("workspaceId", workspaceId);
		payload.put("userId", targetUserId);

		WebSocketResponseMessage message = WebSocketResponseMessage.of(CLIENT_FORCE_LOGOUT);
		message.setData(payload);
		return message;
	}
}
