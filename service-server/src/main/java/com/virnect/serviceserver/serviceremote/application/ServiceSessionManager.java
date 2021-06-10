package com.virnect.serviceserver.serviceremote.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.client.RemoteServiceException;
import com.virnect.data.application.workspace.WorkspaceRestService;
import com.virnect.data.dto.PageMetadataResponse;
import com.virnect.serviceserver.serviceremote.dto.mapper.member.MemberWorkspaceMapper;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.data.redis.RedisPublisher;
import com.virnect.data.redis.application.AccessStatusService;
import com.virnect.data.redis.domain.AccessStatus;
import com.virnect.data.redis.domain.AccessType;
import com.virnect.data.redis.domain.ForceLogoutMessage;
import com.virnect.java.client.MediaMode;
import com.virnect.java.client.Recording;
import com.virnect.java.client.RecordingLayout;
import com.virnect.java.client.RecordingMode;
import com.virnect.java.client.RemoteServiceRole;
import com.virnect.java.client.SessionProperties;
import com.virnect.mediaserver.core.EndReason;
import com.virnect.mediaserver.core.IdentifierPrefixes;
import com.virnect.mediaserver.core.Participant;
import com.virnect.mediaserver.core.Session;
import com.virnect.mediaserver.core.SessionManager;
import com.virnect.mediaserver.kurento.core.KurentoSession;
import com.virnect.mediaserver.kurento.core.KurentoSessionListener;
import com.virnect.mediaserver.kurento.core.KurentoTokenOptions;
import com.virnect.data.dto.request.session.ForceLogoutRequest;
import com.virnect.data.dto.response.member.MemberInfoListResponse;
import com.virnect.data.dto.response.member.MemberInfoResponse;
import com.virnect.data.dto.response.session.SessionData;
import com.virnect.data.dto.response.session.SessionTokenData;
import com.virnect.serviceserver.serviceremote.dao.SessionDataRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceSessionManager {
	private static final String TAG = ServiceSessionManager.class.getSimpleName();
	private final String SESSION_METHOD = "generateSession";
	private final String SESSION_MESSAGE_METHOD = "generateMessage";
	private final String SESSION_TOKEN_METHOD = "generateSessionToken";

	SessionManager sessionManager;
	private final SessionDataRepository sessionDataRepository;
	private final FileService fileService;

	private final AccessStatusService accessStatusService;
	private final WorkspaceRestService workspaceRestService;
	//private final ModelMapper modelMapper;
	private static final ChannelTopic REDIS_CHANNEL = new ChannelTopic("force-logout");
	private final RedisPublisher redisPublisher;

	private final MemberWorkspaceMapper memberWorkspaceMapper;

	@Autowired
	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	@Bean
	@ConditionalOnMissingBean
	@DependsOn("sessionManager")
	public KurentoSessionListener kurentoSessionListener() {
		LogMessage.formedInfo(
			TAG,
			"SET LISTENER",
			"kurentoSessionListener",
			"interaction with kurento session",
			"created"
		);
		return new KurentoSessionListener() {
			@Override
			public void createSession(Session sessionNotActive) {
				LogMessage.formedInfo(
					TAG,
					"CREATE SESSION EVENT",
					"createSession",
					"session create and sessionEventHandler is here",
					"event received"
				);
				String sessionId = sessionNotActive.getSessionId();
				Boolean result = sessionDataRepository.generateRoomSession(sessionId);
				//if (result.getData()) {
				if (result) {
					sessionDataRepository.sendSessionCreate(sessionId);
				}
			}

			@Override
			public boolean joinSession(Participant participant, String sessionId, Integer transactionId) {
				String result = "[participant] " + participant + "\n"
					+ "[sessionId]" + sessionId + "\n"
					+ "[transactionId]" + transactionId + "\n";

				LogMessage.formedInfo(
					TAG,
					"JOIN SESSION EVENT",
					"joinSession",
					"session join and session event handler",
					result
				);
				if (participant.getParticipantPublicId().equals("RECORDER")) {
					LogMessage.formedInfo(
						TAG,
						"JOIN SESSION EVENT",
						"joinSession",
						"session join and session event handler for recorder",
						result
					);
					return true;
				} else {
					ErrorCode errorCode = sessionDataRepository.joinSession(participant, sessionId);
					if (errorCode == ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID) {
						LogMessage.formedError(
							TAG,
							"JOIN SESSION EVENT_ERROR",
							"joinSession",
							errorCode.getMessage(),
							"return false"
						);
						return false;

						//do not force disconnect by server
                        /*Session session = sessionManager.getSessionWithNotActive(sessionId);
                        Participant evict = session.getParticipantByPublicId(participant.getParticipantPublicId());
                        sessionManager.evictParticipant(evict, null, null, EndReason.forceDisconnectByServer);*/
					} else {
						sessionDataRepository.setAccessStatus(participant, sessionId, AccessType.JOIN);
						//todo: after log here
						return true;
					}
				}
			}

			@Override
			public void leaveSession(
				Participant participant, String sessionId, Set<Participant> remainingParticipants,
				Integer transactionId, EndReason reason
			) {
				String result = "[participant] " + participant + "\n"
					+ "[clientMetadata]" + participant.getClientMetadata() + "\n"
					+ "[sessionId]" + sessionId + "\n"
					+ "[remainingParticipants]" + remainingParticipants + "\n"
					+ "[transactionId]" + transactionId + "\n"
					+ "[reason]" + reason + "\n";
				LogMessage.formedInfo(
					TAG,
					"LEAVE SESSION EVENT",
					"leaveSession",
					"session leave and sessionEventHandler is here",
					result
				);
				if (reason.equals(EndReason.forceDisconnectByUser)) {
					sessionDataRepository.disconnectSession(participant, sessionId, reason);
				} else if (reason.equals(EndReason.sessionClosedByServer)) {
					sessionDataRepository.closeSession(participant, sessionId, reason);
				} else {
					sessionDataRepository.leaveSession(participant, sessionId, reason);
				}
				sessionDataRepository.setAccessStatus(participant, sessionId, AccessType.LEAVE);
			}

			@Override
			public void destroySession(KurentoSession session, EndReason reason) {
				String result = "[sessionId] " + session.getSessionId() + "\n"
					+ "[reason]" + reason + "\n";
				LogMessage.formedInfo(
					TAG,
					"DESTROY SESSION EVENT",
					"destroySession",
					"session destroy and sessionEventHandler is here",
					result
				);

				fileService.removeFiles(session.getSessionId());
				sessionDataRepository.stopRecordSession(session.getSessionId());
				sessionDataRepository.destroySession(session.getSessionId(), reason);
			}
		};
	}



    /*public ServiceSessionManager(@Lazy SessionManager sessionManager, DataRepository dataRepository) {
        this.sessionManager = sessionManager;
        this.dataRepository = dataRepository;
    }*/

	private JsonObject generateErrorMessage(String errorMessage, String path, HttpStatus status) {
		JsonObject responseJson = new JsonObject();
		responseJson.addProperty("timestamp", System.currentTimeMillis());
		responseJson.addProperty("status", status.value());
		responseJson.addProperty("error", status.getReasonPhrase());
		responseJson.addProperty("message", errorMessage);
		responseJson.addProperty("path", path);
		return responseJson;
	}

	public JsonObject generateSession() {
		SessionData sessionData = new SessionData();
		SessionProperties.Builder builder = new SessionProperties.Builder();
		String customSessionId = sessionData.getCustomSessionId();
		try {
			// Safe parameter retrieval. Default values if not defined
			if (sessionData.getRecordingMode() != null) {
				RecordingMode recordingMode = RecordingMode.valueOf(sessionData.getRecordingMode());
				builder = builder.recordingMode(recordingMode);
			} else {
				builder = builder.recordingMode(RecordingMode.MANUAL);
			}
			if (sessionData.getDefaultOutputMode() != null) {
				Recording.OutputMode defaultOutputMode = Recording.OutputMode.valueOf(
					sessionData.getDefaultOutputMode());
				builder = builder.defaultOutputMode(defaultOutputMode);
			} else {
				builder.defaultOutputMode(Recording.OutputMode.COMPOSED);
			}
			if (sessionData.getDefaultRecordingLayout() != null) {
				RecordingLayout defaultRecordingLayout = RecordingLayout.valueOf(
					sessionData.getDefaultRecordingLayout());
				builder = builder.defaultRecordingLayout(defaultRecordingLayout);
			} else {
				builder.defaultRecordingLayout(RecordingLayout.BEST_FIT);
			}
			if (sessionData.getMediaMode() != null) {
				MediaMode mediaMode = MediaMode.valueOf(sessionData.getMediaMode());
				builder = builder.mediaMode(mediaMode);
			} else {
				builder = builder.mediaMode(MediaMode.ROUTED);
			}
			if (customSessionId != null && !customSessionId.isEmpty()) {
				if (!sessionManager.formatChecker.isValidCustomSessionId(customSessionId)) {
					return generateErrorMessage(
						"Parameter \"customSessionId\" is wrong. Must be an alphanumeric string",
						SESSION_METHOD,
						HttpStatus.BAD_REQUEST
					);
				}
				builder = builder.customSessionId(customSessionId);
			}
			builder = builder.defaultCustomLayout(
				(sessionData.getDefaultCustomLayout() != null) ? sessionData.getDefaultCustomLayout() : "");

		} catch (IllegalArgumentException e) {
			return generateErrorMessage(
				"RecordingMode " + sessionData.getRecordingMode() + " | "
					+ "Default OutputMode " + sessionData.getDefaultOutputMode() + " | "
					+ "Default RecordingLayout " + sessionData.getDefaultRecordingLayout() + " | "
					+ "MediaMode " + sessionData.getMediaMode()
					+ ". Some parameter is not defined",
				"/api/sessions",
				HttpStatus.BAD_REQUEST
			);
		}

		SessionProperties sessionProperties = builder.build();
		String sessionId;
		if (customSessionId != null && !customSessionId.isEmpty()) {
			if (sessionManager.getSessionWithNotActive(customSessionId) != null) {
				return generateErrorMessage(
					"customSessionId" + customSessionId + " | "
						+ "RecordingMode " + sessionData.getRecordingMode() + " | "
						+ "Default OutputMode " + sessionData.getDefaultOutputMode() + " | "
						+ "Default RecordingLayout " + sessionData.getDefaultRecordingLayout() + " | "
						+ "MediaMode " + sessionData.getMediaMode()
						+ ". Custom session is already defined",
					SESSION_METHOD,
					HttpStatus.CONFLICT
				);
			}
			sessionId = customSessionId;
		} else {
			sessionId = IdentifierPrefixes.SESSION_ID + RandomStringUtils.randomAlphabetic(1).toUpperCase()
				+ RandomStringUtils.randomAlphanumeric(9);
		}

		Session sessionNotActive = sessionManager.storeSessionNotActive(sessionId, sessionProperties);
		log.info("New session {} initialized {}", sessionId, this.sessionManager.getSessionsWithNotActive().stream()
			.map(Session::getSessionId).collect(Collectors.toList()).toString());
		JsonObject sessionJson = new JsonObject();
		sessionJson.addProperty("id", sessionNotActive.getSessionId());
		sessionJson.addProperty("createdAt", sessionNotActive.getStartTime());

		return sessionJson;
	}

	public JsonObject generateSession(String customSessionId) {
		SessionData sessionData = new SessionData();
		SessionProperties.Builder builder = new SessionProperties.Builder();
		//String customSessionId = sessionData.getCustomSessionId();
		try {
			// Safe parameter retrieval. Default values if not defined
			if (sessionData.getRecordingMode() != null) {
				RecordingMode recordingMode = RecordingMode.valueOf(sessionData.getRecordingMode());
				builder = builder.recordingMode(recordingMode);
			} else {
				builder = builder.recordingMode(RecordingMode.MANUAL);
			}
			if (sessionData.getDefaultOutputMode() != null) {
				Recording.OutputMode defaultOutputMode = Recording.OutputMode.valueOf(
					sessionData.getDefaultOutputMode());
				builder = builder.defaultOutputMode(defaultOutputMode);
			} else {
				builder.defaultOutputMode(Recording.OutputMode.COMPOSED);
			}
			if (sessionData.getDefaultRecordingLayout() != null) {
				RecordingLayout defaultRecordingLayout = RecordingLayout.valueOf(
					sessionData.getDefaultRecordingLayout());
				builder = builder.defaultRecordingLayout(defaultRecordingLayout);
			} else {
				builder.defaultRecordingLayout(RecordingLayout.BEST_FIT);
			}
			if (sessionData.getMediaMode() != null) {
				MediaMode mediaMode = MediaMode.valueOf(sessionData.getMediaMode());
				builder = builder.mediaMode(mediaMode);
			} else {
				builder = builder.mediaMode(MediaMode.ROUTED);
			}
			if (customSessionId != null && !customSessionId.isEmpty()) {
				if (!sessionManager.formatChecker.isValidCustomSessionId(customSessionId)) {
					return generateErrorMessage(
						"Parameter \"customSessionId\" is wrong. Must be an alphanumeric string",
						SESSION_METHOD,
						HttpStatus.BAD_REQUEST
					);
				}
				builder = builder.customSessionId(customSessionId);
			}
			builder = builder.defaultCustomLayout(
				(sessionData.getDefaultCustomLayout() != null) ? sessionData.getDefaultCustomLayout() : "");

		} catch (IllegalArgumentException e) {
			return generateErrorMessage(
				"RecordingMode " + sessionData.getRecordingMode() + " | "
					+ "Default OutputMode " + sessionData.getDefaultOutputMode() + " | "
					+ "Default RecordingLayout " + sessionData.getDefaultRecordingLayout() + " | "
					+ "MediaMode " + sessionData.getMediaMode()
					+ ". Some parameter is not defined",
				"/api/sessions",
				HttpStatus.BAD_REQUEST
			);
		}

		SessionProperties sessionProperties = builder.build();
        /*String sessionId;
        if (customSessionId != null && !customSessionId.isEmpty()) {
            if (sessionManager.getSessionWithNotActive(customSessionId) != null) {
                return generateErrorMessage(
                        "customSessionId" + customSessionId + " | "
                                +"RecordingMode " + sessionData.getRecordingMode() + " | "
                                + "Default OutputMode " + sessionData.getDefaultOutputMode() + " | "
                                + "Default RecordingLayout " + sessionData.getDefaultRecordingLayout() + " | "
                                + "MediaMode " + sessionData.getMediaMode()
                                + ". Custom session is already defined",
                        SESSION_METHOD,
                        HttpStatus.CONFLICT
                );
            }
            sessionId = customSessionId;
        } else {
            sessionId = IdentifierPrefixes.SESSION_ID + RandomStringUtils.randomAlphabetic(1).toUpperCase()
                    + RandomStringUtils.randomAlphanumeric(9);
        }*/

		Session sessionNotActive = sessionManager.storeSessionNotActive(customSessionId, sessionProperties);
		log.info(
			"New session {} initialized with custom id: {}", customSessionId,
			this.sessionManager.getSessionsWithNotActive().stream()
				.map(Session::getSessionId).collect(Collectors.toList()).toString()
		);
		JsonObject sessionJson = new JsonObject();
		sessionJson.addProperty("id", sessionNotActive.getSessionId());
		sessionJson.addProperty("createdAt", sessionNotActive.getStartTime());

		return sessionJson;
	}

	//public JsonObject generateSessionToken(SessionTokenData tokenData) {
	public JsonObject generateSessionToken(JsonObject sessionJson) {
		//Assert.assertTrue(jsonObject.get("name").getAsString().equals("Baeldung"));
		//Assert.assertTrue(jsonObject.get("java").getAsBoolean() == true);
		//Auto generate Token
		SessionTokenData tokenData = new SessionTokenData();
		tokenData.setSession(sessionJson.get("id").getAsString());
		tokenData.setRole("PUBLISHER");
		tokenData.setData("");

		if (ObjectUtils.isEmpty(tokenData)) {
			return generateErrorMessage(
				"Error in body parameters. Cannot be empty",
				SESSION_TOKEN_METHOD,
				HttpStatus.BAD_REQUEST
			);
		}

		log.info("INTERNAL API: generateSessionToken {}", tokenData.toString());

		String sessionId;
		String roleString;
		String metadata;
		try {
			sessionId = tokenData.getSession();
			roleString = tokenData.getRole();
			metadata = tokenData.getRole();
		} catch (ClassCastException e) {
			return generateErrorMessage(
				"Type error in some parameter",
				SESSION_TOKEN_METHOD,
				HttpStatus.BAD_REQUEST
			);
		}

		if (sessionId == null) {
			return generateErrorMessage(
				"\"session\" parameter is mandatory",
				SESSION_TOKEN_METHOD,
				HttpStatus.BAD_REQUEST
			);
		}

		final Session session = this.sessionManager.getSessionWithNotActive(sessionId);
		if (session == null) {
			return generateErrorMessage(
				"Session " + sessionId + " not found",
				SESSION_TOKEN_METHOD,
				HttpStatus.NOT_FOUND
			);
		}

		JsonObject kurentoOptions = null;

		if (tokenData.getKurentoOptions() != null) {
			try {
				kurentoOptions = JsonParser.parseString(tokenData.getKurentoOptions().toString()).getAsJsonObject();
			} catch (Exception e) {
				return generateErrorMessage(
					"Error in parameter 'kurentoOptions'. It is not a valid JSON object",
					SESSION_TOKEN_METHOD,
					HttpStatus.BAD_REQUEST
				);
			}
		}

		RemoteServiceRole role;
		try {
			if (roleString != null) {
				role = RemoteServiceRole.valueOf(roleString);
			} else {
				role = RemoteServiceRole.PUBLISHER;
			}
		} catch (IllegalArgumentException e) {
			return generateErrorMessage(
				"Parameter role " + tokenData.getRole() + " is not defined",
				SESSION_TOKEN_METHOD,
				HttpStatus.BAD_REQUEST
			);
		}

		KurentoTokenOptions kurentoTokenOptions = null;
		if (kurentoOptions != null) {
			try {
				kurentoTokenOptions = new KurentoTokenOptions(kurentoOptions);
			} catch (Exception e) {
				return generateErrorMessage(
					"Type error in some parameter of 'kurentoOptions'",
					SESSION_TOKEN_METHOD,
					HttpStatus.BAD_REQUEST
				);
			}
		}

		metadata = (metadata != null) ? metadata : "";

		// While closing a session tokens can't be generated
		if (session.closingLock.readLock().tryLock()) {
			try {
				String token = sessionManager.newToken(session, role, metadata, kurentoTokenOptions);

				JsonObject responseJson = new JsonObject();
				responseJson.addProperty("id", token);
				responseJson.addProperty("session", sessionId);
				responseJson.addProperty("role", role.toString());
				responseJson.addProperty("data", metadata);
				responseJson.addProperty("token", token);
				responseJson.addProperty("error", HttpStatus.OK.getReasonPhrase());

				if (kurentoOptions != null) {
					JsonObject kurentoOptsResponse = new JsonObject();
					if (kurentoTokenOptions.getVideoMaxRecvBandwidth() != null) {
						kurentoOptsResponse.addProperty(
							"videoMaxRecvBandwidth",
							kurentoTokenOptions.getVideoMaxRecvBandwidth()
						);
					}
					if (kurentoTokenOptions.getVideoMinRecvBandwidth() != null) {
						kurentoOptsResponse.addProperty(
							"videoMinRecvBandwidth",
							kurentoTokenOptions.getVideoMinRecvBandwidth()
						);
					}
					if (kurentoTokenOptions.getVideoMaxSendBandwidth() != null) {
						kurentoOptsResponse.addProperty(
							"videoMaxSendBandwidth",
							kurentoTokenOptions.getVideoMaxSendBandwidth()
						);
					}
					if (kurentoTokenOptions.getVideoMinSendBandwidth() != null) {
						kurentoOptsResponse.addProperty(
							"videoMinSendBandwidth",
							kurentoTokenOptions.getVideoMinSendBandwidth()
						);
					}
					if (kurentoTokenOptions.getAllowedFilters().length > 0) {
						JsonArray filters = new JsonArray();
						for (String filter : kurentoTokenOptions.getAllowedFilters()) {
							filters.add(filter);
						}
						kurentoOptsResponse.add("allowedFilters", filters);
					}
					responseJson.add("kurentoOptions", kurentoOptsResponse);
				}
				//return new ResponseEntity<>(responseJson.toString(), getResponseHeaders(), HttpStatus.OK);
				//flags: return token string
				return responseJson;
			} catch (Exception e) {
				return generateErrorMessage(
					"Error generating token for session " + sessionId + ": " + e.getMessage(),
					SESSION_TOKEN_METHOD,
					HttpStatus.INTERNAL_SERVER_ERROR
				);
			} finally {
				session.closingLock.readLock().unlock();
			}
		} else {
			log.error("Session {} is in the process of closing. Token couldn't be generated", sessionId);
			return generateErrorMessage(
				"Session " + sessionId + " not found",
				SESSION_TOKEN_METHOD,
				HttpStatus.NOT_FOUND
			);
		}
	}

	public boolean closeActiveSession(String sessionId) {
		Session session = this.sessionManager.getSession(sessionId);
		if (session != null) {
			log.info("REST API: DELETE closeSession");
			this.sessionManager.closeSession(sessionId, EndReason.sessionClosedByServer);
			return true;
		}
		return false;
	}

	public boolean closeNotActiveSession(String sessionId) {
		Session sessionNotActive = this.sessionManager.getSessionNotActive(sessionId);
		if (sessionNotActive != null) {
			try {
				if (sessionNotActive.closingLock.writeLock().tryLock(15, TimeUnit.SECONDS)) {
					try {
						log.info("REST API: DELETE close sessionNotActive");
						if (sessionNotActive.isClosed()) {
							return true;
						}

						this.sessionManager.closeSessionAndEmptyCollections(
							sessionNotActive,
							EndReason.sessionClosedByServer,
							true
						);
						return true;
						//return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
					} finally {
						sessionNotActive.closingLock.writeLock().unlock();
					}
				} else {
					String errorMsg = "Timeout waiting for Session " + sessionId
						+ " closing lock to be available for closing from DELETE /api/sessions";
					log.error(errorMsg);
					return false;
					//apiResponse.setMessage(generateErrorMessage(errorMsg, "/api/sessions", HttpStatus.BAD_REQUEST).toString());
					//return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
				}
			} catch (InterruptedException e) {
				String errorMsg = "InterruptedException while waiting for Session " + sessionId
					+ " closing lock to be available for closing from DELETE /api/sessions";
				log.error(errorMsg);
				return false;
                /*apiResponse.setData(false);
                apiResponse.setMessage(generateErrorMessage(errorMsg, "/api/sessions", HttpStatus.BAD_REQUEST).toString());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);*/
			}
		} else {
			//return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
			return false;
		}
	}

	public JsonObject generateMessage(
		String sessionId,
		List<String> to,
		String type,
		String data

	) {
		log.info("session generateMessage event is here");
		JsonObject completeMessage = new JsonObject();

		Session session = sessionManager.getSession(sessionId);
		if (session == null) {
			session = sessionManager.getSessionNotActive(sessionId);
			if (session != null) {
				// Session is not active (no connected participants)
				return generateErrorMessage(
					"Session is not active (no connected participants)",
					SESSION_MESSAGE_METHOD,
					HttpStatus.NOT_ACCEPTABLE
				);
			}
			// Session does not exist
			return generateErrorMessage(
				"Session does not exist",
				SESSION_MESSAGE_METHOD,
				HttpStatus.NOT_FOUND
			);
		}

		if (type != null) {
			completeMessage.addProperty("type", type);
		}

		//create data to json object string
		if (data != null) {
			//example as {data : {"type" : "evict"}}
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("type", data);
			completeMessage.addProperty("data", jsonObject.toString());
			log.info("generateMessage data is {}", jsonObject.toString());
			//completeMessage.addProperty("data", data);
		}

		if (to != null) {
			try {
				Gson gson = new GsonBuilder().create();
				JsonArray toArray = gson.toJsonTree(to).getAsJsonArray();
				completeMessage.add("to", toArray);
			} catch (IllegalStateException exception) {
				return generateErrorMessage(
					"\"to\" parameter is not a valid JSON array",
					SESSION_MESSAGE_METHOD,
					HttpStatus.BAD_REQUEST
				);
			}
		}

		try {
			sessionManager.sendMessage(completeMessage.toString(), sessionId);
		} catch (RemoteServiceException e) {
			return this.generateErrorMessage(
				"\"to\" array has no valid connection identifiers",
				SESSION_MESSAGE_METHOD,
				HttpStatus.NOT_ACCEPTABLE
			);
		}
		return completeMessage;
		//return new ResponseEntity<>(HttpStatus.OK);
	}

	public boolean evictParticipant(String sessionId, String connectionId) {
		Session session = this.sessionManager.getSessionWithNotActive(sessionId);
		if (session == null) {
			return false;
		}

		//evictedParticipant
		Participant participant = session.getParticipantByPublicId(connectionId);
		if (participant != null) {
			this.sessionManager.evictParticipant(participant, null, null, EndReason.forceDisconnectByUser);
			return true;
			//return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return false;
			//return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
		}
	}

	public ApiResponse<MemberInfoListResponse> forceLogout(ForceLogoutRequest forceLogoutRequest) {
		// Master uuid 및 권한 체크
		WorkspaceMemberInfoResponse masterUserInfo = workspaceRestService.getWorkspaceMemberInfo(
			forceLogoutRequest.getWorkspaceId(), forceLogoutRequest.getUserId()).getData();
		if (StringUtils.isBlank(masterUserInfo.getUuid())) {
			log.info("Master uuid is null");
			return new ApiResponse<>(ErrorCode.ERR_API_AUTHENTICATION);
		}
		if (!("MASTER".equals(masterUserInfo.getRole()))) {
			log.info("This user is not Master");
			return new ApiResponse<>(ErrorCode.ERR_API_AUTHENTICATION);
		}

		// 로그아웃 및 협업 중인 멤버 필터링
		List<String> failUserIds = new ArrayList<>();
		for(Iterator<String> targetUuidList = forceLogoutRequest.getTargetUserIds().iterator(); targetUuidList.hasNext();){
			AccessStatus targetUser = accessStatusService.getAccessStatus(forceLogoutRequest.getWorkspaceId() + "_" + targetUuidList.next());
			if (!ObjectUtils.isEmpty(targetUser)) {
				if (targetUser.getAccessType() == AccessType.LOGOUT || targetUser.getAccessType() == AccessType.JOIN) {
					targetUuidList.remove();
					failUserIds.add(targetUser.getId());
				}
			}
		}

		List<WorkspaceMemberInfoResponse> failMembers = new ArrayList<>();
		List<MemberInfoResponse> failMembersResponse = new ArrayList<>();
		PageMetadataResponse pageMeta;

		// 강제 로그 아웃 대상이 없을 경우 (대상 전체가 이미 로그아웃 또는 협업 중인 경우)
		if (forceLogoutRequest.getTargetUserIds().size() == 0) {
			for (String failMemberUuid : failUserIds) {
				ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(
					forceLogoutRequest.getWorkspaceId(), failMemberUuid);
				if (workspaceMemberInfo.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
					return new ApiResponse<>(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
				}
				failMembers.add(workspaceMemberInfo.getData());
			}
			// Mapper Response
			failMembersResponse = failMembers.stream()
				//.map(memberInfo -> modelMapper.map(memberInfo, MemberInfoResponse.class))
				.map(workspaceMemberInfoResponse -> memberWorkspaceMapper.toDto(workspaceMemberInfoResponse))
				.collect(Collectors.toList());
			// 페이징 데이터 셋팅 (페이징 사용안함)
			pageMeta = PageMetadataResponse.builder()
				.currentPage(1)
				.currentSize(failMembersResponse.size())
				.totalPage(1)
				.totalElements(failMembersResponse.size())
				.numberOfElements(failMembersResponse.size())
				.build();
			return
				new ApiResponse<>(new MemberInfoListResponse(failMembersResponse, pageMeta), ErrorCode.ERR_MEMBER_LOGOUT_OR_JOIN);
		}

		// Redis force-logout 채널로 정보 전송
		ForceLogoutMessage forceLogoutMessage = ForceLogoutMessage.builder()
			.workspaceId(forceLogoutRequest.getWorkspaceId())
			.userId(forceLogoutRequest.getUserId())
			.targetUserIds(forceLogoutRequest.getTargetUserIds())
			.build();
		log.info("ForceLogoutMessage to string : " + forceLogoutMessage.toString());
		redisPublisher.publish(REDIS_CHANNEL, forceLogoutMessage);

		// Logout 상태 확인 (in Redis)
		for (String targetUserId : forceLogoutRequest.getTargetUserIds()) {
			AccessStatus checkLogout = accessStatusService.getAccessStatus(forceLogoutRequest.getWorkspaceId() + "_" + targetUserId);
			if (ObjectUtils.isEmpty(checkLogout)) {
				for(Iterator<String> failUserId = failUserIds.iterator(); failUserId.hasNext();){
					if (targetUserId.equals(failUserId.next())) {
						failUserId.remove();
					}
				}
			} else {
				if (checkLogout.getAccessType() == AccessType.LOGOUT) {
					for(Iterator<String> failUserId = failUserIds.iterator(); failUserId.hasNext();){
						if (targetUserId.equals(failUserId.next())) {
							failUserId.remove();
						}
					}
				}
			}
		}

		// 강제 로그아웃 대상에서 제외된 유저들 정보
		for (String failMemberUuid : failUserIds) {
			ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(
				forceLogoutRequest.getWorkspaceId(), failMemberUuid);
			if (workspaceMemberInfo.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
				return new ApiResponse<>(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
			}
			failMembers.add(workspaceMemberInfo.getData());
		}

		if (failMembers.size() > 0) {
			// Mapper Response
			failMembersResponse = failMembers.stream()
				.map(workspaceMemberInfoResponse -> memberWorkspaceMapper.toDto(workspaceMemberInfoResponse))
				.collect(Collectors.toList());
			// 페이징 데이터 셋팅 (페이징 사용안함)
			pageMeta = PageMetadataResponse.builder()
				.currentPage(1)
				.currentSize(failMembersResponse.size())
				.totalPage(1)
				.totalElements(failMembersResponse.size())
				.numberOfElements(failMembersResponse.size())
				.build();
		} else {
			// 페이징 데이터 셋팅 (페이징 사용안함)
			pageMeta = PageMetadataResponse.builder()
				.currentPage(1)
				.currentSize(1)
				.totalPage(1)
				.totalElements(0)
				.numberOfElements(0)
				.build();
		}

		return
			new ApiResponse<>(
				new MemberInfoListResponse(failMembersResponse, pageMeta),
				ErrorCode.ERR_SUCCESS
			);
	}
}
