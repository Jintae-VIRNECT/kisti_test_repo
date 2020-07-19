package com.virnect.serviceserver.gateway.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.virnect.java.client.*;
import com.virnect.serviceserver.core.EndReason;
import com.virnect.serviceserver.core.IdentifierPrefixes;
import com.virnect.serviceserver.core.Session;
import com.virnect.serviceserver.core.SessionManager;
import com.virnect.serviceserver.gateway.application.RemoteGatewayService;
import com.virnect.serviceserver.gateway.domain.MemberType;
import com.virnect.serviceserver.gateway.dto.request.*;
import com.virnect.serviceserver.gateway.dto.response.*;
import com.virnect.serviceserver.gateway.exception.RemoteServiceException;
import com.virnect.serviceserver.gateway.global.common.ApiResponse;
import com.virnect.serviceserver.gateway.global.error.ErrorCode;
import com.virnect.serviceserver.gateway.model.SessionData;
import com.virnect.serviceserver.gateway.model.SessionTokenData;
import com.virnect.serviceserver.kurento.core.KurentoTokenOptions;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class RoomRestController {
    private static final String TAG = "RoomRestController";

    private final RemoteGatewayService remoteGatewayService;
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";

    //
    private final SessionManager sessionManager;

    /*private ResponseEntity<String> generateErrorResponse(String errorMessage, String path, HttpStatus status) {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("timestamp", System.currentTimeMillis());
        responseJson.addProperty("status", status.value());
        responseJson.addProperty("error", status.getReasonPhrase());
        responseJson.addProperty("message", errorMessage);
        responseJson.addProperty("path", path);
        return new ResponseEntity<>(responseJson.toString(), getResponseHeaders(), status);
    }*/

    private String generateErrorMessage(String errorMessage, String path, HttpStatus status) {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("timestamp", System.currentTimeMillis());
        responseJson.addProperty("status", status.value());
        responseJson.addProperty("error", status.getReasonPhrase());
        responseJson.addProperty("message", errorMessage);
        responseJson.addProperty("path", path);
        return responseJson.toString();
    }

    private HttpHeaders getResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }

    private String generateSessionToken(SessionTokenData tokenData) {
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
                return responseJson.toString();
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


    /**
     * 1. check room request handler
     * 2. check user license type using user uuid
     * 3. generate session id and token
     * 4. create room
     * 5. register user as a leader who creates the room
     * 6. register other users as a worker(participant), if the request contains other user information.
     * 7. return session id and token
     */
    @ApiOperation(value = "Initialize a Remote Room ", notes = "원격협업 방을 생성합니다.")
    @ApiImplicitParams({
    })
    @PostMapping(value = "room")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoomRequestHandler(
            @RequestBody @Valid RoomRequest roomRequest,
            @ModelAttribute RoomProfileUpdateRequest roomProfileUpdateRequest,
            BindingResult result) {
        log.info("REST API: POST /retmoe/room {}", roomRequest != null ? roomRequest.toString() : "{}");


        // 1. check room request handler
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        // 2. check user license type using user uuid
        //TODO : License check after test account has remote product
        /*ApiResponse<LicenseInfoListResponse> licenseInfoList = remoteGatewayService.getLicenseValidity(roomRequest.getWorkspaceId(), roomRequest.getLeaderId());
        LicenseInfoResponse licenseInfo = null;
        for (LicenseInfoResponse licenseInfoResponse : licenseInfoList.getData().getLicenseInfoList()) {
            if(licenseInfoResponse.getLicenseType().equals(LicenseConstants.PRODUCT_NAME)) {
                licenseInfo = licenseInfoResponse;
            }
        }
        if(licenseInfo != null) {
            if(licenseInfo.getStatus().equals(LicenseConstants.STATUS_UN_USE)) {
                throw new RemoteServiceException(ErrorCode.ERR_LICENSE_NOT_VALIDITY);
            }

            if(licenseInfo.getLicenseType().equals(LicenseConstants.LICENSE_TYPE)) {
                if(roomRequest.getParticipants().size() > ServiceConstants.PRODUCT_BASIC_MAX_USER) {
                    throw new RemoteServiceException(ErrorCode.ERR_ROOM_LICENSE_TYPE);
                }
                *//*if(!roomRequest.getParticipants().isEmpty() &&
                roomRequest.getParticipants().s)*//*
            } else {
                throw new RemoteServiceException(ErrorCode.ERR_LICENSE_UNEXPECTED_TYPE);
            }
        } else {
            throw new RemoteServiceException(ErrorCode.ERR_LICENSE_PRODUCT_VALIDITY);
        }*/


        // 3. generate session id and token
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
                            HttpStatus.BAD_REQUEST));
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
                    HttpStatus.BAD_REQUEST));
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

        //Auto generate Token
        SessionTokenData tokenData = new SessionTokenData();
        tokenData.setSession(sessionNotActive.getSessionId());
        tokenData.setRole("PUBLISHER");
        tokenData.setData("");


        String token = generateSessionToken(tokenData);
        log.info("New session token generated {}", token);


        // 4. create room
        ApiResponse<RoomResponse> roomResponse = this.remoteGatewayService.createRoom(roomRequest, roomProfileUpdateRequest, sessionJson.toString(), token);


        return ResponseEntity.ok(roomResponse);
    }



    @ApiOperation(value = "Load Room Information List", notes = "원격협헙 방 리스트 조회하는 API 입니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "search", value = "검색어(협업, 멤버 이름 검색)", dataType = "string", allowEmptyValue = true, defaultValue = "remote"),
            @ApiImplicitParam(name = "paging", value = "검색 결과 페이지네이션 여부", dataType = "boolean", allowEmptyValue = true, defaultValue = "false"),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(1부터 시작)", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
    })
    @GetMapping(value = "room")
    public ResponseEntity<ApiResponse<RoomInfoListResponse>> getRoomList(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam("paging") boolean paging,
            @RequestParam(value = "webRtcStats", required = false, defaultValue = "false") boolean webRtcStats,
            @ApiIgnore PageRequest pageable
    ) {
        log.debug("getRoomList");
        ApiResponse<RoomInfoListResponse> apiResponse = this.remoteGatewayService.getRoomInfoList(search, paging, pageable.of());
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Load Room Detail Information", notes = "특정 원격협업 방 상세 정보를 조회하는 API 입니다.")
    @GetMapping(value = "room/{sessionId}")
    public ResponseEntity<ApiResponse<RoomDetailInfoResponse>> getRoomById(@PathVariable("sessionId") String sessionId) {
        log.info(TAG, "getRoomById");
        if (sessionId.isEmpty()) {
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<RoomDetailInfoResponse> apiResponse = this.remoteGatewayService.getRoomInfoBySessionId(sessionId);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Delete Specific Room", notes = "특정 원격협업 방을 삭제하는 API 입니다.")
    @DeleteMapping(value = "room/{sessionId}/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteRoomById(
            @PathVariable("sessionId") String sessionId,
            @PathVariable("userId") String userId) {
        log.info("REST API: DELETE /retmoe/room/{}", sessionId != null ? sessionId : "{}");
        if(sessionId.isEmpty()) {
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        if(!remoteGatewayService.getUserGrantValidity(sessionId, userId).equals(MemberType.LEADER)) {
            throw new RemoteServiceException(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
        }

        ApiResponse<Boolean> apiResponse = new ApiResponse<>(true);
        Session session = this.sessionManager.getSession(sessionId);
        if (session != null) {
            log.info("REST API: DELETE closeSession");
            this.sessionManager.closeSession(sessionId, EndReason.sessionClosedByServer);
            apiResponse = this.remoteGatewayService.removeRoom(sessionId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
            //return ResponseEntity.ok(apiResponse);
            //return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        Session sessionNotActive = this.sessionManager.getSessionNotActive(sessionId);
        if (sessionNotActive != null) {
            try {
                if (sessionNotActive.closingLock.writeLock().tryLock(15, TimeUnit.SECONDS)) {
                    try {
                        log.info("REST API: DELETE close sessionNotActive");

                        if (sessionNotActive.isClosed()) {
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
                        }

                        this.sessionManager.closeSessionAndEmptyCollections(
                                sessionNotActive,
                                EndReason.sessionClosedByServer,
                                true);

                        apiResponse = this.remoteGatewayService.removeRoom(sessionId);
                        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
                        //return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                    } finally {
                        sessionNotActive.closingLock.writeLock().unlock();
                    }
                } else {
                    String errorMsg = "Timeout waiting for Session " + sessionId
                            + " closing lock to be available for closing from DELETE /api/sessions";
                    log.error(errorMsg);
                    apiResponse.setData(false);
                    apiResponse.setMessage(generateErrorMessage(errorMsg, "/api/sessions", HttpStatus.BAD_REQUEST));
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
                }
            } catch (InterruptedException e) {
                String errorMsg = "InterruptedException while waiting for Session " + sessionId
                        + " closing lock to be available for closing from DELETE /api/sessions";
                log.error(errorMsg);
                apiResponse.setData(false);
                apiResponse.setMessage(generateErrorMessage(errorMsg, "/api/sessions", HttpStatus.BAD_REQUEST));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }

    @ApiOperation(value = "Update Room Information", notes = "특정 원격협업 방 상세 정보를 수정하는 API 입니다.")
    @PostMapping(value = "room/{sessionId}/info")
    public ResponseEntity<ApiResponse<RoomDetailInfoResponse>> updateRoomById(
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid ModifyRoomInfoRequest modifyRoomInfoRequest,
            @ModelAttribute RoomProfileUpdateRequest roomProfileUpdateRequest,
            BindingResult result
    ) {
        log.info(TAG, "updateRoomById");
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<RoomDetailInfoResponse> apiResponse = this.remoteGatewayService.modifyRoomInfo(sessionId, modifyRoomInfoRequest, roomProfileUpdateRequest);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Join a Specific Room", notes = "특정 원격협업 방에 접속하는 API 입니다.")
    @PostMapping(value = "room/{sessionId}/join")
    public ResponseEntity<ApiResponse<Boolean>> joinRoomById(
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid JoinRoomRequest joinRoomRequest,
            BindingResult result
    ) {
        log.info("REST API: POST /retmoe/room/{}/join {}", sessionId != null ? sessionId : "{}",  joinRoomRequest != null ? joinRoomRequest.toString() : "{}");
        if (result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<Boolean> apiResponse = this.remoteGatewayService.joinRoom(sessionId, joinRoomRequest);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Leave Specific Room", notes = "특정 원격협업 방을 나가는 API 입니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionId", value = "원격협업 방 Session ID", dataType = "string", defaultValue = "", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "사용자 uuid", dataType = "string", defaultValue = "473b12854daa6afeb9e505551d1b2743", paramType = "query", required = true),
    })
    @DeleteMapping(value = "room/{sessionId}/leave")
    public ResponseEntity<ApiResponse<Boolean>> leaveRoomById(
            @PathVariable("sessionId") String sessionId,
            @RequestParam("userId") String userId
    ) {
        log.info(TAG, "leaveRoomById");
        if(sessionId.isEmpty() || userId.isEmpty()) {
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<Boolean> apiResponse = this.remoteGatewayService.leaveRoom(sessionId, userId);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Invite a Member to Specific Room", notes = "특정 멤버를 원격협업 방에 초대하는 API 입니다.")
    @PostMapping(value = "room/{sessionId}/member")
    public ResponseEntity<ApiResponse<Boolean>> inviteMember(
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid InviteRoomRequest inviteRoomRequest,
            BindingResult result
    ) {
        log.info(TAG, "inviteMember");

        if (result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<Boolean> apiResponse = this.remoteGatewayService.inviteRoom(sessionId, inviteRoomRequest);

        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Kick out a specific member from a specific room", notes = "특정 멤버를 원격협업 방에서 내보내는 API 입니다.")
    @DeleteMapping(value = "room/{sessionId}/member")
    public ResponseEntity<ApiResponse<Boolean>> kickOutMember(
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid KickRoomRequest kickRoomRequest,
            BindingResult result
    ) {
        log.info(TAG, "kickOutMember");

        if (result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<Boolean> apiResponse = this.remoteGatewayService.kickFromRoom(sessionId, kickRoomRequest);

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 특정 사용자 삭제 원격협업 방에서 삭제
     * 특정 사용자 원격협업 방에 추가
     * 특정 사용자
     */
    /*@ApiOperation(value = "Delete Specific Member from Room", notes = "특정 멤버를 원격협업 방에서 내보내는 API 입니다.")
    @GetMapping(value = "room/{sessionId}/participants")
    public ResponseEntity<?> getParticipantsByRoomId(@RequestParam("name") String name,
                                            @RequestParam("page") int page) {
        log.info(TAG, "getHistoryByRoomId");

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("name", "test");
        responseJson.addProperty("page", 10);

        return new ResponseEntity<>(responseJson.toString(), getResponseHeaders(), HttpStatus.OK);
    }*/
}
