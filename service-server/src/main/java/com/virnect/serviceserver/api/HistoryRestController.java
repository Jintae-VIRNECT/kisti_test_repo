package com.virnect.serviceserver.api;

import com.google.gson.JsonObject;
import com.virnect.data.dao.SessionType;
import com.virnect.service.ApiResponse;
import com.virnect.service.api.IHistoryRestAPI;
import com.virnect.service.constraint.LicenseItem;
import com.virnect.service.dto.PageRequest;
import com.virnect.service.dto.ResultResponse;
import com.virnect.service.dto.service.request.RoomHistoryDeleteRequest;
import com.virnect.service.dto.service.request.RoomRequest;
import com.virnect.service.dto.service.response.RoomHistoryDetailInfoResponse;
import com.virnect.service.dto.service.response.RoomHistoryInfoListResponse;
import com.virnect.service.dto.service.response.RoomResponse;
import com.virnect.service.error.ErrorCode;
import com.virnect.service.error.exception.RestServiceException;
import com.virnect.serviceserver.data.HistoryDataRepository;
import com.virnect.serviceserver.data.SessionDataRepository;
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

@Slf4j
@RestController
@RequiredArgsConstructor
public class HistoryRestController implements IHistoryRestAPI {
    private static final String TAG = HistoryRestController.class.getSimpleName();
    private static final String REST_PATH = "/remote/history";
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";

    private final ServiceSessionManager serviceSessionManager;

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
                "REST API: GET "
                        + REST_PATH + "/"
                        + (workspaceId != null ? workspaceId : "{}") + "/"
                        + (userId != null ? userId : "{}"),
                "getHistoryList"
        );

        ApiResponse<RoomHistoryInfoListResponse> apiResponse;
        if(paging) {
            apiResponse = this.historyDataRepository.loadRoomHistoryPageList(workspaceId, userId, pageable.ofSortBy());
        } else {
            apiResponse =  this.historyDataRepository.loadRoomHistoryList(workspaceId, userId,  pageable.ofSortBy());
        }
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<RoomHistoryInfoListResponse>> getHistoryList(String workspaceId, String userId, String search, boolean paging, PageRequest pageable) {
        LogMessage.formedInfo(
                TAG,
                "REST API: GET "
                        + REST_PATH + "/"
                        + (workspaceId != null ? workspaceId : "{}")  + "/"
                        + (userId != null ? userId : "{}") + "/"
                        + (search != null ? search : "{}"),
                "getHistoryList"
        );

        ApiResponse<RoomHistoryInfoListResponse> apiResponse;
        apiResponse = this.historyDataRepository.searchRoomHistoryPageList(workspaceId, userId, search, pageable.ofSortBy());
        /*if(paging) {
            apiResponse = this.historyDataRepository.searchRoomHistoryPageList(workspaceId, userId, pageable.ofSortBy());
        } else {
            apiResponse =  this.historyDataRepository.loadRoomHistoryList(workspaceId, userId,  pageable.ofSortBy());
        }*/

        return ResponseEntity.ok(apiResponse);
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
                        (roomRequest != null ? roomRequest.toString() : "{}") + "\n"
                        + (sessionId != null ? sessionId : "{}") + "\n"
                        + "COMPANY CODE: " + companyCode,
                "redialRoomRequest"
        );

        // check room request handler
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message ->
                    LogMessage.formedError(
                            TAG,
                            "REST API: POST " + REST_PATH,
                            "redialRoomRequest",
                            LogMessage.PARAMETER_ERROR,
                            message.toString()
                    )
            );
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        // check user is valid
        //DataProcess<UserInfoResponse> userInfo = this.historyDataRepository.checkUserValidation(roomRequest.getLeaderId());

        // check user license type using user id
        /*DataProcess<LicenseItem> licenseItem = this.historyDataRepository.checkLicenseValidation(roomRequest.getWorkspaceId(), roomRequest.getLeaderId());
        if (licenseItem.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
            ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                    new RoomResponse(),
                    licenseItem.getCode(),
                    licenseItem.getMessage()
            );
            return ResponseEntity.ok(apiResponse);
        }*/

        // change license item using company code if not virnect
        LicenseItem licenseItem = LicenseItem.getLicenseItem(companyCode);
        if (licenseItem == null) {
            ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
                    new RoomResponse(),
                    ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE
            );
            return ResponseEntity.ok(apiResponse);
        }
        /*if (companyCode != CompanyConstants.COMPANY_VIRNECT) {
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
        }*/

        if(roomRequest.getSessionType().equals(SessionType.PRIVATE) || roomRequest.getSessionType().equals(SessionType.PUBLIC)) {
            // check room request member count is over
            if (roomRequest.getParticipantIds().size() + 1 > licenseItem.getUserCapacity()) {
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
            ApiResponse<RoomResponse> apiResponse = this.sessionDataRepository.generateRoom(
                    sessionId,
                    roomRequest,
                    licenseItem,
                    roomRequest.getLeaderId(),
                    sessionJson.toString(),
                    tokenResult.toString());

            return ResponseEntity.ok(apiResponse);
        } else if (roomRequest.getSessionType().equals(SessionType.OPEN)) {
            //open session is not need to check member count.
            // generate session id and token
            JsonObject sessionJson = serviceSessionManager.generateSession();
            JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

            // create room
            ApiResponse<RoomResponse> apiResponse = this.sessionDataRepository.generateRoom(sessionId, roomRequest, licenseItem, roomRequest.getLeaderId(), sessionJson.toString(), tokenResult.toString());
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
    public ResponseEntity<ApiResponse<RoomResponse>> redialRoomRequestHandler(String client,
                                                                              String userId,
                                                                              @Valid RoomRequest roomRequest,
                                                                              String sessionId,
                                                                              int companyCode,
                                                                              BindingResult result) {
        LogMessage.formedInfo(
                TAG,
                "REST API: POST " + REST_PATH +
                        (roomRequest != null ? roomRequest.toString() : "{}") + "\n"
                        + (sessionId != null ? sessionId : "{}") + "\n"
                        + ("COMPANY CODE: " + companyCode) + "\n"
                        + ("REQ USERID: " + userId) + "\n"
                        + ("REQ HEADER: " + client),
                "redialRoomRequestHandler"
        );

        // check room request handler
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message ->
                    LogMessage.formedError(
                            TAG,
                            "REST API: POST " + REST_PATH,
                            "redialRoomRequestHandler",
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
            if (roomRequest.getParticipantIds().size() + 1 > licenseItem.getUserCapacity()) {
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
            ApiResponse<RoomResponse> apiResponse = this.sessionDataRepository.generateRoom(
                    sessionId,
                    roomRequest,
                    licenseItem,
                    userId,
                    sessionJson.toString(),
                    tokenResult.toString());

            return ResponseEntity.ok(apiResponse);
        } else if (roomRequest.getSessionType().equals(SessionType.OPEN)) {
            //open session is not need to check member count.
            // generate session id and token
            JsonObject sessionJson = serviceSessionManager.generateSession();
            JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

            // create room
            ApiResponse<RoomResponse> apiResponse = this.sessionDataRepository.generateRoom(
                    sessionId,
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
