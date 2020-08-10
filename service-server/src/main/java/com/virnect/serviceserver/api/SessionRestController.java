package com.virnect.serviceserver.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.virnect.api.ApiResponse;
import com.virnect.api.constants.ServiceConstants;
import com.virnect.api.dto.request.*;
import com.virnect.api.dto.response.*;
import com.virnect.api.error.ErrorCode;
import com.virnect.api.error.exception.RestServiceException;
import com.virnect.api.service.SessionService;
import com.virnect.serviceserver.core.*;
import com.virnect.serviceserver.core.Session;
import com.virnect.java.client.*;
import com.virnect.serviceserver.gateway.model.SessionData;
import com.virnect.serviceserver.gateway.model.SessionTokenData;
import com.virnect.serviceserver.kurento.core.KurentoTokenOptions;
import com.virnect.serviceserver.session.ServiceSessionManager;
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
public class SessionRestController {
    private static final String TAG = SessionRestController.class.getSimpleName();
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    private static final String REST_PATH = "/remote/room";

    private final SessionService sessionService;

    //private final SessionManager sessionManager;
    private final ServiceSessionManager serviceSessionManager;

    private JsonObject generateErrorMessage(String errorMessage, String path, HttpStatus status) {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("timestamp", System.currentTimeMillis());
        responseJson.addProperty("status", status.value());
        responseJson.addProperty("error", status.getReasonPhrase());
        responseJson.addProperty("message", errorMessage);
        responseJson.addProperty("path", path);
        return responseJson;
    }

    @Deprecated
    private HttpHeaders getResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
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
    @ApiOperation(value = "Initialize a Remote Room ", notes = "원격협업 방을 생성 합니다.")
    @ApiImplicitParams({
    })
    @PostMapping(value = "room")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoomRequestHandler(
            @RequestBody @Valid RoomRequest roomRequest,
            @ModelAttribute RoomProfileUpdateRequest roomProfileUpdateRequest,
            BindingResult result) {
        log.info("REST API: POST {}/{}", REST_PATH, roomRequest != null ? roomRequest.toString() : "{}");

        // 1. check room request handler
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        // 2. check room request member count is over
        if(roomRequest.getLeaderId() != null && roomRequest.getParticipants().size() > (ServiceConstants.PRODUCT_BASIC_MAX_USER - 1)) {
            throw new RestServiceException(ErrorCode.ERR_ROOM_MEMBER_IS_OVER);
        }

        // 3. check user license type using user uuid
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


        // 4. generate session id and token
        JsonObject sessionJson = serviceSessionManager.generateSession();

        //Auto generate Token
        SessionTokenData tokenData = new SessionTokenData();
        tokenData.setSession(sessionNotActive.getSessionId());
        tokenData.setRole("PUBLISHER");
        tokenData.setData("");

        //JsonObject tokenResult = generateSessionToken(tokenData);
        JsonObject tokenResult = serviceSessionManager.generateSessionToken(tokenData);

        // 5. create room
        ApiResponse<RoomResponse> roomResponse = this.sessionService.createRoom(roomRequest, roomProfileUpdateRequest, sessionJson.toString(), tokenResult.toString());
        return ResponseEntity.ok(roomResponse);

        //JsonObject sessionJson = generateSession(null);
        /*if(sessionJson.get("error").toString().equals(HttpStatus.OK.getReasonPhrase())) {
            log.info("New session {} initialized", sessionJson.get("id").toString());
            //Auto generate Token
            SessionTokenData tokenData = new SessionTokenData();
            //tokenData.setSession(sessionNotActive.getSessionId());
            tokenData.setSession(sessionJson.get("id").toString());
            tokenData.setRole("PUBLISHER");
            tokenData.setData("");

            JsonObject tokenResult = generateSessionToken(tokenData);
            if(tokenResult.get("error").toString().equals(HttpStatus.OK.getReasonPhrase())) {
                log.info("New session token generated {}", tokenResult);
                // 4. create room
                ApiResponse<RoomResponse> roomResponse = this.remoteGatewayService.createRoom(roomRequest, roomProfileUpdateRequest, sessionJson.toString(), tokenResult.toString());
                return ResponseEntity.ok(roomResponse);
            } else {
                ApiResponse<RoomResponse> response = new ApiResponse<>(new RoomResponse());
                response.setMessage(tokenResult.toString());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } else {
            ApiResponse<RoomResponse> response = new ApiResponse<>(new RoomResponse());
            response.setMessage(sessionJson.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }*/
    }

