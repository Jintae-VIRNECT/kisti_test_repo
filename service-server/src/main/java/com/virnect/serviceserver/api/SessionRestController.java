package com.virnect.serviceserver.api;

import com.google.gson.JsonObject;
import com.virnect.data.ApiResponse;
import com.virnect.data.api.ISessionRestAPI;
import com.virnect.data.constraint.ServiceConstants;
import com.virnect.data.dto.request.*;
import com.virnect.data.dto.response.*;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
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

@Slf4j
@RestController
@RequiredArgsConstructor
public class SessionRestController implements ISessionRestAPI {
    private static final String TAG = SessionRestController.class.getSimpleName();
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    private static final String REST_PATH = "/remote/room";

    private final DataRepository dataRepository;
    private final ServiceSessionManager serviceSessionManager;


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
        JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

        // 5. create room
        return ResponseEntity.ok(
                this.dataRepository.generateRoom(roomRequest, sessionJson.toString(), tokenResult.toString())
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
            String customSessionId = sessionId;
            JsonObject sessionJson = serviceSessionManager.generateSession();
            JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);
            return ResponseEntity.ok(
                    this.dataRepository.joinRoom(workspaceId, customSessionId, tokenResult.toString(), joinRoomRequest)
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
    public ResponseEntity<ApiResponse<InviteRoomResponse>> inviteMember(
            String workspaceId,
            String sessionId,
            @Valid InviteRoomRequest inviteRoomRequest,
            BindingResult result) {
        return null;
    }

    /*@ApiOperation(value = "Invite a Member to Specific Room", notes = "특정 멤버를 원격협업 방에 초대하는 API 입니다.")
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
    }*/

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
            if(serviceSessionManager.evictParticipant(sessionId, connectionId)) {
                return ResponseEntity.ok(
                        this.dataRepository.kickFromRoom(workspaceId, sessionId, kickRoomRequest)
                );
            } else {
                throw new RestServiceException(ErrorCode.ERR_SERVICE_PROCESS);
                /*apiResponse.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_KICK_FAIL);
                return ResponseEntity.ok(new ApiResponse<>(resultResponse));*/
            }
        }
        //MemberInfoResponse memberInfo = this.dataRepository.loadMember(workspaceId, sessionId, kickRoomRequest.getParticipantId()).getData();

        /*ResultResponse resultResponse = new ResultResponse();
        resultResponse.setResult(false);
        ApiResponse<ResultResponse> apiResponse = new ApiResponse<>(resultResponse);
        if (memberInfo != null) {
            String connectionId = memberInfo.getConnectionId();
            if(serviceSessionManager.evictParticipant(sessionId, connectionId)) {
                return ResponseEntity.ok(
                        this.dataRepository.kickFromRoom(workspaceId, sessionId, kickRoomRequest)
                );
            } else {
                apiResponse.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_KICK_FAIL);
                return ResponseEntity.ok(new ApiResponse<>(resultResponse));
            }
        } else {
            apiResponse.setErrorResponseData(ErrorCode.ERR_ROOM_MEMBER_NOT_FOUND);
            return ResponseEntity.ok(apiResponse);
        }*/
    }

    @Override
    public ResponseEntity<ApiResponse<ResultResponse>> sendSignal(
            String workspaceId,
            String sessionId,
            BindingResult result) {
        return null;
    }
}
