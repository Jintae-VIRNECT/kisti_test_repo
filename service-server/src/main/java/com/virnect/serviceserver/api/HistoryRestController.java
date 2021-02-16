package com.virnect.serviceserver.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.data.dto.request.room.RoomHistoryDeleteRequest;
import com.virnect.data.dto.request.room.RoomRequest;
import com.virnect.remote.application.HistoryService;
import com.virnect.serviceserver.application.RoomService;
import com.virnect.data.dto.response.PageRequest;
import com.virnect.data.dto.response.ResultResponse;
import com.virnect.data.dto.response.room.RoomHistoryDetailInfoResponse;
import com.virnect.data.dto.response.room.RoomHistoryInfoListResponse;
import com.virnect.data.dto.response.room.RoomResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class HistoryRestController {
    private static final String TAG = HistoryRestController.class.getSimpleName();
    private static final String REST_PATH = "/remote/history";
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";

    //private final ServiceSessionManager serviceSessionManager;

    //private final SessionDataRepository sessionDataRepository;
    //private final HistoryDataRepository historyDataRepository;

    private final RoomService roomService;
    //private final HistoryServiceTemp historyService;
    private final HistoryService historyService;

	/*@Qualifier(value = "sessionDataRepository")
	@Autowired
	public void setSessionDataRepository(SessionDataRepository sessionDataRepository) {
		this.sessionDataRepository = sessionDataRepository;
	}

	@Qualifier(value = "historyDataRepository")
	@Autowired
	public void setHistoryDataRepository(HistoryDataRepository historyDataRepository) {
		this.historyDataRepository = historyDataRepository;
	}*/

    @ApiOperation(value = "Load Room History Information List", notes = "최근 기록 리스트를 조회하는 API 입니다.")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 ID", defaultValue = "40f9bbee9d85dca7a34a0dd205aae718", required = true),
        @ApiImplicitParam(name = "userId", value = "유저 uuid", defaultValue = "410df50ca6e32db0b6acba09bcb457ff", required = true),
        @ApiImplicitParam(name = "paging", value = "검색 결과 페이지네이션 여부", dataType = "boolean", allowEmptyValue = true, defaultValue = "false"),
        @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
        @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", paramType = "query", defaultValue = "0"),
        @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "endDate, desc"),
    })
    @GetMapping(value = "history")
    public ResponseEntity<ApiResponse<RoomHistoryInfoListResponse>> getHistoryListCurrent(
        @RequestParam(name = "workspaceId") String workspaceId,
        @RequestParam(name = "userId") String userId,
        @RequestParam(name = "paging") boolean paging,
        @ApiIgnore PageRequest pageable
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "/"
                + (workspaceId != null ? workspaceId : "{}") + "/"
                + (userId != null ? userId : "{}"),
            "getHistoryList"
        );

		/*ApiResponse<RoomHistoryInfoListResponse> apiResponse;
		if (paging) {
			apiResponse = this.historyDataRepository.loadRoomHistoryPageList(workspaceId, userId, pageable.ofSortBy());
		} else {
			apiResponse = this.historyDataRepository.loadRoomHistoryList(workspaceId, userId, pageable.ofSortBy());
		}
		return ResponseEntity.ok(apiResponse);*/

        RoomHistoryInfoListResponse responseData = historyService.getRoomHistoryCurrent(
            workspaceId, userId, paging, pageable.ofSortBy());

        return ResponseEntity.ok(new ApiResponse<>(responseData));
    }

    @ApiOperation(value = "Search Room History Information List", notes = "검색 기준으로 최근 기록 리스트를 조회하는 API 입니다.")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 ID", defaultValue = "40f9bbee9d85dca7a34a0dd205aae718", required = true),
        @ApiImplicitParam(name = "userId", value = "유저 uuid", defaultValue = "410df50ca6e32db0b6acba09bcb457ff", required = true),
        @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
        @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", paramType = "query", defaultValue = "0"),
        @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate, desc"),
    })
    @GetMapping(value = "history/search")
    public ResponseEntity<ApiResponse<RoomHistoryInfoListResponse>> getHistoryListStandardSearch(
        @RequestParam(name = "workspaceId") String workspaceId,
        @RequestParam(name = "userId") String userId,
        @RequestParam(name = "search", required = false) String search,
        @ApiIgnore PageRequest pageable
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "/"
                + (workspaceId != null ? workspaceId : "{}") + "/"
                + (userId != null ? userId : "{}") + "/"
                + (search != null ? search : "{}"),
            "getHistoryList"
        );

		/*ApiResponse<RoomHistoryInfoListResponse> apiResponse = this.historyDataRepository.searchRoomHistoryPageList(
			workspaceId,
			userId,
			search,
			pageable.ofSortBy()
		);*/

        //return ResponseEntity.ok(apiResponse);

        RoomHistoryInfoListResponse responseData = historyService.getHistoryListStandardSearch(
            workspaceId, userId, search, pageable.ofSortBy());

        return ResponseEntity.ok(new ApiResponse<>(responseData));

    }

    @ApiOperation(value = "Redial a History Remote Room with Company Code", notes = "This api will be deprecated")
    @PostMapping(value = "history")
    public ResponseEntity<ApiResponse<RoomResponse>> redialRoomRequest(
        @RequestBody @Valid RoomRequest roomRequest,
        @RequestParam(name = "sessionId") String sessionId,
        @RequestParam(name = "companyCode") int companyCode,
        BindingResult result
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: POST " + REST_PATH +
                (roomRequest != null ? roomRequest.toString() : "{}") + "\n"
                + (sessionId != null ? sessionId : "{}") + "\n"
                + "COMPANY CODE: " + companyCode,
            "redialRoomRequest"
        );

        // check room request handler
        if (result.hasErrors()) {
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

        ApiResponse<RoomResponse> responseData = roomService.redialRoomRequest(
            roomRequest,
            sessionId,
            companyCode
        );

        return ResponseEntity.ok(responseData);

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

		/*// change license item using company code if not virnect
		LicenseItem licenseItem = LicenseItem.getLicenseItem(companyCode);
		if (licenseItem == null) {
			ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
				new RoomResponse(),
				ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE
			);
			return ResponseEntity.ok(apiResponse);
		}
        *//*if (companyCode != CompanyConstants.COMPANY_VIRNECT) {
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
        }*//*

		if (roomRequest.getSessionType().equals(SessionType.PRIVATE) || roomRequest.getSessionType()
			.equals(SessionType.PUBLIC)) {
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
				tokenResult.toString()
			);

			return ResponseEntity.ok(apiResponse);
		} else if (roomRequest.getSessionType().equals(SessionType.OPEN)) {
			//open session is not need to check member count.
			// generate session id and token
			JsonObject sessionJson = serviceSessionManager.generateSession();
			JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

			// create room
			ApiResponse<RoomResponse> apiResponse = this.sessionDataRepository.generateRoom(
				sessionId, roomRequest, licenseItem, roomRequest.getLeaderId(), sessionJson.toString(),
				tokenResult.toString()
			);
			return ResponseEntity.ok(apiResponse);
		} else {
			ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
				new RoomResponse(),
				ErrorCode.ERR_ROOM_CREATE_FAIL
			);
			return ResponseEntity.ok(apiResponse);
		}*/
    }

    @ApiOperation(value = "Redial a History Remote Room with Company Code", notes = "Redial Remote Session")
    @PostMapping(value = "history/{userId}")
    public ResponseEntity<ApiResponse<RoomResponse>> redialRoomRequestByUserId(
        @RequestHeader(name = "client", required = false) String client,
        @PathVariable(name = "userId") String userId,
        @RequestBody @Valid RoomRequest roomRequest,
        @RequestParam(name = "sessionId") String sessionId,
        @RequestParam(name = "companyCode") int companyCode,
        BindingResult result
    ) {
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
        if (result.hasErrors()) {
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

        ApiResponse<RoomResponse> responseData = roomService.redialRoomRequestByUserId(
            client,
            userId,
            roomRequest,
            sessionId,
            companyCode
        );

        return ResponseEntity.ok(responseData);

		/*LicenseItem licenseItem = LicenseItem.getLicenseItem(companyCode);
		if (licenseItem == null) {
			ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
				new RoomResponse(),
				ErrorCode.ERR_ROOM_LICENSE_COMPANY_CODE
			);
			return ResponseEntity.ok(apiResponse);
		}

		if (roomRequest.getSessionType().equals(SessionType.PRIVATE) || roomRequest.getSessionType()
			.equals(SessionType.PUBLIC)) {
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
				tokenResult.toString()
			);

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
				tokenResult.toString()
			);

			return ResponseEntity.ok(apiResponse);
		} else {
			ApiResponse<RoomResponse> apiResponse = new ApiResponse<>(
				new RoomResponse(),
				ErrorCode.ERR_ROOM_CREATE_FAIL
			);
			return ResponseEntity.ok(apiResponse);
		}*/
    }

    @ApiOperation(value = "Load Room History Detail Information", notes = "특정 원격협업 방 최근 기록 상세 정보를 조회하는 API 입니다.")
    @GetMapping(value = "history/{workspaceId}/{sessionId}")
    public ResponseEntity<ApiResponse<RoomHistoryDetailInfoResponse>> getHistoryById(
        @PathVariable("workspaceId") String workspaceId,
        @PathVariable("sessionId") String sessionId
    ) {
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

        ApiResponse<RoomHistoryDetailInfoResponse> responseData = historyService.getHistoryBySessionId(workspaceId, sessionId);

        return ResponseEntity.ok(responseData);

		/*return ResponseEntity.ok(
			this.historyDataRepository.loadRoomHistoryDetail(workspaceId, sessionId)
		);*/
    }

    @ApiOperation(value = "Delete all Room Histories", notes = "모든 최근 기록 리스트를 삭제하는 API 입니다.")
    @DeleteMapping(value = "history/{workspaceId}/{userId}")
    public ResponseEntity<ApiResponse<ResultResponse>> deleteHistory(
        @PathVariable("workspaceId") String workspaceId,
        @PathVariable("userId") String userId
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: DELETE " + REST_PATH +
                (workspaceId != null ? workspaceId : "{}") +
                (userId != null ? userId : "{}"),
            "deleteHistory"
        );
        if ((workspaceId == null || workspaceId.isEmpty()) ||
            (userId == null || userId.isEmpty())) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<ResultResponse> responseData = historyService.deleteHistory(workspaceId, userId);

        return ResponseEntity.ok(responseData);

		/*return ResponseEntity.ok(
			this.historyDataRepository.removeRoomHistory(workspaceId, userId)
		);*/
    }

    @ApiOperation(value = "Delete a specific room", notes = "특정 최근기록을 삭제하는 API 입니다.")
    @DeleteMapping(value = "history/{workspaceId}")
    public ResponseEntity<ApiResponse<ResultResponse>> deleteHistoryById(
        @PathVariable("workspaceId") String workspaceId,
        @RequestBody @Valid RoomHistoryDeleteRequest roomHistoryDeleteRequest,
        BindingResult result
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: DELETE " + REST_PATH +
                (workspaceId != null ? workspaceId : "{}") +
                roomHistoryDeleteRequest.toString(),
            "deleteHistoryById"
        );
        if (result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<ResultResponse> responseData = historyService.deleteHistoryById(
            workspaceId,
            roomHistoryDeleteRequest
        );

        return ResponseEntity.ok(responseData);

		/*return ResponseEntity.ok(
			this.historyDataRepository.removeRoomHistory(workspaceId, roomHistoryDeleteRequest)
		);*/
    }
}