    @ApiOperation(value = "Load Room Information List", notes = "원격협헙 방 리스트 조회하는 API 입니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paging", value = "검색 결과 페이지네이션 여부", dataType = "boolean", allowEmptyValue = true, defaultValue = "false"),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(1부터 시작)", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
    })
    @GetMapping(value = "room")
    public ResponseEntity<ApiResponse<RoomInfoListResponse>> getRoomList(
            @RequestParam(name = "workspaceId") String workspaceId,
            @RequestParam(name = "userId") String userId,
            @RequestParam("paging") boolean paging,
            @ApiIgnore PageRequest pageable
    ) {
        //@RequestParam(value = "webRtcStats", required = false, defaultValue = "false") boolean webRtcStats,
        log.info("REST API: GET {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", userId != null ? userId : "{}");
        ApiResponse<RoomInfoListResponse> apiResponse = this.sessionService.getRoomInfoList(workspaceId, userId, paging, pageable.of());
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Load Room Detail Information", notes = "특정 원격협업 방 상세 정보를 조회하는 API 입니다.")
    @GetMapping(value = "room/{workspaceId}/{sessionId}")
    public ResponseEntity<ApiResponse<RoomDetailInfoResponse>> getRoomById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId) {
        log.info("REST API: GET {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId.toString() : "{}", sessionId != null ? sessionId.toString() : "{}");
        if (workspaceId.isEmpty() || sessionId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<RoomDetailInfoResponse> apiResponse = this.sessionService.getRoomInfoBySessionId(workspaceId, sessionId);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Delete Specific Room", notes = "특정 원격협업 방을 삭제하는 API 입니다.")
    @DeleteMapping(value = "room/{workspaceId}/{sessionId}/{userId}")
    public ResponseEntity<ApiResponse<ResultResponse>> deleteRoomById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @PathVariable("userId") String userId) {
        log.info("REST API: DELETE {}/{}/{}", REST_PATH,
                workspaceId != null ? workspaceId.toString() : "{}",
                sessionId != null ? sessionId : "{}",
                userId != null ? userId : "{}");
        if(sessionId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        /*if(!remoteGatewayService.getUserGrantValidity(sessionId, userId).equals(MemberType.LEADER)) {
            throw new RemoteServiceException(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
        }*/

        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setResult(false);
        ApiResponse<ResultResponse> apiResponse = this.sessionService.removeRoom(workspaceId, sessionId, userId);
        //apiResponse = this.remoteGatewayService.removeAllRoom(workspaceId);
        if(apiResponse.getData().getResult()) {
            Session session = this.sessionManager.getSession(sessionId);
            if (session != null) {
                log.info("REST API: DELETE closeSession");
                this.sessionManager.closeSession(sessionId, EndReason.sessionClosedByServer);
                //apiResponse = this.remoteGatewayService.removeRoom(workspaceId, sessionId);
                //return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
                return ResponseEntity.ok(apiResponse);
            }

            Session sessionNotActive = this.sessionManager.getSessionNotActive(sessionId);
            if (sessionNotActive != null) {
                try {
                    if (sessionNotActive.closingLock.writeLock().tryLock(15, TimeUnit.SECONDS)) {
                        try {
                            log.info("REST API: DELETE close sessionNotActive");
                            if (sessionNotActive.isClosed()) {
                                //ResultResponse resultResponse = new ResultResponse();
                                //resultResponse.setResult(false);
                                //return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
                                apiResponse.getData().setResult(false);
                                return ResponseEntity.ok(apiResponse);

                            }

                            this.sessionManager.closeSessionAndEmptyCollections(
                                    sessionNotActive,
                                    EndReason.sessionClosedByServer,
                                    true);
                            //apiResponse = this.remoteGatewayService.removeRoom(workspaceId, sessionId);
                            return ResponseEntity.ok(apiResponse);
                            //return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
                        } finally {
                            sessionNotActive.closingLock.writeLock().unlock();
                        }
                    } else {
                        String errorMsg = "Timeout waiting for Session " + sessionId
                                + " closing lock to be available for closing from DELETE /api/sessions";
                        log.error(errorMsg);
                        //apiResponse.setData(false);
                        apiResponse.getData().setResult(false);
                        return ResponseEntity.ok(apiResponse);
                        //apiResponse.setMessage(generateErrorMessage(errorMsg, "/api/sessions", HttpStatus.BAD_REQUEST).toString());
                        //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
                    }
                } catch (InterruptedException e) {
                    String errorMsg = "InterruptedException while waiting for Session " + sessionId
                            + " closing lock to be available for closing from DELETE /api/sessions";
                    log.error(errorMsg);
                /*apiResponse.setData(false);
                apiResponse.setMessage(generateErrorMessage(errorMsg, "/api/sessions", HttpStatus.BAD_REQUEST).toString());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);*/
                    apiResponse.getData().setResult(false);
                    return ResponseEntity.ok(apiResponse);
                }
            } else {
                //return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
                return ResponseEntity.ok(apiResponse);
            }
        } else {
            return ResponseEntity.ok(apiResponse);
        }

        /*Session session = this.sessionManager.getSession(sessionId);
        if (session != null) {
            log.info("REST API: DELETE closeSession");
            this.sessionManager.closeSession(sessionId, EndReason.sessionClosedByServer);
            apiResponse = this.remoteGatewayService.removeRoom(workspaceId, sessionId);
            //return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
            return ResponseEntity.ok(apiResponse);
            //return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }


        Session sessionNotActive = this.sessionManager.getSessionNotActive(sessionId);
        if (sessionNotActive != null) {
            try {
                if (sessionNotActive.closingLock.writeLock().tryLock(15, TimeUnit.SECONDS)) {
                    try {
                        log.info("REST API: DELETE close sessionNotActive");

                        if (sessionNotActive.isClosed()) {
                            //ResultResponse resultResponse = new ResultResponse();
                            //resultResponse.setResult(false);
                            //return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
                            apiResponse.getData().setResult(false);
                            return ResponseEntity.ok(apiResponse);

                        }

                        this.sessionManager.closeSessionAndEmptyCollections(
                                sessionNotActive,
                                EndReason.sessionClosedByServer,
                                true);

                        apiResponse = this.remoteGatewayService.removeRoom(workspaceId, sessionId);
                        return ResponseEntity.ok(apiResponse);
                        //return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
                        //return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                    } finally {
                        sessionNotActive.closingLock.writeLock().unlock();
                    }
                } else {
                    String errorMsg = "Timeout waiting for Session " + sessionId
                            + " closing lock to be available for closing from DELETE /api/sessions";
                    log.error(errorMsg);
                    //apiResponse.setData(false);
                    apiResponse.getData().setResult(false);
                    return ResponseEntity.ok(apiResponse);
                    //apiResponse.setMessage(generateErrorMessage(errorMsg, "/api/sessions", HttpStatus.BAD_REQUEST).toString());
                    //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
                }
            } catch (InterruptedException e) {
                String errorMsg = "InterruptedException while waiting for Session " + sessionId
                        + " closing lock to be available for closing from DELETE /api/sessions";
                log.error(errorMsg);
                *//*apiResponse.setData(false);
                apiResponse.setMessage(generateErrorMessage(errorMsg, "/api/sessions", HttpStatus.BAD_REQUEST).toString());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);*//*
                apiResponse.getData().setResult(false);
                return ResponseEntity.ok(apiResponse);
            }
        } else {
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
            return ResponseEntity.ok(apiResponse);
        }*/
    }

    @ApiOperation(value = "Update Room Information", notes = "특정 원격협업 방 상세 정보를 수정하는 API 입니다.")
    @PostMapping(value = "room/{workspaceId}/{sessionId}/info")
    public ResponseEntity<ApiResponse<RoomDetailInfoResponse>> updateRoomById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid ModifyRoomInfoRequest modifyRoomInfoRequest,
            @ModelAttribute RoomProfileUpdateRequest roomProfileUpdateRequest,
            BindingResult result
    ) {
        log.info("REST API: POST {}/{}/info", REST_PATH,
                workspaceId != null ? workspaceId.toString() : "{}",
                sessionId != null ? sessionId : "{}");
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<RoomDetailInfoResponse> apiResponse = this.sessionService.modifyRoomInfo(workspaceId, sessionId, modifyRoomInfoRequest, roomProfileUpdateRequest);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Join a Specific Room", notes = "특정 원격협업 방에 접속하는 API 입니다.")
    @PostMapping(value = "room/{workspaceId}/{sessionId}/join")
    public ResponseEntity<ApiResponse<RoomResponse>> joinRoomById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid JoinRoomRequest joinRoomRequest,
            BindingResult result
    ) {
        log.info("REST API: POST {}/{}/{}/join {}", REST_PATH,
                workspaceId != null ? workspaceId.toString() : "{}",
                sessionId != null ? sessionId : "{}",
                joinRoomRequest != null ? joinRoomRequest.toString() : "{}");
        if (result.hasErrors()) {
            log.info("has errors");
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        else {
            log.info("has no errors");
        }

        // 3. generate session id and token
        SessionData sessionData = new SessionData();
        SessionProperties.Builder builder = new SessionProperties.Builder();
        //String customSessionId = sessionData.getCustomSessionId();
        String customSessionId = sessionId;
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
        //String sessionId;
        //todo: conflict when session is not created.
        /*if (customSessionId != null && !customSessionId.isEmpty()) {
            if (sessionManager.getSessionWithNotActive(customSessionId) != null) {
                log.info("has CONFLICT: " + sessionManager.getSessionWithNotActive(customSessionId));
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            //sessionId = customSessionId;
        } else {
            customSessionId = IdentifierPrefixes.SESSION_ID + RandomStringUtils.randomAlphabetic(1).toUpperCase()
                    + RandomStringUtils.randomAlphanumeric(9);
        }*/

        Session sessionNotActive = sessionManager.storeSessionNotActive(customSessionId, sessionProperties);
        log.info("New session {} initialized {}", customSessionId, this.sessionManager.getSessionsWithNotActive().stream()
                .map(Session::getSessionId).collect(Collectors.toList()).toString());
        JsonObject sessionJson = new JsonObject();
        sessionJson.addProperty("id", sessionNotActive.getSessionId());
        sessionJson.addProperty("createdAt", sessionNotActive.getStartTime());

        //Auto generate Token
        SessionTokenData tokenData = new SessionTokenData();
        tokenData.setSession(sessionNotActive.getSessionId());
        tokenData.setRole("PUBLISHER");
        tokenData.setData("");

        JsonObject tokenResult = generateSessionToken(tokenData);

        ApiResponse<RoomResponse> apiResponse = this.sessionService.joinRoom(workspaceId, customSessionId, tokenResult.toString(), joinRoomRequest);
        return ResponseEntity.ok(apiResponse);
        //generate session and token
        /*JsonObject sessionJson = generateSession(sessionId);
        if(sessionJson.get("error").equals(HttpStatus.OK.getReasonPhrase())) {
            //Auto generate Token
            SessionTokenData tokenData = new SessionTokenData();
            tokenData.setSession(sessionJson.get("id").toString());
            tokenData.setRole("PUBLISHER");
            tokenData.setData("");

            JsonObject tokenResult = generateSessionToken(tokenData);
            if(tokenResult.get("error").toString().equals(HttpStatus.OK.getReasonPhrase())) {
                log.info("New session token generated {}", tokenResult);
                // join room
                ApiResponse<RoomResponse> apiResponse = this.remoteGatewayService.joinRoom(workspaceId, sessionId, tokenResult.toString(), joinRoomRequest);
                return ResponseEntity.ok(apiResponse);
            } else {
                ApiResponse<RoomResponse> response = new ApiResponse<>(new RoomResponse());
                response.setMessage(tokenResult.toString());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } else {
            ApiResponse<RoomResponse> response = new ApiResponse<>(new RoomResponse());
            response.setMessage(sessionJson.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }*/
    }

    @ApiOperation(value = "Exit Specific Room", notes = "특정 원격협업 방을 나가는 API 입니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionId", value = "원격협업 방 Session ID", dataType = "string", defaultValue = "", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "사용자 uuid", dataType = "string", defaultValue = "", paramType = "query", required = true),
    })
    @DeleteMapping(value = "room/{workspaceId}/{sessionId}/exit")
    public ResponseEntity<ApiResponse<ResultResponse>> exitRoomById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @RequestParam("userId") String userId
    ) {
        log.info("REST API: POST {}/{}/{}/exit {}", REST_PATH,
                workspaceId != null ? workspaceId.toString() : "{}",
                sessionId != null ? sessionId : "{}",
                userId != null ? userId.toString() : "{}");
        if(sessionId.isEmpty() || userId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<ResultResponse> apiResponse = this.sessionService.exitRoom(workspaceId, sessionId, userId);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "Invite a Member to Specific Room", notes = "특정 멤버를 원격협업 방에 초대하는 API 입니다.")
    @PostMapping(value = "room/{workspaceId}/{sessionId}/member")
    public ResponseEntity<ApiResponse<InviteRoomResponse>> inviteMember(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid InviteRoomRequest inviteRoomRequest,
            BindingResult result
    ) {
        log.info(TAG, "inviteMember");

        if (result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<InviteRoomResponse> apiResponse = this.sessionService.inviteRoom(workspaceId, sessionId, inviteRoomRequest);
        return ResponseEntity.ok(apiResponse);
    }

    /*@ApiOperation(value = "Accept Invitation from a Specific Room", notes = "초대받은 사용자가 초대를 수락 및 거절 하는 API 입니다.")
    @PostMapping(value = "room/{workspaceId}/{sessionId}/member/accept")
    public ResponseEntity<ApiResponse<Boolean>> inviteMemberAccept(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @RequestParam("userId") String userId,
            @RequestParam("accept") Boolean accept,
            @ApiIgnore Locale locale
    ) {
        log.info(TAG, "inviteMember");

        if(sessionId.isEmpty() || userId.isEmpty()) {
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<Boolean> apiResponse = this.remoteGatewayService.inviteRoomAccept(workspaceId, sessionId, userId, accept, locale);

        return ResponseEntity.ok(apiResponse);
    }*/


    /*public ResponseEntity<ApiResponse<Boolean>> kickOutMember(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid KickRoomRequest kickRoomRequest,
            BindingResult result
    )*/
    @ApiOperation(value = "Kick out a specific member from a specific room", notes = "특정 멤버를 원격협업 방에서 내보내는 API 입니다.")
    @DeleteMapping(value = "room/{workspaceId}/{sessionId}/member")
    public ResponseEntity<ApiResponse<ResultResponse>> kickOutMember(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid KickRoomRequest kickRoomRequest,
            BindingResult result
    ) {
        log.info("REST API: DELETE {}/{}/{}/member {}", REST_PATH,
                workspaceId != null ? workspaceId.toString() : "{}",
                sessionId != null ? sessionId : "{}",
                kickRoomRequest != null ? kickRoomRequest.toString() : "{}");

        if (result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        //ApiResponse<Boolean> apiResponse = new ApiResponse<>(true);
        ApiResponse<ResultResponse> apiResponse = this.sessionService.kickFromRoom(workspaceId, sessionId, kickRoomRequest);
        Session session = this.sessionManager.getSessionWithNotActive(sessionId);
        if (session == null) {
            //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
            apiResponse.getData().setResult(false);
            return ResponseEntity.ok(apiResponse);
        }

        Participant participant = session.getParticipantByPublicId(kickRoomRequest.getConnectionId());
        if (participant != null) {
            this.sessionManager.evictParticipant(participant, null, null, EndReason.forceDisconnectByServer);
            //apiResponse = this.remoteGatewayService.kickFromRoom(workspaceId, sessionId, kickRoomRequest);
            return ResponseEntity.ok(apiResponse);
            //return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            apiResponse.getData().setResult(false);
            return ResponseEntity.ok(apiResponse);
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
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
