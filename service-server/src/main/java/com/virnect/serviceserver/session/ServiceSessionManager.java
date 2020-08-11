package com.virnect.serviceserver.session;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.virnect.serviceserver.api.ApiResponse;
import com.virnect.serviceserver.dao.Room;
import com.virnect.serviceserver.dao.RoomStatus;
import com.virnect.serviceserver.dto.response.RoomResponse;
import com.virnect.serviceserver.error.ErrorCode;
import com.virnect.java.client.*;
import com.virnect.serviceserver.core.*;
import com.virnect.serviceserver.core.Session;
import com.virnect.serviceserver.data.DataRepository;
import com.virnect.serviceserver.model.SessionData;
import com.virnect.serviceserver.model.SessionTokenData;
import com.virnect.serviceserver.kurento.core.KurentoSession;
import com.virnect.serviceserver.kurento.core.KurentoTokenOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
//@RequiredArgsConstructor
public class ServiceSessionManager {
    private static final String TAG = ServiceSessionManager.class.getSimpleName();
    private final String SESSION_METHOD = "generateSession";
    private final String SESSION_TOKEN_METHOD = "generateSessionToken";

    private final SessionManager sessionManager;
    private final DataRepository dataRepository;

    public ServiceSessionManager(@Lazy SessionManager sessionManager, DataRepository dataRepository) {
        this.sessionManager = sessionManager;
        this.dataRepository = dataRepository;
    }

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
                Recording.OutputMode defaultOutputMode = Recording.OutputMode.valueOf(sessionData.getDefaultOutputMode());
                builder = builder.defaultOutputMode(defaultOutputMode);
            } else {
                builder.defaultOutputMode(Recording.OutputMode.COMPOSED);
            }
            if (sessionData.getDefaultRecordingLayout() != null) {
                RecordingLayout defaultRecordingLayout = RecordingLayout.valueOf(sessionData.getDefaultRecordingLayout());
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
                            HttpStatus.BAD_REQUEST);
                }
                builder = builder.customSessionId(customSessionId);
            }
            builder = builder.defaultCustomLayout((sessionData.getDefaultCustomLayout() != null) ? sessionData.getDefaultCustomLayout() : "");

        } catch (IllegalArgumentException e) {
            return generateErrorMessage(
                    "RecordingMode " + sessionData.getRecordingMode() + " | "
                            + "Default OutputMode " + sessionData.getDefaultOutputMode() + " | "
                            + "Default RecordingLayout " + sessionData.getDefaultRecordingLayout() + " | "
                            + "MediaMode " + sessionData.getMediaMode()
                            + ". Some parameter is not defined",
                    "/api/sessions",
                    HttpStatus.BAD_REQUEST);
        }

        SessionProperties sessionProperties = builder.build();
        String sessionId;
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
        }

        Session sessionNotActive = sessionManager.storeSessionNotActive(sessionId, sessionProperties);
        log.info("New session {} initialized {}", sessionId, this.sessionManager.getSessionsWithNotActive().stream()
                .map(Session::getSessionId).collect(Collectors.toList()).toString());
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

        if (tokenData == null) {
            return generateErrorMessage(
                    "Error in body parameters. Cannot be empty",
                    SESSION_TOKEN_METHOD,
                    HttpStatus.BAD_REQUEST);
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
                    HttpStatus.BAD_REQUEST);
        }

        if (sessionId == null) {
            return generateErrorMessage(
                    "\"session\" parameter is mandatory",
                    SESSION_TOKEN_METHOD,
                    HttpStatus.BAD_REQUEST);
        }

        final Session session = this.sessionManager.getSessionWithNotActive(sessionId);
        if (session == null) {
            return generateErrorMessage(
                    "Session " + sessionId + " not found",
                    SESSION_TOKEN_METHOD,
                    HttpStatus.NOT_FOUND);
        }

        JsonObject kurentoOptions = null;

        if (tokenData.getKurentoOptions() != null) {
            try {
                kurentoOptions = JsonParser.parseString(tokenData.getKurentoOptions().toString()).getAsJsonObject();
            } catch (Exception e) {
                return generateErrorMessage(
                        "Error in parameter 'kurentoOptions'. It is not a valid JSON object",
                        SESSION_TOKEN_METHOD,
                        HttpStatus.BAD_REQUEST);
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
                    HttpStatus.BAD_REQUEST);
        }

        KurentoTokenOptions kurentoTokenOptions = null;
        if (kurentoOptions != null) {
            try {
                kurentoTokenOptions = new KurentoTokenOptions(kurentoOptions);
            } catch (Exception e) {
                return generateErrorMessage(
                        "Type error in some parameter of 'kurentoOptions'",
                        SESSION_TOKEN_METHOD,
                        HttpStatus.BAD_REQUEST);
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
                        kurentoOptsResponse.addProperty("videoMaxRecvBandwidth",
                                kurentoTokenOptions.getVideoMaxRecvBandwidth());
                    }
                    if (kurentoTokenOptions.getVideoMinRecvBandwidth() != null) {
                        kurentoOptsResponse.addProperty("videoMinRecvBandwidth",
                                kurentoTokenOptions.getVideoMinRecvBandwidth());
                    }
                    if (kurentoTokenOptions.getVideoMaxSendBandwidth() != null) {
                        kurentoOptsResponse.addProperty("videoMaxSendBandwidth",
                                kurentoTokenOptions.getVideoMaxSendBandwidth());
                    }
                    if (kurentoTokenOptions.getVideoMinSendBandwidth() != null) {
                        kurentoOptsResponse.addProperty("videoMinSendBandwidth",
                                kurentoTokenOptions.getVideoMinSendBandwidth());
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
                        HttpStatus.INTERNAL_SERVER_ERROR);
            } finally {
                session.closingLock.readLock().unlock();
            }
        } else {
            log.error("Session {} is in the process of closing. Token couldn't be generated", sessionId);
            return generateErrorMessage(
                    "Session " + sessionId + " not found",
                    SESSION_TOKEN_METHOD,
                    HttpStatus.NOT_FOUND);
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
                            return false;
                        }

                        this.sessionManager.closeSessionAndEmptyCollections(
                                sessionNotActive,
                                EndReason.sessionClosedByServer,
                                true);
                        return false;
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

    //
    public void createSession(Session sessionNotActive) {
        log.info("session create and sessionEventHandler is here");
        dataRepository.generateRoomSession(sessionNotActive.getSessionId());
    }

    public void joinSession(Participant participant, String sessionId, Set<Participant> existingParticipants, Integer transactionId) {
        log.info("session join and sessionEventHandler is here:[participant] {}", participant);
        log.info("session join and sessionEventHandler is here:[sessionId] {}", sessionId);
        log.info("session join and sessionEventHandler is here:[transactionId] {}", transactionId);
        log.info("session join and sessionEventHandler is here:[existingParticipants] {}", existingParticipants);
        dataRepository.joinSession(participant, sessionId);
    }

    public void leaveSession(Participant participant, String sessionId, Set<Participant> remainingParticipants, Integer transactionId, EndReason reason) {
        log.info("session leave and sessionEventHandler is here:[participant] {}", participant);
        log.info("session leave and sessionEventHandler is here:[reason] {}", participant.getClientMetadata());
        log.info("session leave and sessionEventHandler is here:[sessionId] {}", sessionId);
        log.info("session leave and sessionEventHandler is here:[remainingParticipants] {}", remainingParticipants);
        log.info("session leave and sessionEventHandler is here:[transactionId] {}", transactionId);
        log.info("session leave and sessionEventHandler is here:[reason] {}", reason);
        dataRepository.leaveSession(participant, sessionId);
    }

    public void destroySession(KurentoSession session, EndReason reason) {
        log.info("session destroy and sessionEventHandler is here: {}", session.getSessionId());
        dataRepository.destroySession(session.getSessionId());
    }

    public boolean evictParticipant(String sessionId, String connectionId) {
        Session session = this.sessionManager.getSessionWithNotActive(sessionId);
        if(session == null) {
            return false;
        }

        Participant participant = session.getParticipantByPublicId(connectionId);
        if (participant != null) {
            this.sessionManager.evictParticipant(participant, null, null, EndReason.forceDisconnectByServer);
            return true;
            //return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return false;
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }






}
