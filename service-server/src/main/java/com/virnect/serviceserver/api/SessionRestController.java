package com.virnect.serviceserver.api;

import com.google.gson.JsonObject;
import com.virnect.data.ApiResponse;
import com.virnect.data.api.ISessionRestAPI;
import com.virnect.data.constraint.LicenseItem;
import com.virnect.data.constraint.PushConstants;
import com.virnect.data.constraint.ServiceConstants;
import com.virnect.data.dto.feign.PushResponse;
import com.virnect.data.dto.request.*;
import com.virnect.data.dto.response.*;
import com.virnect.data.dto.rpc.RpcParamsRequest;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.feign.service.MessageRestService;
import com.virnect.serviceserver.data.DataProcess;
import com.virnect.serviceserver.data.DataRepository;
import com.virnect.serviceserver.session.ServiceSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SessionRestController implements ISessionRestAPI {
    private static final String TAG = SessionRestController.class.getSimpleName();
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    private static final String REST_PATH = "/remote/room";

    private final DataRepository dataRepository;
    private final ServiceSessionManager serviceSessionManager;
    private final MessageRestService messageRestService;

    @Override
    public ResponseEntity<ApiResponse<PushResponse>> sendPushMessageHandler(@Valid PushSendRequest pushSendRequest, BindingResult result) {
        log.info("REST API: POST {}/message/push", REST_PATH);

        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<PushResponse> response = this.messageRestService.sendPush(pushSendRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponse<RoomResponse>> createRoomRequestHandler(
            @RequestBody @Valid RoomRequest roomRequest,
            BindingResult result) {
        log.info("REST API: POST {}/{}", REST_PATH, roomRequest != null ? roomRequest.toString() : "{}");

        // 1. check room request handler
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        DataProcess<LicenseItem> licenseItem = this.dataRepository.checkLicenseValidation(roomRequest.getWorkspaceId(), roomRequest.getLeaderId());

        if (licenseItem.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
            ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                    new RoomResponse(),
                    licenseItem.getCode(),
                    licenseItem.getMessage()
            );
            return ResponseEntity.ok(apiResponse);
        }

        // 2. check room request member count is over
        if (roomRequest.getParticipantIds().size() + 1 > licenseItem.getData().getUserCapacity()) {
            ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                    new RoomResponse(),
                    ErrorCode.ERR_ROOM_MEMBER_IS_OVER
            );
            return ResponseEntity.ok(apiResponse);
        }

        // 2. check room request member count is over
        /*if(roomRequest.getLeaderId() != null && roomRequest.getParticipantIds().size() > (ServiceConstants.PRODUCT_BUSINESS_MAX_USER - 1)) {
            throw new RestServiceException(ErrorCode.ERR_ROOM_MEMBER_IS_OVER);
        }*/

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
        JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

        // 5. create room
        return ResponseEntity.ok(
                this.dataRepository.generateRoom(roomRequest, licenseItem.getData(), sessionJson.toString(), tokenResult.toString())
        );
    }

    @Override
    public ResponseEntity<ApiResponse<RoomInfoListResponse>> getRoomList(
            @RequestParam(name = "workspaceId") String workspaceId,
            @RequestParam(name = "userId") String userId,
            @RequestParam("paging") boolean paging,
            @ApiIgnore PageRequest pageRequest
    ) {
        //@RequestParam(value = "webRtcStats", required = false, defaultValue = "false") boolean webRtcStats,
        log.info("REST API: GET {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", userId != null ? userId : "{}");
        return ResponseEntity.ok(
                this.dataRepository.loadRoomList(workspaceId, userId, paging, pageRequest.of())
        );
    }

    @Override
    public ResponseEntity<ApiResponse<RoomDetailInfoResponse>> getRoomById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId) {
        log.info("REST API: GET {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId.toString() : "{}", sessionId != null ? sessionId.toString() : "{}");
        if (workspaceId.isEmpty() || sessionId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        return ResponseEntity.ok(
                this.dataRepository.loadRoom(workspaceId, sessionId)
        );
    }

    @Override
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
        ApiResponse<ResultResponse> apiResponse = this.dataRepository.removeRoom(workspaceId, sessionId, userId);
        if(apiResponse.getData().getResult()) {
            //Session session = this.sessionManager.getSession(sessionId);
            if(this.serviceSessionManager.closeActiveSession(sessionId)) {
                resultResponse.setResult(true);
                return ResponseEntity.ok(apiResponse);
            }
            if(this.serviceSessionManager.closeNotActiveSession(sessionId)) {
                return ResponseEntity.ok(apiResponse);
            } else {
                return ResponseEntity.ok(apiResponse);
            }
        } else {
            return ResponseEntity.ok(apiResponse);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<RoomProfileUpdateResponse>> updateRoomProfile(
            @ModelAttribute @Valid RoomProfileUpdateRequest roomProfileUpdateRequest,
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            BindingResult result) {
        log.info("REST API: POST {}/{}/{}/profile",
                REST_PATH,
                workspaceId != null ? workspaceId : "{}",
                sessionId != null ? sessionId : "{}");
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        return ResponseEntity.ok(
                this.dataRepository.updateRoom(workspaceId, sessionId, roomProfileUpdateRequest)
        );
    }

    @Override
    public ResponseEntity<ApiResponse<RoomDetailInfoResponse>> updateRoomById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @RequestBody @Valid ModifyRoomInfoRequest modifyRoomInfoRequest,
            BindingResult result
    ) {
        log.info("REST API: POST {}/{}/{}/info", REST_PATH,
                workspaceId != null ? workspaceId : "{}",
                sessionId != null ? sessionId : "{}");
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        return ResponseEntity.ok(
                this.dataRepository.updateRoom(workspaceId, sessionId, modifyRoomInfoRequest)
        );
    }

    @Override
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

        DataProcess<Boolean> dataProcess = this.dataRepository.prepareJoinRoom(workspaceId, sessionId, joinRoomRequest.getUuid());
        if(dataProcess.getData()) {
            // 3. generate session id and token
            //String customSessionId = sessionData.getCustomSessionId();
            //String customSessionId = sessionId;
            JsonObject sessionJson = serviceSessionManager.generateSession(sessionId);
            JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);
            return ResponseEntity.ok(
                    this.dataRepository.joinRoom(workspaceId, sessionId, tokenResult.toString(), joinRoomRequest)
            );
        } else {
            return ResponseEntity.ok(
                    new ApiResponse<>(dataProcess.getCode(), dataProcess.getMessage())
            );
        }
    }

    @Override
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

        return ResponseEntity.ok(
                this.dataRepository.exitRoom(workspaceId, sessionId, userId)
        );
    }

    @Override
    public ResponseEntity<ApiResponse<ResultResponse>> inviteMember(
            String workspaceId,
            String sessionId,
            @Valid InviteRoomRequest inviteRoomRequest,
            BindingResult result) {
        log.info(TAG, "inviteMember");

        if (result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ResultResponse> response = this.dataRepository.inviteMember(workspaceId, sessionId, inviteRoomRequest);
        //send push message or not?
        //this send push message
        if(response.getData().getResult()) {
            PushSendRequest pushSendRequest = new PushSendRequest();
            pushSendRequest.setService(PushConstants.PUSH_EVENT_REMOTE);
            pushSendRequest.setEvent(PushConstants.SEND_PUSH_ROOM_INVITE);
            pushSendRequest.setWorkspaceId(workspaceId);
            pushSendRequest.setUserId(inviteRoomRequest.getLeaderId());
            pushSendRequest.setTargetUserIds(inviteRoomRequest.getParticipantIds());
            pushSendRequest.setContents(new HashMap<>());

            ApiResponse<PushResponse> pushResponse = this.messageRestService.sendPush(pushSendRequest);
            if(pushResponse.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
                log.info("push send message executed but not success");
                log.info("push response: [code] {}", pushResponse.getCode());
                log.info("push response: [message] {}", pushResponse.getMessage());
            }
        }
        return ResponseEntity.ok(response);
        /*else {
            ApiResponse<InviteRoomResponse> apiResponse = new ApiResponse<>();
            apiResponse.setCode(response.getCode());
            apiResponse.setMessage(response.getMessage());
            return ResponseEntity.ok(apiResponse);
        }*/
    }

    @Override
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

        DataProcess<String> dataProcess = this.dataRepository.evictParticipant(workspaceId, sessionId, kickRoomRequest.getParticipantId());
        if(dataProcess == null) {
            throw new RestServiceException(ErrorCode.ERR_SERVICE_PROCESS);
        } else {
            String connectionId = dataProcess.getData();
            if(connectionId.isEmpty()) {
                //if connection id cannot find, push message and just remove user
                PushSendRequest pushSendRequest = new PushSendRequest();
                pushSendRequest.setService(PushConstants.PUSH_EVENT_REMOTE);
                pushSendRequest.setEvent(PushConstants.SEND_PUSH_ROOM_EVICT);
                pushSendRequest.setWorkspaceId(workspaceId);
                pushSendRequest.setUserId(kickRoomRequest.getLeaderId());
                pushSendRequest.setTargetUserIds(Arrays.asList(kickRoomRequest.getParticipantId()));
                pushSendRequest.setContents(new HashMap<>());

                ApiResponse<PushResponse> pushResponse = this.messageRestService.sendPush(pushSendRequest);
                if(pushResponse.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
                    log.info("push send message executed but not success");
                    log.info("push response: [code] {}", pushResponse.getCode());
                    log.info("push response: [message] {}", pushResponse.getMessage());
                }
                return ResponseEntity.ok(
                        this.dataRepository.kickFromRoom(workspaceId, sessionId, kickRoomRequest)
                );
            } else {
                //send rpc message to connection id user of the session id
                ApiResponse<ResultResponse> apiResponse = new ApiResponse<>();
                ResultResponse response = new ResultResponse();
                response.setResult(false);

                connectionId = dataProcess.getData();

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
                    apiResponse.setCode(Integer.parseInt(jsonObject.get("status").getAsString()));
                    apiResponse.setMessage(jsonObject.get("message").getAsString());
                } else {
                    response.setResult(true);
                }
                apiResponse.setData(response);
                return ResponseEntity.ok(apiResponse);
            }
            //do not force disconnect.
            /*String connectionId = dataProcess.getData();
            if(serviceSessionManager.evictParticipant(sessionId, connectionId)) {
                return ResponseEntity.ok(
                        this.dataRepository.kickFromRoom(workspaceId, sessionId, kickRoomRequest)
                );
            } else {
                throw new RestServiceException(ErrorCode.ERR_SERVICE_PROCESS);
            }*/
        }
    }

    @Override
    public ResponseEntity<ApiResponse<ResultResponse>> sendSignal(
            String workspaceId,
            @Valid SendSignalRequest sendSignalRequest,
            BindingResult result) {

        log.info("REST API: POST {}/{}/{}/signal",
                REST_PATH,
                workspaceId != null ? workspaceId.toString() : "{}",
                sendSignalRequest != null ? sendSignalRequest.toString() : "{}"
                );

        if (result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        //Assert.assertTrue(jsonObject.get("name").getAsString().equals("Baeldung"));
        //Assert.assertTrue(jsonObject.get("java").getAsBoolean() == true);
        ApiResponse<ResultResponse> apiResponse = new ApiResponse<>();
        ResultResponse response = new ResultResponse();
        response.setResult(false);

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
