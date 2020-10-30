package com.virnect.serviceserver.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.virnect.data.ApiResponse;
import com.virnect.data.api.ISessionRestAPI;
import com.virnect.data.constraint.CompanyConstants;
import com.virnect.data.constraint.LicenseItem;
import com.virnect.data.constraint.PushConstants;
import com.virnect.data.dao.SessionType;
import com.virnect.data.dto.feign.PushResponse;
import com.virnect.data.dto.feign.UserInfoResponse;
import com.virnect.data.dto.push.InviteRoomContents;
import com.virnect.data.dto.request.*;
import com.virnect.data.dto.response.*;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.serviceserver.data.DataProcess;
import com.virnect.serviceserver.data.DataRepository;
import com.virnect.serviceserver.feign.service.MessageRestService;
import com.virnect.serviceserver.session.ServiceSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            String client,
            String userId,
            @Valid RoomRequest roomRequest,
            int companyCode,
            BindingResult result) {
        log.info("REST API: POST {}/{}", REST_PATH, roomRequest != null ? roomRequest.toString() : "{}");
        log.info("REST API: POST {}, Request UserId::{}", REST_PATH, userId != null ? userId : "null userId");
        log.info("REST API: POST Header {}, Request Header::{}", REST_PATH, client != null ? client : "null client header");

        // check room request handler
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        // check user is valid
        DataProcess<UserInfoResponse> userInfo = this.dataRepository.checkUserValidation(userId);

        // check user license type using user id
        DataProcess<LicenseItem> licenseItem = this.dataRepository.checkLicenseValidation(roomRequest.getWorkspaceId(), roomRequest.getLeaderId());
        if (licenseItem.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
            ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                    new RoomResponse(),
                    licenseItem.getCode(),
                    licenseItem.getMessage()
            );
            return ResponseEntity.ok(apiResponse);
        }

        // check room request member count is over
        /*if (roomRequest.getParticipantIds().size() + 1 > licenseItem.getData().getUserCapacity()) {
            ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                    new RoomResponse(),
                    ErrorCode.ERR_ROOM_MEMBER_IS_OVER
            );
            return ResponseEntity.ok(apiResponse);
        }*/

        // change license item using company code if not virnect
        if (companyCode != CompanyConstants.COMPANY_VIRNECT) {
            LicenseItem companyLicenseItem = LicenseItem.getLicenseItem(companyCode);
            if (companyLicenseItem == null) {
                ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                        new RoomResponse(),
                        ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE
                );
                return ResponseEntity.ok(apiResponse);
            } else {
                licenseItem.setData(companyLicenseItem);
            }
        }

        //ApiResponse<RoomResponse> apiResponse;
        if(roomRequest.getSessionType().equals(SessionType.PRIVATE) || roomRequest.getSessionType().equals(SessionType.PUBLIC)) {
            // check room request member count is over
            if (roomRequest.getParticipantIds().size() + 1 > licenseItem.getData().getUserCapacity()) {
                ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                        new RoomResponse(),
                        ErrorCode.ERR_ROOM_MEMBER_IS_OVER
                );
                return ResponseEntity.ok(apiResponse);
            }

            // generate session id and token
            JsonObject sessionJson = serviceSessionManager.generateSession();
            JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

            // create room
            ApiResponse<RoomResponse> apiResponse = this.dataRepository.generateRoom(roomRequest, licenseItem.getData(), sessionJson.toString(), tokenResult.toString());
            if(apiResponse.getCode() == ErrorCode.ERR_SUCCESS.getCode()) {
                //send push message invite
                PushSendRequest pushSendRequest = new PushSendRequest();
                pushSendRequest.setService(PushConstants.PUSH_EVENT_REMOTE);
                pushSendRequest.setEvent(PushConstants.SEND_PUSH_ROOM_INVITE);
                pushSendRequest.setWorkspaceId(roomRequest.getWorkspaceId());
                pushSendRequest.setUserId(userId);
                pushSendRequest.setTargetUserIds(Arrays.asList(roomRequest.getLeaderId()));
                //set push message invite room contents
                InviteRoomContents inviteRoomContents = new InviteRoomContents();
                inviteRoomContents.setSessionId(apiResponse.getData().getSessionId());
                inviteRoomContents.setTitle(roomRequest.getTitle());
                inviteRoomContents.setNickName(userInfo.getData().getNickname());
                inviteRoomContents.setProfile(userInfo.getData().getProfile());
                ObjectMapper mapper = new ObjectMapper();
                try {
                    String jsonString = mapper.writeValueAsString(inviteRoomContents);
                    pushSendRequest.setContents(mapper.readValue(jsonString, new TypeReference<Map<Object, Object>>() {}));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                ApiResponse<PushResponse> pushResponse = this.messageRestService.sendPush(pushSendRequest);
                if(pushResponse.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
                    log.info("push send message executed but not success");
                    log.info("push response: [code] {}", pushResponse.getCode());
                    log.info("push response: [message] {}", pushResponse.getMessage());
                }
            }
            return ResponseEntity.ok(apiResponse);
        } else if (roomRequest.getSessionType().equals(SessionType.OPEN)) {
            //open session is not need to check member count.
            // generate session id and token
            JsonObject sessionJson = serviceSessionManager.generateSession();
            JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

            // create room
            ApiResponse<RoomResponse> apiResponse = this.dataRepository.generateRoom(roomRequest, licenseItem.getData(), sessionJson.toString(), tokenResult.toString());
            return ResponseEntity.ok(apiResponse);
        } else {
            ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                    new RoomResponse(),
                    ErrorCode.ERR_ROOM_CREATE_FAIL
            );
            return ResponseEntity.ok(apiResponse);
        }
    }

    /*@Override
    public ResponseEntity<ApiResponse<RoomResponse>> createRoomRequestHandler(
            @Valid RoomRequest roomRequest,
            BindingResult result) {
        log.info("REST API: POST {}/{}", REST_PATH, roomRequest != null ? roomRequest.toString() : "{}");

        // check room request handler
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        // check user is valid
        DataProcess<UserInfoResponse> userInfo = this.dataRepository.checkUserValidation(roomRequest.getLeaderId());

        // check user license type using user id
        DataProcess<LicenseItem> licenseItem = this.dataRepository.checkLicenseValidation(roomRequest.getWorkspaceId(), roomRequest.getLeaderId());
        if (licenseItem.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
            ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                    new RoomResponse(),
                    licenseItem.getCode(),
                    licenseItem.getMessage()
            );
            return ResponseEntity.ok(apiResponse);
        }

        // check room request member count is over
        if (roomRequest.getParticipantIds().size() + 1 > licenseItem.getData().getUserCapacity()) {
            ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                    new RoomResponse(),
                    ErrorCode.ERR_ROOM_MEMBER_IS_OVER
            );
            return ResponseEntity.ok(apiResponse);
        }

        // generate session id and token
        JsonObject sessionJson = serviceSessionManager.generateSession();
        JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

        // create room
        ApiResponse<RoomResponse> apiResponse = this.dataRepository.generateRoom(roomRequest, licenseItem.getData(), sessionJson.toString(), tokenResult.toString());
        if(apiResponse.getCode() == ErrorCode.ERR_SUCCESS.getCode()) {
            //send push message invite
            PushSendRequest pushSendRequest = new PushSendRequest();
            pushSendRequest.setService(PushConstants.PUSH_EVENT_REMOTE);
            pushSendRequest.setEvent(PushConstants.SEND_PUSH_ROOM_INVITE);
            pushSendRequest.setWorkspaceId(roomRequest.getWorkspaceId());
            pushSendRequest.setUserId(roomRequest.getLeaderId());
            pushSendRequest.setTargetUserIds(roomRequest.getParticipantIds());
            //set push message invite room contents
            InviteRoomContents inviteRoomContents = new InviteRoomContents();
            inviteRoomContents.setSessionId(apiResponse.getData().getSessionId());
            inviteRoomContents.setTitle(roomRequest.getTitle());
            inviteRoomContents.setNickName(userInfo.getData().getNickname());
            inviteRoomContents.setProfile(userInfo.getData().getProfile());
            ObjectMapper mapper = new ObjectMapper();
            try {
                String jsonString = mapper.writeValueAsString(inviteRoomContents);
                pushSendRequest.setContents(mapper.readValue(jsonString, new TypeReference<Map<Object, Object>>() {}));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            ApiResponse<PushResponse> pushResponse = this.messageRestService.sendPush(pushSendRequest);
            if(pushResponse.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
                log.info("push send message executed but not success");
                log.info("push response: [code] {}", pushResponse.getCode());
                log.info("push response: [message] {}", pushResponse.getMessage());
            }
        }
        return ResponseEntity.ok(apiResponse);
    }*/

    @Override
    public ResponseEntity<ApiResponse<RoomResponse>> createRoomRequestHandler(@Valid RoomRequest roomRequest, int companyCode, BindingResult result) {
        log.info("REST API: POST {}/{} company code : {}",
                REST_PATH,
                roomRequest != null ? roomRequest.toString() : "{}",
                companyCode
                );
        // check room request handler
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        // check user is valid
        DataProcess<UserInfoResponse> userInfo = this.dataRepository.checkUserValidation(roomRequest.getLeaderId());

        // check user license type using user id
        DataProcess<LicenseItem> licenseItem = this.dataRepository.checkLicenseValidation(roomRequest.getWorkspaceId(), roomRequest.getLeaderId());
        if (licenseItem.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
            ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                    new RoomResponse(),
                    licenseItem.getCode(),
                    licenseItem.getMessage()
            );
            return ResponseEntity.ok(apiResponse);
        }

        // change license item using company code if not virnect
        if (companyCode != CompanyConstants.COMPANY_VIRNECT) {
            LicenseItem companyLicenseItem = LicenseItem.getLicenseItem(companyCode);
            if (companyLicenseItem == null) {
                ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                        new RoomResponse(),
                        ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE
                );
                return ResponseEntity.ok(apiResponse);
            } else {
                licenseItem.setData(companyLicenseItem);
            }
        }

        if(roomRequest.getSessionType().equals(SessionType.PRIVATE) || roomRequest.getSessionType().equals(SessionType.PUBLIC)) {
            // check room request member count is over
            if (roomRequest.getParticipantIds().size() + 1 > licenseItem.getData().getUserCapacity()) {
                ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                        new RoomResponse(),
                        ErrorCode.ERR_ROOM_MEMBER_IS_OVER
                );
                return ResponseEntity.ok(apiResponse);
            }
            // generate session id and token
            JsonObject sessionJson = serviceSessionManager.generateSession();
            JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

            // create room
            ApiResponse<RoomResponse> apiResponse = this.dataRepository.generateRoom(roomRequest, licenseItem.getData(), sessionJson.toString(), tokenResult.toString());
            if(apiResponse.getCode() == ErrorCode.ERR_SUCCESS.getCode()) {
                //send push message invite
                PushSendRequest pushSendRequest = new PushSendRequest();
                pushSendRequest.setService(PushConstants.PUSH_EVENT_REMOTE);
                pushSendRequest.setEvent(PushConstants.SEND_PUSH_ROOM_INVITE);
                pushSendRequest.setWorkspaceId(roomRequest.getWorkspaceId());
                pushSendRequest.setUserId(roomRequest.getLeaderId());
                pushSendRequest.setTargetUserIds(roomRequest.getParticipantIds());
                //set push message invite room contents
                InviteRoomContents inviteRoomContents = new InviteRoomContents();
                inviteRoomContents.setSessionId(apiResponse.getData().getSessionId());
                inviteRoomContents.setTitle(roomRequest.getTitle());
                inviteRoomContents.setNickName(userInfo.getData().getNickname());
                inviteRoomContents.setProfile(userInfo.getData().getProfile());
                ObjectMapper mapper = new ObjectMapper();
                try {
                    String jsonString = mapper.writeValueAsString(inviteRoomContents);
                    pushSendRequest.setContents(mapper.readValue(jsonString, new TypeReference<Map<Object, Object>>() {}));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                ApiResponse<PushResponse> pushResponse = this.messageRestService.sendPush(pushSendRequest);
                if(pushResponse.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
                    log.info("push send message executed but not success");
                    log.info("push response: [code] {}", pushResponse.getCode());
                    log.info("push response: [message] {}", pushResponse.getMessage());
                }
            }
            return ResponseEntity.ok(apiResponse);
        } else if (roomRequest.getSessionType().equals(SessionType.OPEN)) {
            //open session is not need to check member count.
            // generate session id and token
            JsonObject sessionJson = serviceSessionManager.generateSession();
            JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

            // create room
            ApiResponse<RoomResponse> apiResponse = this.dataRepository.generateRoom(roomRequest, licenseItem.getData(), sessionJson.toString(), tokenResult.toString());
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
    public ResponseEntity<ApiResponse<RoomDeleteResponse>> deleteRoomById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @PathVariable("userId") String userId) {
        log.info("REST API: DELETE {}/{}/{}/{}", REST_PATH,
                workspaceId != null ? workspaceId.toString() : "{}",
                sessionId != null ? sessionId : "{}",
                userId != null ? userId : "{}");

        //check null or empty
        if(sessionId == null || sessionId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        /*if(!remoteGatewayService.getUserGrantValidity(sessionId, userId).equals(MemberType.LEADER)) {
            throw new RemoteServiceException(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
        }*/

        //ResultResponse resultResponse = new ResultResponse();
        //resultResponse.setResult(false);
        DataProcess<List<String>> dataProcess = this.dataRepository.getConnectionIds(workspaceId, sessionId);
        ApiResponse<RoomDeleteResponse> apiResponse = this.dataRepository.removeRoom(workspaceId, sessionId, userId);

        //if(apiResponse.getData() != null) {
        if(apiResponse.getData().result) {
            //send rpc message to connection id user of the session id
            JsonObject jsonObject = serviceSessionManager.generateMessage(
                    sessionId,
                    dataProcess.getData(),
                    PushConstants.PUSH_SIGNAL_SYSTEM,
                    PushConstants.SEND_PUSH_ROOM_CLOSED
            );

            //
            if(this.serviceSessionManager.closeActiveSession(sessionId)) {
                //todo: to do sth, when close active session, if you need sth
                return ResponseEntity.ok(apiResponse);
            }

            if(this.serviceSessionManager.closeNotActiveSession(sessionId)) {
                //todo: do sth close not active session, if you need sth
                return ResponseEntity.ok(apiResponse);
            }

            /*if(this.serviceSessionManager.closeActiveSession(sessionId)) {
                //resultResponse.setResult(true);
                return ResponseEntity.ok(apiResponse);
            }

            if(this.serviceSessionManager.closeNotActiveSession(sessionId)) {
                return ResponseEntity.ok(apiResponse);
            } else {
                return ResponseEntity.ok(apiResponse);
            }*/
        }
        return ResponseEntity.ok(apiResponse);
        /*if(apiResponse.getData().getResult()) {
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
        }*/
    }

    /*@Override
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
    }*/

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
                workspaceId != null ? workspaceId : "{}",
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
        ApiResponse<InviteRoomResponse> response = this.dataRepository.inviteMember(workspaceId, sessionId, inviteRoomRequest);

        //send push message or not?
        //this send push message
        if(response.getData() != null) {
            // check user is valid
            DataProcess<UserInfoResponse> userInfo = this.dataRepository.checkUserValidation(response.getData().getLeaderId());

            PushSendRequest pushSendRequest = new PushSendRequest();
            pushSendRequest.setService(PushConstants.PUSH_EVENT_REMOTE);
            pushSendRequest.setEvent(PushConstants.SEND_PUSH_ROOM_INVITE);
            pushSendRequest.setWorkspaceId(response.getData().getWorkspaceId());
            pushSendRequest.setUserId(response.getData().getLeaderId());
            pushSendRequest.setTargetUserIds(response.getData().getParticipantIds());

            //set push message invite room contents
            InviteRoomContents inviteRoomContents = new InviteRoomContents();
            inviteRoomContents.setSessionId(response.getData().getSessionId());
            inviteRoomContents.setTitle(response.getData().getTitle());

            inviteRoomContents.setNickName(userInfo.getData().getNickname());
            inviteRoomContents.setProfile(userInfo.getData().getProfile());
            ObjectMapper mapper = new ObjectMapper();
            try {
                String jsonString = mapper.writeValueAsString(inviteRoomContents);
                pushSendRequest.setContents(mapper.readValue(jsonString, new TypeReference<Map<Object, Object>>() {}));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            ApiResponse<PushResponse> pushResponse = this.messageRestService.sendPush(pushSendRequest);
            if(pushResponse.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
                log.info("push send message executed but not success");
                log.info("push response: [code] {}", pushResponse.getCode());
                log.info("push response: [message] {}", pushResponse.getMessage());
            }
            ApiResponse<ResultResponse> resultResponse = new ApiResponse<>(new ResultResponse());
            resultResponse.getData().setResult(true);
            return ResponseEntity.ok(resultResponse);
        } else {
            ApiResponse<ResultResponse> resultResponse = new ApiResponse<>();
            resultResponse.setCode(response.getCode());
            resultResponse.setMessage(response.getMessage());
            return ResponseEntity.ok(resultResponse);
        }
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
                    //send force disconnected
                    //todo:forceResult when get false do process something.
                    boolean forceResult = serviceSessionManager.evictParticipant(sessionId, connectionId);
                    log.info("evictParticipant :{}", forceResult);
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
