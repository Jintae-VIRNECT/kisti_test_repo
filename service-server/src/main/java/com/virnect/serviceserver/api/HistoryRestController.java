package com.virnect.serviceserver.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.virnect.data.ApiResponse;
import com.virnect.data.api.IHistoryRestAPI;
import com.virnect.data.constraint.CompanyConstants;
import com.virnect.data.constraint.LicenseItem;
import com.virnect.data.constraint.PushConstants;
import com.virnect.data.dao.SessionType;
import com.virnect.data.dto.feign.PushResponse;
import com.virnect.data.dto.feign.UserInfoResponse;
import com.virnect.data.dto.push.InviteRoomContents;
import com.virnect.data.dto.request.PageRequest;
import com.virnect.data.dto.request.PushSendRequest;
import com.virnect.data.dto.request.RoomHistoryDeleteRequest;
import com.virnect.data.dto.request.RoomRequest;
import com.virnect.data.dto.response.ResultResponse;
import com.virnect.data.dto.response.RoomHistoryDetailInfoResponse;
import com.virnect.data.dto.response.RoomHistoryInfoListResponse;
import com.virnect.data.dto.response.RoomResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.service.HistoryService;
import com.virnect.serviceserver.data.DataProcess;
import com.virnect.serviceserver.data.DataRepository;
import com.virnect.serviceserver.feign.service.MessageRestService;
import com.virnect.serviceserver.session.ServiceSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HistoryRestController implements IHistoryRestAPI {
    private static final String TAG = HistoryRestController.class.getSimpleName();
    private static final String REST_PATH = "/remote/history";
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";

    private final DataRepository dataRepository;
    private final ServiceSessionManager serviceSessionManager;
    private final MessageRestService messageRestService;

    @Override
    public ResponseEntity<ApiResponse<RoomHistoryInfoListResponse>> getHistoryList(String workspaceId, String userId, boolean paging, PageRequest pageable) {
        log.info("REST API: GET {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", userId != null ? userId : "{}");
        return ResponseEntity.ok(
                this.dataRepository.loadRoomHistoryInfoList(workspaceId, userId, paging, pageable.of())
        );
        /*ApiResponse<RoomHistoryInfoListResponse> apiResponse = this.historyService.getRoomHistoryInfoList(workspaceId, userId, paging, pageable);
        return ResponseEntity.ok(apiResponse);*/
    }

    @Override
    public ResponseEntity<ApiResponse<RoomResponse>> redialRoomRequest(@Valid RoomRequest roomRequest, String sessionId, int companyCode, BindingResult result) {
        log.info("REST API: POST {}/{}/{} company code : {}",
                REST_PATH,
                roomRequest != null ? roomRequest.toString() : "{}",
                sessionId != null ? sessionId.toString() : "{}",
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
            JsonObject sessionJson = serviceSessionManager.generateSession(sessionId);
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
    public ResponseEntity<ApiResponse<RoomHistoryDetailInfoResponse>> getHistoryById(String workspaceId, String sessionId) {
        log.info("REST API: DELETE {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", sessionId != null ? sessionId : "{}");
        if (sessionId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        return ResponseEntity.ok(
                this.dataRepository.loadRoomHistoryDetail(workspaceId, sessionId)
        );
        /*ApiResponse<RoomHistoryDetailInfoResponse> apiResponse = this.historyService.getRoomHistoryDetailInfo(workspaceId, sessionId);
        return ResponseEntity.ok(apiResponse);*/
    }

    @Override
    public ResponseEntity<ApiResponse<ResultResponse>> deleteHistory(String workspaceId, String userId) {
        log.info("REST API: DELETE {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", userId != null ? userId : "{}");
        if(userId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        return ResponseEntity.ok(
                this.dataRepository.removeRoomHistory(workspaceId, userId)
        );
        /*ApiResponse<ResultResponse> apiResponse = this.historyService.removeAllRoomHistory(workspaceId, userId);
        return ResponseEntity.ok(apiResponse);*/
    }

    @Override
    public ResponseEntity<ApiResponse<ResultResponse>> deleteHistoryById(String workspaceId, @Valid RoomHistoryDeleteRequest roomHistoryDeleteRequest, BindingResult result) {
        log.info("REST API: DELETE {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", roomHistoryDeleteRequest.toString());

        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        return ResponseEntity.ok(
                this.dataRepository.removeRoomHistory(workspaceId, roomHistoryDeleteRequest)
        );

        /*ApiResponse<ResultResponse> apiResponse = this.historyService.removeRoomHistory(workspaceId, roomHistoryDeleteRequest);
        return ResponseEntity.ok(apiResponse);*/
    }

    /*@ApiOperation(value = "Load Room History Information List", notes = "최근 기록 리스트를 조회하는 API 입니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 ID", defaultValue = "40f9bbee9d85dca7a34a0dd205aae718", required = true),
            @ApiImplicitParam(name = "userId", value = "유저 uuid", defaultValue = "410df50ca6e32db0b6acba09bcb457ff", required = true),
            @ApiImplicitParam(name = "paging", value = "검색 결과 페이지네이션 여부", dataType = "boolean", allowEmptyValue = true, defaultValue = "false"),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(1부터 시작)", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
    })
    @GetMapping(value = "history")
    public ResponseEntity<ApiResponse<RoomHistoryInfoListResponse>> getHistoryList(
            @RequestParam(name = "workspaceId") String workspaceId,
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "paging") boolean paging,
            @ApiIgnore PageRequest pageable
    ) {
        log.info("REST API: GET {}/{}{}", REST_PATH, workspaceId != null ? workspaceId : "{}", userId != null ? userId : "{}");
        //ApiResponse<RoomHistoryInfoListResponse> apiResponse = this.historyService.getRoomHistoryInfoList(workspaceId, userId, paging, pageable.of());
        ApiResponse<RoomHistoryInfoListResponse> apiResponse = this.historyService.getRoomHistoryInfoList(workspaceId, userId, paging, pageable);
        return ResponseEntity.ok(apiResponse);
    }*/

    /*@ApiOperation(value = "Load Room History Detail Information", notes = "특정 원격협업 방 최근 기록 상세 정보를 조회하는 API 입니다.")
    @GetMapping(value = "history/{workspaceId}/{sessionId}")
    public ResponseEntity<ApiResponse<RoomHistoryDetailInfoResponse>> getHistoryById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId) {
        log.info("REST API: DELETE {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", sessionId != null ? sessionId : "{}");
        if (sessionId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<RoomHistoryDetailInfoResponse> apiResponse = this.historyService.getRoomHistoryDetailInfo(workspaceId, sessionId);
        return ResponseEntity.ok(apiResponse);
    }*/

    /*@ApiOperation(value = "Delete all Room Histories", notes = "모든 최근 기록 리스트를 삭제하는 API 입니다.")
    @DeleteMapping(value = "history/{workspaceId}/{userId}")
    public ResponseEntity<ApiResponse<ResultResponse>> deleteHistory(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("userId") String userId) {
        log.info("REST API: DELETE {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", userId != null ? userId : "{}");
        if(userId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<ResultResponse> apiResponse = this.historyService.removeAllRoomHistory(workspaceId, userId);
        return ResponseEntity.ok(apiResponse);
    }*/

    /*@ApiOperation(value = "Delete a specific room", notes = "특정 최근기록을 삭제하는 API 입니다.")
    @DeleteMapping(value = "history/{workspaceId}")
    public ResponseEntity<ApiResponse<ResultResponse>> deleteHistoryById(
            @PathVariable("workspaceId") String workspaceId,
            @RequestBody @Valid RoomHistoryDeleteRequest roomHistoryDeleteRequest,
            BindingResult result) {
        log.info("REST API: DELETE {}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", roomHistoryDeleteRequest.toString());

        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<ResultResponse> apiResponse = this.historyService.removeRoomHistory(workspaceId, roomHistoryDeleteRequest);
        return ResponseEntity.ok(apiResponse);
    }*/

    //=====
    /*@ApiOperation(value = "Delete a specific room", notes = "특정 최근기록을 삭제하는 API 입니다.")
    @DeleteMapping(value = "history/{workspaceId}/{sessionId}/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteHistoryById(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @PathVariable("userId") String userId) {
        log.info("REST API: DELETE {}/{}/{}/{}", REST_PATH, workspaceId != null ? workspaceId : "{}", sessionId != null ? sessionId : "{}", userId != null ? userId : "{}");

        if(sessionId.isEmpty() || userId.isEmpty()) {
            throw new RemoteServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<Boolean> apiResponse = this.remoteGatewayService.removeRoomHistory(workspaceId, sessionId, userId);
        return ResponseEntity.ok(apiResponse);
    }*/

}
