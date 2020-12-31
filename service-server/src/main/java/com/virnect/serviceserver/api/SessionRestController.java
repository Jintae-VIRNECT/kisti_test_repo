package com.virnect.serviceserver.api;

import com.google.gson.JsonObject;
import com.virnect.data.dao.SessionType;
import com.virnect.service.ApiResponse;
import com.virnect.service.api.ISessionRestAPI;
import com.virnect.service.constraint.LicenseItem;
import com.virnect.service.constraint.PushConstants;
import com.virnect.service.dto.PageRequest;
import com.virnect.service.dto.ResultResponse;
import com.virnect.service.dto.feign.PushResponse;
import com.virnect.service.dto.service.request.*;
import com.virnect.service.dto.service.response.*;
import com.virnect.service.error.ErrorCode;
import com.virnect.service.error.exception.RestServiceException;
import com.virnect.serviceserver.data.DataProcess;
import com.virnect.serviceserver.data.FileDataRepository;
import com.virnect.serviceserver.data.SessionDataRepository;
import com.virnect.serviceserver.session.ServiceSessionManager;
import com.virnect.serviceserver.utils.LogMessage;
import com.virnect.serviceserver.utils.PushMessageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SessionRestController implements ISessionRestAPI {
    private static final String TAG = SessionRestController.class.getSimpleName();
    private static final String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    private static final String REST_PATH = "/remote/room";

    private SessionDataRepository sessionDataRepository;
    private FileDataRepository fileDataRepository;
    private PushMessageClient pushMessageClient;

    private final ServiceSessionManager serviceSessionManager;

    @Qualifier(value = "pushMessageClient")
    @Autowired
    public void setPushMessageClient(PushMessageClient pushMessageClient) {
        this.pushMessageClient = pushMessageClient;
    }

    @Qualifier(value = "sessionDataRepository")
    @Autowired
    public void setSessionDataRepository(SessionDataRepository sessionDataRepository) {
        this.sessionDataRepository = sessionDataRepository;
    }

    @Qualifier(value = "fileDataRepository")
    @Autowired
    public void setFileDataRepository(FileDataRepository fileDataRepository) {
        this.fileDataRepository = fileDataRepository;
    }

    @Deprecated
    private HttpHeaders getResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }

    private boolean IsValidUserCapacity(RoomRequest roomRequest, LicenseItem licenseItem) {
        // check room request member count is over
        return roomRequest.getParticipantIds().size() + 1 <= licenseItem.getUserCapacity();
    }

    @Deprecated
    private LicenseItem IsValidCompanyCode(int companyCode) {
        return LicenseItem.getLicenseItem(companyCode);
        /*if (licenseItem == null) {
            ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                    new RoomResponse(),
                    ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE
            );
            return ResponseEntity.ok(apiResponse);
        }*/
    }

    @Override
    public ResponseEntity<ApiResponse<PushResponse>> sendPushMessageHandler(@Valid PushSendRequest pushSendRequest, BindingResult result) {
        LogMessage.formedInfo(
                TAG,
                "REST API: POST " + REST_PATH + "/message/push",
                "sendPushMessageHandler"
        );

        LogMessage.formedInfo(
                TAG,
                "REST API: POST "
                        + REST_PATH
                        + (pushSendRequest != null ? pushSendRequest.toString() : "{}")
                        + "/message/push",
                "sendPushMessageHandler"
        );

        if(result.hasErrors()) {
            result.getAllErrors().forEach(message ->
                    LogMessage.formedError(
                            TAG,
                            "REST API: POST " + REST_PATH + "/message/push",
                            "sendPushMessageHandler",
                            LogMessage.PARAMETER_ERROR,
                            message.toString()
                    )
            );
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<PushResponse> response = this.pushMessageClient.sendPush(pushSendRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponse<RoomResponse>> createRoomRequestHandler(
            String client,
            String userId,
            @Valid RoomRequest roomRequest,
            int companyCode,
            BindingResult result) {
        LogMessage.formedInfo(
                TAG,
                "REST API: POST " + REST_PATH +
                        (roomRequest != null ? roomRequest.toString() : "{}") + "\n"
                        + ("COMPANY CODE: " + companyCode) + "\n"
                        + ("REQ USERID: " + userId) + "\n"
                        + ("REQ HEADER: " + client),
                        "createRoomRequestHandler"
        );


        // check room request handler
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message ->
                    LogMessage.formedError(
                            TAG,
                            "REST API: POST " + REST_PATH,
                            "createRoomRequestHandler",
                            LogMessage.PARAMETER_ERROR,
                            message.toString()
                    )
            );
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        LicenseItem licenseItem = LicenseItem.getLicenseItem(companyCode);
        if (licenseItem == null) {
            ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                    new RoomResponse(),
                    ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE
            );
            return ResponseEntity.ok(apiResponse);
        }

        if(roomRequest.getSessionType().equals(SessionType.PRIVATE) || roomRequest.getSessionType().equals(SessionType.PUBLIC)) {
            // check room request member count is over
            if(IsValidUserCapacity(roomRequest, licenseItem)) {
                // generate session id and token
                JsonObject sessionJson = serviceSessionManager.generateSession();
                JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

                // create room
                ApiResponse<RoomResponse> apiResponse = this.sessionDataRepository.generateRoom(
                        roomRequest,
                        licenseItem,
                        userId,
                        sessionJson.toString(),
                        tokenResult.toString());

                return ResponseEntity.ok(apiResponse);
            } else {
                ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                        new RoomResponse(),
                        ErrorCode.ERR_ROOM_MEMBER_IS_OVER
                );
                return ResponseEntity.ok(apiResponse);
            }
        } else if (roomRequest.getSessionType().equals(SessionType.OPEN)) {
            //open session is not need to check member count.
            // generate session id and token
            JsonObject sessionJson = serviceSessionManager.generateSession();
            JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

            // create room
            ApiResponse<RoomResponse> apiResponse = this.sessionDataRepository.generateRoom(
                    roomRequest,
                    licenseItem,
                    userId,
                    sessionJson.toString(),
                    tokenResult.toString());
            return ResponseEntity.ok(apiResponse);
        } else {
            ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                    new RoomResponse(),
                    ErrorCode.ERR_ROOM_CREATE_FAIL
            );
            return ResponseEntity.ok(apiResponse);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<RoomResponse>> createRoomRequestHandler(@Valid RoomRequest roomRequest, int companyCode, BindingResult result) {
        LogMessage.formedInfo(
                TAG,
                "REST API: POST " + REST_PATH +
                        (roomRequest != null ? roomRequest.toString() : "{}") +
                        "company code: " + companyCode,
                "createRoomRequestHandler"
        );
        // check room request handler
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message ->
                    LogMessage.formedError(
                            TAG,
                            "REST API: POST " + REST_PATH,
                            "createRoomRequestHandler",
                            LogMessage.PARAMETER_ERROR,
                            message.toString()
                    )
            );
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        // check license item using company code if not virnect
        LicenseItem licenseItem = LicenseItem.getLicenseItem(companyCode);
        if (licenseItem == null) {
            ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                    new RoomResponse(),
                    ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE
            );
            return ResponseEntity.ok(apiResponse);
        }

        if(roomRequest.getSessionType().equals(SessionType.PRIVATE) || roomRequest.getSessionType().equals(SessionType.PUBLIC)) {
            // check room request member count is over
            if(IsValidUserCapacity(roomRequest, licenseItem)) {
                // generate session id and token
                JsonObject sessionJson = serviceSessionManager.generateSession();
                JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

                ApiResponse<RoomResponse> apiResponse = this.sessionDataRepository.generateRoom(
                        roomRequest,
                        licenseItem,
                        roomRequest.getLeaderId(),
                        sessionJson.toString(),
                        tokenResult.toString());

                return ResponseEntity.ok(apiResponse);
            } else {
                ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                        new RoomResponse(),
                        ErrorCode.ERR_ROOM_MEMBER_IS_OVER
                );
                return ResponseEntity.ok(apiResponse);
            }
        } else if (roomRequest.getSessionType().equals(SessionType.OPEN)) {
            // open session is not need to check member count.
            // generate session id and token
            JsonObject sessionJson = serviceSessionManager.generateSession();
            JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

            // create room
            ApiResponse<RoomResponse> apiResponse = this.sessionDataRepository.generateRoom(
                    roomRequest,
                    licenseItem,
                    roomRequest.getLeaderId(),
                    sessionJson.toString(),
                    tokenResult.toString());
            return ResponseEntity.ok(apiResponse);
        } else {
            ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                    new RoomResponse(),
                    ErrorCode.ERR_ROOM_CREATE_FAIL
            );
            return ResponseEntity.ok(apiResponse);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<RoomInfoListResponse>> getRoomList(
            @RequestParam(name = "workspaceId") String workspaceId,
            @RequestParam(name = "userId") String userId,
            @RequestParam("paging") boolean paging,
            @ApiIgnore PageRequest pageRequest
    ) {
        //@RequestParam(value = "webRtcStats", required = false, defaultValue = "false") boolean webRtcStats,
        LogMessage.formedInfo(
                TAG,
                "REST API: GET "
                        + REST_PATH + "/"
                        + (workspaceId != null ? workspaceId : "{}") + "/"
                        + (userId != null ? userId : "{}") ,
                "getRoomList"
        );

        ApiResponse<RoomInfoListResponse> apiResponse;
        if(paging) {
            apiResponse = this.sessionDataRepository.loadRoomPageList(workspaceId, userId, pageRequest.ofSortBy());
        } else {
            apiResponse = this.sessionDataRepository.loadRoomList(workspaceId, userId, pageRequest.ofSortBy());
        }
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<RoomInfoListResponse>> getRoomList(String workspaceId, String userId, String search, PageRequest pageRequest) {
        LogMessage.formedInfo(
                TAG,
                "REST API: GET "
                        + REST_PATH + "/"
                        + (workspaceId != null ? workspaceId : "{}")  + "/"
                        + (userId != null ? userId : "{}") + "/"
                        + (search != null ? search : "{}"),
                "getRoomList"
        );
        ApiResponse<RoomInfoListResponse> apiResponse = this.sessionDataRepository.searchRoomPageList(workspaceId, userId, search, pageRequest.ofSortBy());
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<RoomDetailInfoResponse>> getRoomById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId) {
        LogMessage.formedInfo(
                TAG,
                "REST API: GET "
                        + REST_PATH + "/"
                        + (workspaceId != null ? workspaceId : "{}") + "/"
                        + (sessionId != null ? sessionId : "{}"),
                "getRoomById"
        );
        if (workspaceId.isEmpty() || sessionId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        return ResponseEntity.ok(
                this.sessionDataRepository.loadRoom(workspaceId, sessionId)
        );
    }

    @Override
    public ResponseEntity<ApiResponse<RoomDeleteResponse>> deleteRoomById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @PathVariable("userId") String userId) {
        LogMessage.formedInfo(
                TAG,
                "REST API: DELETE "
                        + REST_PATH + "/"
                        + (workspaceId != null ? workspaceId : "{}") + "/"
                        + (sessionId != null ? sessionId : "{}") + "/"
                        + (userId != null ? userId : "{}"),
                "deleteRoomById"
        );

        //check null or empty
        if(sessionId == null || sessionId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<RoomDeleteResponse> apiResponse = this.sessionDataRepository.removeRoom(workspaceId, sessionId, userId);

        if(apiResponse.getData().result) {
            //send rpc message to connection id user of the session id
            /*DataProcess<List<String>> dataProcess = this.sessionDataRepository.getConnectionIds(workspaceId, sessionId);
            JsonObject jsonObject = serviceSessionManager.generateMessage(
                    sessionId,
                    dataProcess.getData(),
                    PushConstants.PUSH_SIGNAL_SYSTEM,
                    PushConstants.SEND_PUSH_ROOM_CLOSED
            );*/

            if(this.serviceSessionManager.closeActiveSession(sessionId)) {
                LogMessage.formedInfo(
                        TAG,
                        "serviceSessionManager",
                        "closeActiveSession"
                );
                this.fileDataRepository.removeFiles(workspaceId, sessionId);
                return ResponseEntity.ok(apiResponse);
            }

            if(this.serviceSessionManager.closeNotActiveSession(sessionId)) {
                LogMessage.formedInfo(
                        TAG,
                        "serviceSessionManager",
                        "closeNotActiveSession"
                );
                this.fileDataRepository.removeFiles(workspaceId, sessionId);
                return ResponseEntity.ok(apiResponse);
            }
        }
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<RoomDetailInfoResponse>> updateRoomById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid ModifyRoomInfoRequest modifyRoomInfoRequest,
            BindingResult result
    ) {
        LogMessage.formedInfo(
                TAG,
                "REST API: POST "
                        + REST_PATH + "/"
                        + (workspaceId != null ? workspaceId : "{}") + "/"
                        + (sessionId != null ? sessionId : "{}") + "/"
                        + "info",
                "updateRoomById"
        );

        if(result.hasErrors()) {
            result.getAllErrors().forEach(message ->
                    LogMessage.formedError(
                            TAG,
                            "REST API: POST " + REST_PATH,
                            "updateRoomById",
                            LogMessage.PARAMETER_ERROR,
                            message.toString()
                    )
            );
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        return ResponseEntity.ok(
                this.sessionDataRepository.updateRoom(workspaceId, sessionId, modifyRoomInfoRequest)
        );
    }

    @Override
    public ResponseEntity<ApiResponse<RoomResponse>> joinRoomById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid JoinRoomRequest joinRoomRequest,
            BindingResult result
    ) {
        LogMessage.formedInfo(
                TAG,
                "REST API: POST "
                        + REST_PATH + "/"
                        + (workspaceId != null ? workspaceId : "{}") + "/"
                        + (sessionId != null ? sessionId : "{}") + "/"
                        + (joinRoomRequest != null ? joinRoomRequest : "{}") + "/"
                        + "join",
                "joinRoomById"
        );

        if(result.hasErrors()) {
            result.getAllErrors().forEach(message ->
                    LogMessage.formedError(
                            TAG,
                            "REST API: POST " + REST_PATH,
                            "joinRoomById",
                            LogMessage.PARAMETER_ERROR,
                            message.toString()
                    )
            );
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        // generate session id and token
        JsonObject sessionJson = serviceSessionManager.generateSession(sessionId);
        JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);
        //
        return ResponseEntity.ok(
                this.sessionDataRepository.joinRoom(workspaceId, sessionId, tokenResult.toString(), joinRoomRequest)
        );
    }

    @Override
    public ResponseEntity<ApiResponse<ResultResponse>> exitRoomById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @RequestParam("userId") String userId
    ) {
        LogMessage.formedInfo(
                TAG,
                "REST API: POST "
                        + REST_PATH + "/"
                        + (workspaceId != null ? workspaceId : "{}") + "/"
                        + (sessionId != null ? sessionId : "{}") + "/"
                        + (userId != null ? userId : "{}") + "/"
                        + "exit",
                "exitRoomById"
        );
        if(sessionId.isEmpty() || userId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        return ResponseEntity.ok(
                this.sessionDataRepository.exitRoom(workspaceId, sessionId, userId)
        );
    }

    @Override
    public ResponseEntity<ApiResponse<ResultResponse>> inviteMember(
            String workspaceId,
            String sessionId,
            @Valid InviteRoomRequest inviteRoomRequest,
            BindingResult result) {

        LogMessage.formedInfo(
                TAG,
                "REST API: POST "
                        + REST_PATH + "/"
                        + (workspaceId != null ? workspaceId : "{}") + "/"
                        + (sessionId != null ? sessionId : "{}") + "/"
                        + (inviteRoomRequest != null ? inviteRoomRequest.toString() : "{}") + "/"
                        + "member",
                "inviteMember"
        );

        if(result.hasErrors()) {
            result.getAllErrors().forEach(message ->
                    LogMessage.formedError(
                            TAG,
                            "REST API: POST " + REST_PATH,
                            "inviteMember",
                            LogMessage.PARAMETER_ERROR,
                            message.toString()
                    )
            );
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<InviteRoomResponse> response = this.sessionDataRepository.inviteMember(workspaceId, sessionId, inviteRoomRequest);
        ApiResponse<ResultResponse> resultResponse;
        if(response.getCode() == ErrorCode.ERR_SUCCESS.getCode()) {
            //send push message
            this.sessionDataRepository.sendInviteMessage(response.getData());

            resultResponse = new ApiResponse<>(
                    new ResultResponse(inviteRoomRequest.getLeaderId(), true, LocalDateTime.now(), new HashMap<>())
            );
        } else {
            resultResponse = new ApiResponse<>(new ResultResponse());
            resultResponse.setCode(response.getCode());
            resultResponse.setMessage(response.getMessage());
        }
        return ResponseEntity.ok(resultResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<ResultResponse>> kickOutMember(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid KickRoomRequest kickRoomRequest,
            BindingResult result
    ) {
        LogMessage.formedInfo(
                TAG,
                "REST API: DELETE "
                        + REST_PATH + "/"
                        + (workspaceId != null ? workspaceId : "{}") + "/"
                        + (sessionId != null ? sessionId : "{}") + "/"
                        + (kickRoomRequest != null ? kickRoomRequest.toString() : "{}") + "/"
                        + "member",
                "kickOutMember"
        );

        if(result.hasErrors()) {
            result.getAllErrors().forEach(message ->
                    LogMessage.formedError(
                            TAG,
                            "REST API: DELETE " + REST_PATH,
                            "kickOutMember",
                            LogMessage.PARAMETER_ERROR,
                            message.toString()
                    )
            );
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<KickRoomResponse> apiResponse = this.sessionDataRepository.kickFromRoom(workspaceId, sessionId, kickRoomRequest);
        ApiResponse<ResultResponse> resultResponse = null;
        if(apiResponse.getCode() == ErrorCode.ERR_SUCCESS.getCode()) {
            String connectionId = apiResponse.getData().getConnectionId();
            if(connectionId == null || connectionId.isEmpty()) {
                //send push message
                this.sessionDataRepository.sendEvictMessage(apiResponse.getData());
                resultResponse = new ApiResponse<>(new ResultResponse(
                        kickRoomRequest.getLeaderId(), true, LocalDateTime.now(), new HashMap<>()
                ));
            } else {
                //send rpc message to connection id user of the session id
                JsonObject jsonObject = serviceSessionManager.generateMessage(
                        sessionId,
                        Arrays.asList(connectionId),
                        PushConstants.PUSH_SIGNAL_SYSTEM,
                        PushConstants.SEND_PUSH_ROOM_EVICT
                );

                if(jsonObject.has("error")) {
                    log.info("sendSignal :{}", jsonObject.get("error").getAsString());
                    log.info("sendSignal :{}", jsonObject.get("status").getAsString());
                    log.info("sendSignal :{}", jsonObject.get("message").getAsString());
                    resultResponse = new ApiResponse<>(new ResultResponse());
                    resultResponse.setCode(Integer.parseInt(jsonObject.get("status").getAsString()));
                    resultResponse.setMessage(jsonObject.get("message").getAsString());
                } else {
                    //send force disconnected
                    //todo:forceResult when get false do process something.
                    boolean forceResult = serviceSessionManager.evictParticipant(sessionId, connectionId);
                    log.info("evictParticipant :{}", forceResult);
                    resultResponse = new ApiResponse<>(new ResultResponse(
                            kickRoomRequest.getLeaderId(), true, LocalDateTime.now(), new HashMap<>()
                    ));
                }
            }
        } else {
            resultResponse = new ApiResponse<>(new ResultResponse());
            resultResponse.setCode(apiResponse.getCode());
            resultResponse.setMessage(apiResponse.getMessage());
        }
        return ResponseEntity.ok(resultResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<ResultResponse>> sendSignal(
            String workspaceId,
            @Valid SendSignalRequest sendSignalRequest,
            BindingResult result) {

        LogMessage.formedInfo(
                TAG,
                "REST API: POST "
                        + REST_PATH
                        + (workspaceId != null ? workspaceId : "{}")
                        + (sendSignalRequest != null ? sendSignalRequest.toString() : "{}")
                        + "/signal",
                "inviteMember"
        );

        if(result.hasErrors()) {
            result.getAllErrors().forEach(message ->
                    LogMessage.formedError(
                            TAG,
                            "REST API: POST " + REST_PATH,
                            "sendSignal",
                            LogMessage.PARAMETER_ERROR,
                            message.toString()
                    )
            );
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        //Assert.assertTrue(jsonObject.get("name").getAsString().equals("remote"));
        //Assert.assertTrue(jsonObject.get("java").getAsBoolean() == true);

        ApiResponse<ResultResponse> apiResponse = new ApiResponse<>();
        ResultResponse response = new ResultResponse();
        JsonObject jsonObject = serviceSessionManager.generateMessage(
                sendSignalRequest.getSessionId(),
                sendSignalRequest.getTo(),
                sendSignalRequest.getType(),
                sendSignalRequest.getData()
                );
        if(jsonObject.has("error")) {
            log.info("sendSignal :{}", jsonObject.get("error").getAsString());
            log.info("sendSignal :{}", jsonObject.get("status").getAsString());
            log.info("sendSignal :{}", jsonObject.get("message").getAsString());
            apiResponse.setCode(Integer.parseInt(jsonObject.get("status").getAsString()));
            apiResponse.setMessage(jsonObject.get("message").getAsString());
        } else {
            response.setResult(true);
        }
        apiResponse.setData(response);
        return ResponseEntity.ok(apiResponse);
    }
}
