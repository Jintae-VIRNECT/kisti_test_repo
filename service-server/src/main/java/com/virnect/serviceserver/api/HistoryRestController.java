package com.virnect.serviceserver.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.virnect.data.dao.SessionType;
import com.virnect.data.service.HistoryService;
import com.virnect.service.ApiResponse;
import com.virnect.service.api.IHistoryRestAPI;
import com.virnect.service.constraint.CompanyConstants;
import com.virnect.service.constraint.LicenseItem;
import com.virnect.service.constraint.PushConstants;
import com.virnect.service.dto.PageRequest;
import com.virnect.service.dto.ResultResponse;
import com.virnect.service.dto.feign.PushResponse;
import com.virnect.service.dto.feign.UserInfoResponse;
import com.virnect.service.dto.push.InviteRoomContents;
import com.virnect.service.dto.service.request.PushSendRequest;
import com.virnect.service.dto.service.request.RoomHistoryDeleteRequest;
import com.virnect.service.dto.service.request.RoomRequest;
import com.virnect.service.dto.service.response.RoomHistoryDetailInfoResponse;
import com.virnect.service.dto.service.response.RoomHistoryInfoListResponse;
import com.virnect.service.dto.service.response.RoomResponse;
import com.virnect.service.error.ErrorCode;
import com.virnect.service.error.exception.RestServiceException;
import com.virnect.serviceserver.data.DataProcess;
import com.virnect.serviceserver.data.DataRepository;
import com.virnect.serviceserver.data.HistoryDataRepository;
import com.virnect.serviceserver.data.SessionDataRepository;
import com.virnect.serviceserver.feign.service.MessageRestService;
import com.virnect.serviceserver.session.ServiceSessionManager;
import com.virnect.serviceserver.utils.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HistoryRestController implements IHistoryRestAPI {
    private static final String TAG = HistoryRestController.class.getSimpleName();
    private static final String REST_PATH = "/remote/history";
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";

    //private final DataRepository dataRepository;
    private final ServiceSessionManager serviceSessionManager;
    private final MessageRestService messageRestService;

    private SessionDataRepository sessionDataRepository;
    private HistoryDataRepository historyDataRepository;

    @Qualifier(value = "sessionDataRepository")
    @Autowired
    public void setSessionDataRepository(SessionDataRepository sessionDataRepository) {
        this.sessionDataRepository = sessionDataRepository;
    }

    @Qualifier(value = "historyDataRepository")
    @Autowired
    public void setHistoryDataRepository(HistoryDataRepository historyDataRepository) {
        this.historyDataRepository = historyDataRepository;
    }


    @Override
    public ResponseEntity<ApiResponse<RoomHistoryInfoListResponse>> getHistoryList(String workspaceId, String userId, boolean paging, PageRequest pageable) {
        LogMessage.formedInfo(
                TAG,
                "REST API: GET " + REST_PATH +
                        (workspaceId != null ? workspaceId : "{}") +
                        (userId != null ? userId : "{}"),
                "getHistoryList"
        );
        return ResponseEntity.ok(
                this.historyDataRepository.loadRoomHistoryInfoList(workspaceId, userId, paging, pageable.of())
        );
    }

    @Override
    public ResponseEntity<ApiResponse<RoomResponse>> redialRoomRequest(
            @Valid RoomRequest roomRequest,
            String sessionId,
            int companyCode,
            BindingResult result) {
        LogMessage.formedInfo(
                TAG,
                "REST API: POST " + REST_PATH +
                        (roomRequest != null ? roomRequest.toString() : "{}") +
                        (sessionId != null ? sessionId : "{}") +
                        "company code: " + companyCode,
                "redialRoomRequest"
        );

        // check room request handler
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        // check user is valid
        DataProcess<UserInfoResponse> userInfo = this.historyDataRepository.checkUserValidation(roomRequest.getLeaderId());

        // check user license type using user id
        DataProcess<LicenseItem> licenseItem = this.historyDataRepository.checkLicenseValidation(roomRequest.getWorkspaceId(), roomRequest.getLeaderId());
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
            ApiResponse<RoomResponse> apiResponse = this.sessionDataRepository.generateRoom(sessionId, roomRequest, licenseItem.getData(), sessionJson.toString(), tokenResult.toString());
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
            ApiResponse<RoomResponse> apiResponse = this.sessionDataRepository.generateRoom(sessionId, roomRequest, licenseItem.getData(), sessionJson.toString(), tokenResult.toString());
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
    public ResponseEntity<ApiResponse<RoomHistoryDetailInfoResponse>> getHistoryById(String workspaceId, String sessionId) {
        LogMessage.formedInfo(
                TAG,
                "REST API: DELETE " + REST_PATH +
                        (workspaceId != null ? workspaceId : "{}") +
                        (sessionId != null ? sessionId : "{}"),
                "getHistoryById"
        );

        if ((workspaceId == null || workspaceId.isEmpty()) ||
                (sessionId == null || sessionId.isEmpty())) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        return ResponseEntity.ok(
                this.historyDataRepository.loadRoomHistoryDetail(workspaceId, sessionId)
        );
    }

    @Override
    public ResponseEntity<ApiResponse<ResultResponse>> deleteHistory(String workspaceId, String userId) {
        LogMessage.formedInfo(
                TAG,
                "REST API: DELETE " + REST_PATH +
                        (workspaceId != null ? workspaceId : "{}") +
                        (userId != null ? userId : "{}"),
                "deleteHistory"
        );
        if((workspaceId == null || workspaceId.isEmpty()) ||
                (userId == null || userId.isEmpty())) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        return ResponseEntity.ok(
                this.historyDataRepository.removeRoomHistory(workspaceId, userId)
        );
    }

    @Override
    public ResponseEntity<ApiResponse<ResultResponse>> deleteHistoryById(String workspaceId, @Valid RoomHistoryDeleteRequest roomHistoryDeleteRequest, BindingResult result) {
        LogMessage.formedInfo(
                TAG,
                "REST API: DELETE " + REST_PATH +
                        (workspaceId != null ? workspaceId : "{}") +
                        roomHistoryDeleteRequest.toString(),
                "deleteHistoryById"
        );
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        return ResponseEntity.ok(
                this.historyDataRepository.removeRoomHistory(workspaceId, roomHistoryDeleteRequest)
        );
    }
}
