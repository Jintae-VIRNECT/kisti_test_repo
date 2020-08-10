package com.virnect.serviceserver.session;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.virnect.api.ApiResponse;
import com.virnect.api.dto.response.RoomResponse;
import com.virnect.java.client.*;
import com.virnect.serviceserver.core.IdentifierPrefixes;
import com.virnect.serviceserver.core.Session;
import com.virnect.serviceserver.core.SessionManager;
import com.virnect.serviceserver.gateway.model.SessionData;
import com.virnect.serviceserver.gateway.model.SessionTokenData;
import com.virnect.serviceserver.kurento.core.KurentoTokenOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ServiceSessionManager {
    private static final String TAG = ServiceSessionManager.class.getSimpleName();

    private final SessionManager sessionManager;

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
                    ApiResponse<RoomResponse> response = new ApiResponse<>(new RoomResponse());
                    response.setMessage(generateErrorMessage(
                            "Parameter \"customSessionId\" is wrong. Must be an alphanumeric string",
                            "/api/sessions",
                            HttpStatus.BAD_REQUEST).toString());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                builder = builder.customSessionId(customSessionId);
            }
            builder = builder.defaultCustomLayout((sessionData.getDefaultCustomLayout() != null) ? sessionData.getDefaultCustomLayout() : "");

        } catch (IllegalArgumentException e) {
            ApiResponse<RoomResponse> response = new ApiResponse<>(new RoomResponse());
            response.setMessage(generateErrorMessage(
                    "RecordingMode " + sessionData.getRecordingMode() + " | "
                            + "Default OutputMode " + sessionData.getDefaultOutputMode() + " | "
                            + "Default RecordingLayout " + sessionData.getDefaultRecordingLayout() + " | "
                            + "MediaMode " + sessionData.getMediaMode()
                            + ". Some parameter is not defined",
                    "/api/sessions",
                    HttpStatus.BAD_REQUEST).toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        SessionProperties sessionProperties = builder.build();
        String sessionId;
        if (customSessionId != null && !customSessionId.isEmpty()) {
            if (sessionManager.getSessionWithNotActive(customSessionId) != null) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
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

    public JsonObject generateSessionToken(SessionTokenData tokenData) {
        if (tokenData == null) {
            return generateErrorMessage("Error in body parameters. Cannot be empty", "",
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
            return generateErrorMessage("Type error in some parameter", "/api/tokens", HttpStatus.BAD_REQUEST);
        }

        if (sessionId == null) {
            return generateErrorMessage("\"session\" parameter is mandatory", "/api/tokens",
                    HttpStatus.BAD_REQUEST);
        }

        final Session session = this.sessionManager.getSessionWithNotActive(sessionId);
        if (session == null) {
            return generateErrorMessage("Session " + sessionId + " not found", "/api/tokens",
                    HttpStatus.NOT_FOUND);
        }

        JsonObject kurentoOptions = null;

        if (tokenData.getKurentoOptions() != null) {
            try {
                kurentoOptions = JsonParser.parseString(tokenData.getKurentoOptions().toString()).getAsJsonObject();
            } catch (Exception e) {
                return generateErrorMessage("Error in parameter 'kurentoOptions'. It is not a valid JSON object",
                        "/api/tokens", HttpStatus.BAD_REQUEST);
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
            return generateErrorMessage("Parameter role " + tokenData.getRole() + " is not defined", "/api/tokens",
                    HttpStatus.BAD_REQUEST);
        }

        KurentoTokenOptions kurentoTokenOptions = null;
        if (kurentoOptions != null) {
            try {
                kurentoTokenOptions = new KurentoTokenOptions(kurentoOptions);
            } catch (Exception e) {
                return generateErrorMessage("Type error in some parameter of 'kurentoOptions'", "/api/tokens",
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
                        "Error generating token for session " + sessionId + ": " + e.getMessage(), "/api/tokens",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            } finally {
                session.closingLock.readLock().unlock();
            }
        } else {
            log.error("Session {} is in the process of closing. Token couldn't be generated", sessionId);
            return generateErrorMessage("Session " + sessionId + " not found", "/api/tokens",
                    HttpStatus.NOT_FOUND);
        }
    }

}
