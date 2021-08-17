package com.virnect.uaa.domain.auth.websocket;

import static com.virnect.data.redis.domain.AccessType.*;
import static com.virnect.uaa.domain.auth.websocket.LogEventType.*;
import static com.virnect.uaa.domain.auth.websocket.WebSocketLogFormat.*;
import static com.virnect.uaa.domain.auth.websocket.message.WebSocketResponseCode.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.redis.domain.AccessStatus;
import com.virnect.data.redis.domain.StatusMessage;
import com.virnect.uaa.domain.auth.websocket.dao.AccessStatusRepository;
import com.virnect.uaa.domain.auth.websocket.exception.StatusMessageNotValidException;
import com.virnect.uaa.domain.auth.websocket.message.Command;
import com.virnect.uaa.domain.auth.websocket.message.LogoutMessage;
import com.virnect.uaa.domain.auth.websocket.message.WebSocketRequestMessage;
import com.virnect.uaa.domain.auth.websocket.message.WebSocketResponseMessage;
import com.virnect.uaa.domain.auth.websocket.message.WorkspaceChangeMessage;
import com.virnect.uaa.domain.auth.websocket.session.SessionManager;
import com.virnect.uaa.global.config.redis.RedisPublisher;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
	private static final ChannelTopic REDIS_CHANNEL = new ChannelTopic("rm-service");
	private static final String LOGIN_STATUS = "Login";
	private static final String LOGOUT_STATUS = "Logout";
	private static final String PONG_MESSAGE = "PONG";
	private static final long HEART_BEAT_TIME = 5000;
	private final SessionManager<ClientSessionInfo> sessionManager;
	private final ObjectMapper objectMapper;
	private final RedisPublisher redisPublisher;
	private final AccessStatusRepository accessStatusRepository;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info(WEB_SOCKET_LOG_FORMAT, CONNECT_EVENT, session.getId(), "New Socket Connect.");
		sessionManager.registerNewSession(session);

		WebSocketResponseMessage connectSuccessMessage = connectSuccessResponse(session.getId());
		String successResponseMessage = messageConvertToText(connectSuccessMessage);

		// Send Register Success Message
		session.sendMessage(new TextMessage(successResponseMessage));
		log.info(WEB_SOCKET_LOG_FORMAT, CONNECT_EVENT, session.getId(), "Connect Success. " + successResponseMessage);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// Check Pong Message
		if (message.getPayload().equals(PONG_MESSAGE)) {
			handlerPongMessage(session);
			return;
		}

		// Incoming Text Message Logging
		log.info(WEB_SOCKET_LOG_FORMAT, MESSAGE_HANDLE_EVENT, session.getId(), message.getPayload());

		// Parse Client Request Message
		WebSocketRequestMessage<Object> requestMessage = objectMapper.readValue(
			message.getPayload(), new TypeReference<WebSocketRequestMessage<Object>>() {
			}
		);

		if (requestMessage.getCommand() == Command.REGISTER) {
			handleRegisterCommand(session, requestMessage);
		} else if (requestMessage.getCommand() == Command.REMOTE_EXIT) {
			handleRemoteExitCommand(session, requestMessage);
		} else if (requestMessage.getCommand() == Command.WORKSPACE_UPDATE) {
			handleWorkspaceChangeCommand(session, requestMessage);
		}
	}

	private void handleWorkspaceChangeCommand(
		WebSocketSession session, WebSocketRequestMessage<Object> requestMessage
	) throws IOException {
		try {
			WorkspaceChangeMessage workspaceChangeMessage = objectMapper.convertValue(
				requestMessage.getData(), WorkspaceChangeMessage.class
			);

			// 1. found previous client information
			ClientSessionInfo clientSessionInfo = sessionManager.getSessionInformation(session);
			StatusMessage originStatusMessage = clientSessionInfo.getStatusMessage();
			String sessionId = sessionManager.findUserCurrentSessionId(originStatusMessage.getUserAccessStatusKey());

			// 2. check duplicate
			if (sessionManager.hasExistUserSessionId(workspaceChangeMessage.getUserAccessStatusKey())) {
				log.info(
					WEB_SOCKET_LOG_FORMAT, WORKSPACE_UPDATE, session.getId(),
					" Change Target Workspace Duplicate Session Exist"
				);
				// duplicate error response
				handleWorkspaceChangeDuplicateError(session, workspaceChangeMessage, originStatusMessage);
				return;
			}

			// 3. remove and logout previous session

			// 3.1 status change to logout
			userAccessStatusChangeToLogout(originStatusMessage, sessionId);
			originStatusMessage.setStatus(LOGOUT_STATUS);
			redisPublisher.publish(REDIS_CHANNEL, originStatusMessage, sessionId);
			log.info(WEB_SOCKET_LOG_FORMAT, WORKSPACE_UPDATE, session.getId(),
				String.format("previous Session Logout: %s", originStatusMessage)
			);

			// 3.2 remove previous session information
			sessionManager.removeUserSessionId(originStatusMessage.getUserAccessStatusKey());
			// 3.3 update origin status workspace Id and status
			originStatusMessage.setWorkspaceId(workspaceChangeMessage.getWorkspaceId());
			originStatusMessage.setStatus(LOGIN_STATUS);
			log.info(WEB_SOCKET_LOG_FORMAT, WORKSPACE_UPDATE, session.getId(),
				String.format("Update Session For Login: %s", originStatusMessage)
			);
			// 3.4 create new user session information with changed workspaceId
			sessionManager.putUserSessionId(originStatusMessage.getUserAccessStatusKey(), sessionId);
			// 3.5 update current client session information
			clientSessionInfo.setStatusMessage(originStatusMessage);
			// 3.4 update current client session store
			sessionManager.putSessionInformation(session, clientSessionInfo);
			redisPublisher.publish(REDIS_CHANNEL, originStatusMessage, sessionId);
			log.info(WEB_SOCKET_LOG_FORMAT, WORKSPACE_UPDATE, session.getId(),
				String.format("Update Client Session Info: %s", clientSessionInfo)
			);

			userAccessStatusChangeToLogin(originStatusMessage, sessionId);

			WebSocketResponseMessage message = workspaceChangeSuccessMessage(
				workspaceChangeMessage.getUserId(), workspaceChangeMessage.getWorkspaceId()
			);

			String successResponseMessage = messageConvertToText(message);
			log.info(WEB_SOCKET_LOG_FORMAT, WORKSPACE_UPDATE, session.getId(),
				String.format("Send Workspace Change Success Response: %s", successResponseMessage)
			);
			session.sendMessage(new TextMessage(successResponseMessage));
		} catch (Exception e) {
			log.error(WEB_SOCKET_LOG_FORMAT, WORKSPACE_UPDATE, session.getId(), " workspace change fail");
			log.error(WEB_SOCKET_LOG_FORMAT, WORKSPACE_UPDATE, session.getId(), e.getMessage());
			WebSocketResponseMessage message = workspaceChangeFailResponse();
			String workspaceChangeFailResponse = messageConvertToText(message);
			log.error(WEB_SOCKET_LOG_FORMAT, WORKSPACE_UPDATE, session.getId(),
				String.format("Send Workspace Change Fail Response: %s", workspaceChangeFailResponse)
			);
			session.sendMessage(new TextMessage(workspaceChangeFailResponse));
		}
	}

	private WebSocketResponseMessage workspaceChangeFailResponse() {
		Map<String, Object> payload = new HashMap<>();
		WebSocketResponseMessage message = WebSocketResponseMessage.of(CLIENT_WORKSPACE_UPDATE_FAIL);
		message.setData(payload);
		return message;
	}

	private WebSocketResponseMessage workspaceChangeSuccessMessage(String userId, String targetWorkspaceId) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("userId", userId);
		payload.put("workspaceId", targetWorkspaceId);

		WebSocketResponseMessage message = WebSocketResponseMessage.of(CLIENT_WORKSPACE_UPDATE);
		message.setData(payload);
		return message;
	}

	private void handleWorkspaceChangeDuplicateError(
		WebSocketSession session, WorkspaceChangeMessage workspaceChangeMessage,
		StatusMessage originStatusMessage
	) throws IOException {
		AccessStatus accessStatus = findUserAccessStatusInformation(
			workspaceChangeMessage.getUserAccessStatusKey()).orElseThrow(
			() -> new RuntimeException("accessStatus 못찾음")
		);
		WebSocketResponseMessage message = workspaceChangeDuplicateErrorResponse(accessStatus, originStatusMessage);
		String responseMessage = messageConvertToText(message);
		session.sendMessage(new TextMessage(responseMessage));
	}

	private void handleRemoteExitCommand(
		WebSocketSession session, WebSocketRequestMessage<Object> requestMessage
	) throws
		IOException {
		try {
			LogoutMessage logoutMessage = objectMapper.convertValue(requestMessage.getData(), LogoutMessage.class);

			log.info(WEB_SOCKET_LOG_FORMAT, REMOTE_EXIT_EVENT, session.getId(),
				String.format("REQUEST: %s", logoutMessage)
			);

			// find target user web socket
			String targetUserSessionId = sessionManager.findUserCurrentSessionId(
				logoutMessage.getLogoutTargetUserAccessStatusKey());

			log.info(
				WEB_SOCKET_LOG_FORMAT, REMOTE_EXIT_EVENT, session.getId(),
				String.format("Find Target WebSocket Session Id. %s", targetUserSessionId)
			);

			if (isTargetUserNotExitCurrentSessionStore(session, targetUserSessionId)) {
				log.error(WEB_SOCKET_LOG_FORMAT, REMOTE_EXIT_EVENT, session.getId(), "Target User not found");
				Optional<AccessStatus> userAccessStatusInformation = findUserAccessStatusInformation(
					logoutMessage.getLogoutTargetUserAccessStatusKey()
				);
				if (userAccessStatusInformation.isPresent()) {
					accessStatusRepository.delete(userAccessStatusInformation.get());
					log.info(
						WEB_SOCKET_LOG_FORMAT, REMOTE_EXIT_EVENT, session.getId(),
						"Target User Info Not Found In Server. But AccessStatus Found In Redis!. Forced Delete"
					);
					// send remote exit request success response message to current request websocket session user
					sendRemoteExitRequestSuccessToRequestUserSession(session);
					return;
				}

				remoteExitClientNotFoundResponse(session);
				return;
			}

			if (remoteExitUserStatusNotValid(logoutMessage.getLogoutTargetUserAccessStatusKey())) {
				sendRemoteExitRejectResponse(session, logoutMessage);
				return;
			}

			// send remote exit command to target user websocket session
			sendRemoteExitRequestToTargetUserSession(session.getId(), logoutMessage, targetUserSessionId);

			// send remote exit request success response message to current request websocket session user
			sendRemoteExitRequestSuccessToRequestUserSession(session);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			log.error(WEB_SOCKET_LOG_FORMAT, REMOTE_EXIT_EVENT, session.getId(), "Send Remote Exit Fail.");
			log.error(WEB_SOCKET_LOG_FORMAT, REMOTE_EXIT_EVENT, session.getId(), exception.getMessage());
			remoteExitClientNotFoundResponse(session);
		}
	}

	private void sendRemoteExitRejectResponse(
		WebSocketSession session, LogoutMessage logoutMessage
	) throws IOException {
		WebSocketResponseMessage logoutFailMessage = remoteExitRejectResponse(session.getId(), logoutMessage);
		try {
			String responseMessage = messageConvertToText(logoutFailMessage);
			log.error(
				WEB_SOCKET_LOG_FORMAT, REMOTE_EXIT_EVENT, session.getId(),
				"Remote Exit Operation Fail. " + responseMessage
			);
			session.sendMessage(new TextMessage(responseMessage));
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			session.close();
		}
	}

	private boolean remoteExitUserStatusNotValid(String logoutTargetUserAccessStatusKey) {
		Optional<AccessStatus> accessStatusInfo = findUserAccessStatusInformation(logoutTargetUserAccessStatusKey);
		return accessStatusInfo.filter(accessStatus -> accessStatus.getAccessType() == JOIN).isPresent();
	}

	private void sendRemoteExitRequestSuccessToRequestUserSession(WebSocketSession session) throws IOException {
		WebSocketResponseMessage remoteExitLogoutRequestResponse = remoteExitRequestSuccessResponse();
		String remoteExitLogoutRequestResponseMessage = messageConvertToText(remoteExitLogoutRequestResponse);
		session.sendMessage(new TextMessage(remoteExitLogoutRequestResponseMessage));
		log.info(WEB_SOCKET_LOG_FORMAT, REMOTE_EXIT_EVENT, session.getId(),
			String.format("Send Remote Exit Request Success.  %s", remoteExitLogoutRequestResponseMessage)
		);
	}

	private void sendRemoteExitRequestToTargetUserSession(
		String requestSessionId, LogoutMessage logoutMessage, String targetUserSessionId
	) throws IOException {
		Optional<WebSocketSession> targetUserSessionInfo = sessionManager.findWebSocketSessionBySessionId(
			targetUserSessionId);

		if (!targetUserSessionInfo.isPresent()) {
			log.error(WEB_SOCKET_LOG_FORMAT, REMOTE_EXIT_EVENT, requestSessionId,
				String.format("Remote Exit User Session Not Found [%s]", targetUserSessionId)
			);
			return;
		}

		WebSocketSession targetUserSession = targetUserSessionInfo.get();
		WebSocketResponseMessage remoteExitResponse = remoteExitResponse(targetUserSessionId, logoutMessage);
		String remoteExitResponseMessage = messageConvertToText(remoteExitResponse);
		targetUserSession.sendMessage(new TextMessage(remoteExitResponseMessage));
		log.info(WEB_SOCKET_LOG_FORMAT, REMOTE_EXIT_EVENT, requestSessionId,
			String.format("Send Remote Exit Request To [%s]  %s", targetUserSession.getId(), remoteExitResponseMessage)
		);
		targetUserSession.close(CloseStatus.NORMAL);
	}

	private boolean isTargetUserNotExitCurrentSessionStore(WebSocketSession session, String sessionId) {
		if (sessionId == null || !sessionManager.findWebSocketSessionBySessionId(sessionId).isPresent()) {
			// logout fail error response to request user
			log.error(
				WEB_SOCKET_LOG_FORMAT, REMOTE_EXIT_EVENT, session.getId(),
				"Can't Found Target Session Information."
			);
			return true;
		}
		return false;
	}

	private void handleRegisterCommand(WebSocketSession session, WebSocketRequestMessage<Object> requestMessage) throws
		IOException {
		try {
			StatusMessage statusMessage = objectMapper.convertValue(requestMessage.getData(), StatusMessage.class);

			statusMessageValidation(statusMessage);

			// Check Session Duplicate
			if (sessionManager.hasExistUserSessionId(statusMessage.getUserAccessStatusKey())) {
				handleDuplicateRegisterRequestMessage(session, statusMessage);
				return;
			}

			statusMessage.setStatus(LOGIN_STATUS);
			log.info(WEB_SOCKET_LOG_FORMAT, REGISTER_EVENT, session.getId(),
				String.format("REQUEST: %s", statusMessage)
			);

			// Get Client WebSocket Message Information from store
			ClientSessionInfo clientSessionInfo = sessionManager.getSessionInformation(session);

			// Set Current Client Status Message
			clientSessionInfo.setStatusMessage(statusMessage);

			// Update Last Message Received at LocalDate.
			sessionManager.putSessionInformation(session, clientSessionInfo);

			// Store New User Information With Current Session Information
			sessionManager.putUserSessionId(statusMessage.getUserAccessStatusKey(), session.getId());

			log.info(
				WEB_SOCKET_LOG_FORMAT, REGISTER_EVENT, session.getId(),
				"Save User Information In To Current User Session Store With Session Information"
			);

			// Generate Register Success Response
			WebSocketResponseMessage registrationSuccessMessage = registerSuccessResponse(
				session.getId(), statusMessage.getUserId(), statusMessage.getWorkspaceId(), statusMessage.getEmail()
			);

			// Send Register Success Message
			String successResponseMessage = messageConvertToText(registrationSuccessMessage);

			log.info(WEB_SOCKET_LOG_FORMAT, REGISTER_EVENT, session.getId(), successResponseMessage);

			userAccessStatusChangeToLogin(statusMessage, session.getId());

			// Save or Update Client Status to Redis
			redisPublisher.publish(REDIS_CHANNEL, statusMessage, session.getId());

			// Send Register Request Success
			session.sendMessage(new TextMessage(successResponseMessage));
			log.info(
				WEB_SOCKET_LOG_FORMAT, REGISTER_EVENT, session.getId(),
				String.format("Register Success. %s", successResponseMessage)
			);
		} catch (Exception exception) {
			log.error(WEB_SOCKET_LOG_FORMAT, REGISTER_EVENT, session.getId(), exception.getMessage());
			WebSocketResponseMessage registrationFailMessage = registerFailResponse(session.getId());
			String registrationFailMessageString = messageConvertToText(registrationFailMessage);
			session.sendMessage(new TextMessage(registrationFailMessageString));
			log.error(WEB_SOCKET_LOG_FORMAT, REGISTER_EVENT, session.getId(), registrationFailMessage);
		}
	}

	private void statusMessageValidation(StatusMessage statusMessage) throws StatusMessageNotValidException {
		if (StringUtils.isEmpty(statusMessage.getUserId())
			|| StringUtils.isEmpty(statusMessage.getNickname())
			|| StringUtils.isEmpty(statusMessage.getEmail())
			|| StringUtils.isEmpty(statusMessage.getWorkspaceId())
		) {
			throw new StatusMessageNotValidException("상태값이 올바르지 않습니다: " + statusMessage);
		}
	}

	private void handleDuplicateRegisterRequestMessage(WebSocketSession session, StatusMessage statusMessage) throws
		IOException {
		AccessStatus accessStatus = findUserAccessStatusInformation(statusMessage.getUserAccessStatusKey()).orElseThrow(
			() -> new RuntimeException(String.format("[%s] accessStatus not found", statusMessage)));
		WebSocketResponseMessage message = duplicateRegisterFailResponse(accessStatus, statusMessage);
		String registerDuplicateErrorResponse = messageConvertToText(message);
		session.sendMessage(new TextMessage(registerDuplicateErrorResponse));
		log.error(
			WEB_SOCKET_LOG_FORMAT, REGISTER_DUPLICATE_EVENT, session.getId(),
			registerDuplicateErrorResponse
		);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		log.info(WEB_SOCKET_LOG_FORMAT, CONNECT_CLOSE_EVENT, session.getId(), "Connection Closed. " + status);
		ClientSessionInfo removedClientSessionInfo = sessionManager.removeSessionInformation(session);
		if (removedClientSessionInfo == null) {
			log.error(
				WEB_SOCKET_LOG_FORMAT, CONNECT_CLOSE_EVENT, session.getId(),
				"Connection Closed. Client Information Not Found"
			);
			return;
		}

		log.info(
			WEB_SOCKET_LOG_FORMAT, CONNECT_CLOSE_EVENT, session.getId(),
			"Closed Client Session Info " + removedClientSessionInfo
		);

		StatusMessage statusMessage = removedClientSessionInfo.getStatusMessage();

		// if not send user information, do nothing
		if (statusMessage == null || statusMessage.getStatus() == null) {
			log.error(
				WEB_SOCKET_LOG_FORMAT, CONNECT_CLOSE_EVENT, session.getId(),
				"Connection Closed. Client Status Information Not Found"
			);
			return;
		}

		statusMessage.setStatus(LOGOUT_STATUS);
		log.info(
			WEB_SOCKET_LOG_FORMAT, CONNECT_CLOSE_EVENT, session.getId(), "Publish Logout Status Event. " + statusMessage
		);
		redisPublisher.publish(REDIS_CHANNEL, statusMessage, session.getId());
		String removedSessionId = sessionManager.removeUserSessionId(statusMessage.getUserAccessStatusKey());
		log.info(
			WEB_SOCKET_LOG_FORMAT, CONNECT_CLOSE_EVENT, session.getId(),
			"Current User Session Information Deleted. Removed Session Id Is [" + removedSessionId + "]"
		);
		log.info(
			WEB_SOCKET_LOG_FORMAT, CONNECT_CLOSE_EVENT, session.getId(),
			"Current Session Id [" + session.getId() + "] and Removed Session Id [" + removedSessionId + "] Is Equal ["
				+ session.getId().equals(removedSessionId) + "]"
		);
		userAccessStatusChangeToLogout(statusMessage, session.getId());
	}

	private void handlerPongMessage(WebSocketSession session) {
		long lastPongTime = System.currentTimeMillis();
		log.debug(WEB_SOCKET_LOG_FORMAT, PONG_EVENT, session.getId(), "Server Received Pong Message.");
		ClientSessionInfo clientSessionInfo = sessionManager.getSessionInformation(session);
		log.debug(
			WEB_SOCKET_LOG_FORMAT, PONG_EVENT, session.getId(),
			"" + clientSessionInfo + " Update Last Pong Time To " + lastPongTime
		);
		clientSessionInfo.setLastPongTime(lastPongTime);
		sessionManager.putSessionInformation(session, clientSessionInfo);
	}

	private void remoteExitClientNotFoundResponse(WebSocketSession session) throws IOException {
		WebSocketResponseMessage logoutFailMessage = remoteExitFailResponse(session.getId());
		try {
			String responseMessage = messageConvertToText(logoutFailMessage);
			log.error(
				WEB_SOCKET_LOG_FORMAT, REMOTE_EXIT_EVENT, session.getId(),
				"Remote Exit Operation Fail. " + responseMessage
			);
			session.sendMessage(new TextMessage(responseMessage));
		} catch (Exception exception) {
			exception.printStackTrace();
			session.close();
		}
	}

	public void userAccessStatusChangeToLogin(StatusMessage statusMessage, String sessionId) {
		Optional<AccessStatus> userAccessStatusInformation = findUserAccessStatusInformation(
			statusMessage.getUserAccessStatusKey());
		if (!userAccessStatusInformation.isPresent()) {
			AccessStatus accessStatus = new AccessStatus(
				statusMessage.getUserAccessStatusKey(), statusMessage.getEmail(), LOGIN);

			accessStatusRepository.save(accessStatus);
			log.info(WEB_SOCKET_LOG_FORMAT, ACCESS_STATUS_EVENT, sessionId, "Create New Access Status " + accessStatus);
		} else {
			AccessStatus existAccessStatus = userAccessStatusInformation.get();
			log.info(
				WEB_SOCKET_LOG_FORMAT, ACCESS_STATUS_EVENT, sessionId,
				"Find Previous Access Status " + existAccessStatus
			);
			existAccessStatus.setAccessType(LOGIN);
			accessStatusRepository.save(existAccessStatus);
			log.info(
				WEB_SOCKET_LOG_FORMAT, ACCESS_STATUS_EVENT, sessionId,
				"Update Previous Access Status To " + existAccessStatus
			);
		}
	}

	public void userAccessStatusChangeToLogout(StatusMessage statusMessage, String sessionId) {
		Optional<AccessStatus> userAccessStatusInformation = findUserAccessStatusInformation(
			statusMessage.getUserAccessStatusKey());
		if (userAccessStatusInformation.isPresent()) {
			AccessStatus existAccessStatus = userAccessStatusInformation.get();
			log.info(WEB_SOCKET_LOG_FORMAT, CONNECT_CLOSE_EVENT, sessionId,
				"Find Access Status Information Of Client Session Info From Closed Session " + existAccessStatus
			);
			existAccessStatus.setAccessType(LOGOUT);
			accessStatusRepository.save(existAccessStatus);
			log.info(WEB_SOCKET_LOG_FORMAT, CONNECT_CLOSE_EVENT, sessionId,
				"Update Access Status Information Of Client Session Info From Closed Session " + existAccessStatus
			);
		}
	}

	public Optional<AccessStatus> findUserAccessStatusInformation(String accessUserStatusKey) {
		return accessStatusRepository.findById(accessUserStatusKey);
	}

	private WebSocketResponseMessage connectSuccessResponse(final String sessionId) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("sessionId", sessionId);
		payload.put("heartbeat", HEART_BEAT_TIME);

		WebSocketResponseMessage message = WebSocketResponseMessage.of(CLIENT_CONNECT_SUCCESS);
		message.setData(payload);
		return message;
	}

	private WebSocketResponseMessage registerSuccessResponse(
		String sessionId, final String userUUID, final String workspaceId, final String email
	) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("sessionId", sessionId);
		payload.put("workspaceId", workspaceId);
		payload.put("userId", userUUID);
		payload.put("email", email);

		WebSocketResponseMessage message = WebSocketResponseMessage.of(CLIENT_REGISTRATION_SUCCESS);
		message.setData(payload);
		return message;
	}

	private WebSocketResponseMessage remoteExitRequestSuccessResponse() {
		Map<String, Object> payload = new HashMap<>();
		WebSocketResponseMessage message = WebSocketResponseMessage.of(CLIENT_REMOTE_EXIT_SUCCESS);
		message.setData(payload);
		return message;
	}

	private WebSocketResponseMessage remoteExitResponse(
		final String sendUserUUID, LogoutMessage logoutMessage
	) {
		// Create Socket Connect Success Response
		Map<String, Object> payload = new HashMap<>();
		payload.put("sender", sendUserUUID);
		payload.put("logoutData", logoutMessage);

		WebSocketResponseMessage message = WebSocketResponseMessage.of(CLIENT_REMOTE_EXIT);
		message.setData(payload);
		return message;
	}

	private WebSocketResponseMessage remoteExitFailResponse(
		final String sessionId
	) {
		// Create Socket Connect Success Response
		Map<String, Object> payload = new HashMap<>();
		payload.put("sessionId", sessionId);

		WebSocketResponseMessage message = WebSocketResponseMessage.of(CLIENT_REMOTE_EXIT_FAIL);
		message.setData(payload);
		return message;
	}

	private WebSocketResponseMessage remoteExitRejectResponse(
		final String sessionId, LogoutMessage logoutMessage
	) {
		// Create Socket Connect Success Response
		Map<String, Object> payload = new HashMap<>();
		payload.put("sessionId", sessionId);
		payload.put("rejectLogoutData", logoutMessage);

		WebSocketResponseMessage message = WebSocketResponseMessage.of(CLIENT_REMOTE_EXIT_REJECT);
		message.setData(payload);
		return message;
	}

	private WebSocketResponseMessage registerFailResponse(final String sessionId) {
		// Create Socket Connect Success Response
		Map<String, Object> payload = new HashMap<>();
		payload.put("sessionId", sessionId);

		WebSocketResponseMessage message = WebSocketResponseMessage.of(CLIENT_REGISTRATION_FAIL);
		message.setData(payload);
		return message;
	}

	private WebSocketResponseMessage duplicateRegisterFailResponse(
		final AccessStatus accessStatus, final StatusMessage statusMessage
	) {
		// Create Socket Connect Success Response
		Map<String, Object> payload = new HashMap<>();
		payload.put("workspaceId", statusMessage.getWorkspaceId());
		payload.put("userId", statusMessage.getUserId());
		payload.put("email", statusMessage.getEmail());
		payload.put("nickname", statusMessage.getNickname());
		payload.put("currentStatus", accessStatus.getAccessType().name());

		WebSocketResponseMessage message = WebSocketResponseMessage.of(CLIENT_REGISTRATION_FAIL_DUPLICATE);
		message.setData(payload);

		return message;
	}

	private WebSocketResponseMessage workspaceChangeDuplicateErrorResponse(
		AccessStatus accessStatus, StatusMessage statusMessage
	) {
		Map<String, Object> payload = new HashMap<>();
		payload.put("workspaceId", statusMessage.getWorkspaceId());
		payload.put("userId", statusMessage.getUserId());
		payload.put("email", statusMessage.getEmail());
		payload.put("nickname", statusMessage.getNickname());
		payload.put("currentStatus", accessStatus.getAccessType().name());

		WebSocketResponseMessage message = WebSocketResponseMessage.of(CLIENT_WORKSPACE_UPDATE_DUPLICATE_FAIL);
		message.setData(payload);
		return message;
	}

	private String messageConvertToText(WebSocketResponseMessage webSocketResponseMessage) throws
		JsonProcessingException {
		return objectMapper.writeValueAsString(webSocketResponseMessage);
	}
}
