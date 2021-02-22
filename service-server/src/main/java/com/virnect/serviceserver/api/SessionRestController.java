package com.virnect.serviceserver.api;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import org.springframework.web.client.RestTemplate;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.remote.dto.constraint.LicenseItem;
import com.virnect.serviceserver.application.RoomService;
import com.virnect.serviceserver.application.SessionService;
import com.virnect.data.dto.PushSendRequest;
import com.virnect.remote.dto.push.SendSignalRequest;
import com.virnect.remote.dto.request.room.InviteRoomRequest;
import com.virnect.remote.dto.request.room.JoinRoomRequest;
import com.virnect.remote.dto.request.room.KickRoomRequest;
import com.virnect.remote.dto.request.room.ModifyRoomInfoRequest;
import com.virnect.remote.dto.request.room.RoomRequest;
import com.virnect.remote.dto.response.PageRequest;
import com.virnect.remote.dto.response.ResultResponse;
import com.virnect.remote.dto.response.room.RoomDeleteResponse;
import com.virnect.remote.dto.response.room.RoomDetailInfoResponse;
import com.virnect.remote.dto.response.room.RoomInfoListResponse;
import com.virnect.remote.dto.response.room.RoomResponse;
import com.virnect.data.dto.rest.PushResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.infra.utils.PushMessageClient;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class SessionRestController {
    private static final String TAG = SessionRestController.class.getSimpleName();
    private static final String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    private static final String REST_PATH = "/remote/room";

    //private SessionDataRepository sessionDataRepository;
    //private FileDataRepository fileDataRepository;
    //private final PushMessageClient pushMessageClient;

    //private final ServiceSessionManager serviceSessionManager;

    private RestTemplate restTemplate;

    private final PushMessageClient pushMessageClient;

    private final SessionService sessionService;
    private final RoomService roomService;

    @Autowired(required = false)
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /*@Qualifier(value = "pushMessageClient")
    @Autowired
    public void setPushMessageClient(PushMessageClient pushMessageClient) {
        this.pushMessageClient = pushMessageClient;
    }*/

	/*@Qualifier(value = "pushMessageClient")
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
	}*/

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

    @ApiOperation(value = "Service Push Message ", notes = "푸시 메시지를 발행 하는 API 입니다.")
    @PostMapping(value = "message/push")
    public ResponseEntity<ApiResponse<PushResponse>> sendPushMessageHandler(
        @RequestBody @Valid PushSendRequest pushSendRequest,
        BindingResult result
    ) {
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

        if (result.hasErrors()) {
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

        /*PushResponse responseData = sessionService.sendPush(pushSendRequest);
        return ResponseEntity.ok(new ApiResponse(responseData));*/
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
    @ApiOperation(value = "Initialize a Remote Room with Company Code", notes = "Generate Remote Session")
    @PostMapping(value = "room/{userId}")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoomRequestHandler(
        @RequestHeader(name = "client", required = false) String client,
        @PathVariable(name = "userId") String userId,
        @RequestBody @Valid RoomRequest roomRequest,
        @RequestParam(name = "companyCode") int companyCode,
        BindingResult result
    ) {
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
        if (result.hasErrors()) {
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

        ApiResponse<RoomResponse> responseData = roomService.initRoomByClient(
            client,
            userId,
            roomRequest,
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
			if (IsValidUserCapacity(roomRequest, licenseItem)) {
				// generate session id and token
				JsonObject sessionJson = serviceSessionManager.generateSession();
				JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

				// create room
				ApiResponse<RoomResponse> apiResponse = this.sessionDataRepository.generateRoom(
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

    /**
     * 1. check room request handler
     * 2. check user license type using user uuid
     * 3. generate session id and token
     * 4. create room
     * 5. register user as a leader who creates the room
     * 6. register other users as a worker(participant), if the request contains other user information.
     * 7. return session id and token
     */
    @ApiOperation(value = "Initialize a Remote Room with Company Code", notes = "This api will be deprecated")
    @PostMapping(value = "room")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoomRequestHandler(
        @RequestBody @Valid RoomRequest roomRequest,
        @RequestParam(name = "companyCode") int companyCode,
        BindingResult result
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: POST " + REST_PATH +
                (roomRequest != null ? roomRequest.toString() : "{}") +
                "company code: " + companyCode,
            "createRoomRequestHandler"
        );
        // check room request handler
        if (result.hasErrors()) {
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

        ApiResponse<RoomResponse> responseData = roomService.initRoom(
            roomRequest,
            companyCode
        );
        return ResponseEntity.ok(responseData);

        // check license item using company code if not virnect
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
			if (IsValidUserCapacity(roomRequest, licenseItem)) {
				// generate session id and token
				JsonObject sessionJson = serviceSessionManager.generateSession();
				JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

				ApiResponse<RoomResponse> apiResponse = this.sessionDataRepository.generateRoom(
					roomRequest,
					licenseItem,
					roomRequest.getLeaderId(),
					sessionJson.toString(),
					tokenResult.toString()
				);

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

    @ApiOperation(value = "Load Room Information List", notes = "원격협헙 방 리스트 조회하는 API 입니다.")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "paging", value = "검색 결과 페이지네이션 여부", dataType = "boolean", allowEmptyValue = true, defaultValue = "false"),
        @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
        @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", paramType = "query", defaultValue = "0"),
        @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
    })
    @GetMapping(value = "room")
    public ResponseEntity<ApiResponse<RoomInfoListResponse>> getRoomList(
        @RequestParam(name = "workspaceId") String workspaceId,
        @RequestParam(name = "userId") String userId,
        @RequestParam(name = "paging") boolean paging,
        @ApiIgnore PageRequest pageRequest
    ) {
        //@RequestParam(value = "webRtcStats", required = false, defaultValue = "false") boolean webRtcStats,
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "/"
                + (workspaceId != null ? workspaceId : "{}") + "/"
                + (userId != null ? userId : "{}"),
            "getRoomList"
        );

		/*ApiResponse<RoomInfoListResponse> apiResponse;
		if (paging) {
			apiResponse = this.sessionDataRepository.loadRoomPageList(workspaceId, userId, pageRequest.ofSortBy());
		} else {
			apiResponse = this.sessionDataRepository.loadRoomList(workspaceId, userId, pageRequest.ofSortBy());
		}*/

        RoomInfoListResponse responseData = sessionService.getRoomList(
            workspaceId, userId, paging, pageRequest.ofSortBy());

        return ResponseEntity.ok(new ApiResponse(responseData));
    }

    @ApiOperation(value = "Search Room Information List", notes = "검색 기준으로 원격협헙 방 리스트 조회하는 API 입니다.")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
        @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", paramType = "query", defaultValue = "0"),
        @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
    })
    @GetMapping(value = "room/search")
    public ResponseEntity<ApiResponse<RoomInfoListResponse>> getRoomList(
        @RequestParam(name = "workspaceId") String workspaceId,
        @RequestParam(name = "userId") String userId,
        @RequestParam(name = "search", required = false) String search,
        @ApiIgnore PageRequest pageRequest
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "/"
                + (workspaceId != null ? workspaceId : "{}") + "/"
                + (userId != null ? userId : "{}") + "/"
                + (search != null ? search : "{}"),
            "getRoomList"
        );
		/*ApiResponse<RoomInfoListResponse> apiResponse = this.sessionDataRepository.searchRoomPageList(
			workspaceId, userId, search, pageRequest.ofSortBy());
		return ResponseEntity.ok(apiResponse);*/

        RoomInfoListResponse responseData = sessionService.getRoomListStandardSearch(
            workspaceId,
            userId,
            search,
            pageRequest.ofSortBy()
        );

        return ResponseEntity.ok(new ApiResponse(responseData));
    }

    @ApiOperation(value = "Load Room Detail Information", notes = "특정 원격협업 방 상세 정보를 조회하는 API 입니다.")
    @GetMapping(value = "room/{workspaceId}/{sessionId}")
    public ResponseEntity<ApiResponse<RoomDetailInfoResponse>> getRoomById(
        @PathVariable("workspaceId") String workspaceId,
        @PathVariable("sessionId") String sessionId
    ) {
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

        ApiResponse<RoomDetailInfoResponse> responseData = sessionService.getRoomDetailBySessionId(
            workspaceId, sessionId);

        return ResponseEntity.ok(responseData);

		/*return ResponseEntity.ok(
			this.sessionDataRepository.loadRoom(workspaceId, sessionId)
		);*/
    }

    @ApiOperation(value = "Delete Specific Room", notes = "특정 원격협업 방을 삭제하는 API 입니다.")
    @DeleteMapping(value = "room/{workspaceId}/{sessionId}/{userId}")
    public ResponseEntity<ApiResponse<RoomDeleteResponse>> deleteRoomById(
        @PathVariable("workspaceId") String workspaceId,
        @PathVariable("sessionId") String sessionId,
        @PathVariable("userId") String userId
    ) {
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
        if (sessionId == null || sessionId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<RoomDeleteResponse> responseData = sessionService.deleteRoomById(
            workspaceId, sessionId, userId);

        return ResponseEntity.ok(responseData);

		/*ApiResponse<RoomDeleteResponse> apiResponse = this.sessionDataRepository.removeRoom(
			workspaceId, sessionId, userId);*/

		/*if (apiResponse.getData().result) {
			//send rpc message to connection id user of the session id
            *//*DataProcess<List<String>> dataProcess = this.sessionDataRepository.getConnectionIds(workspaceId, sessionId);
            JsonObject jsonObject = serviceSessionManager.generateMessage(
                    sessionId,
                    dataProcess.getData(),
                    PushConstants.PUSH_SIGNAL_SYSTEM,
                    PushConstants.SEND_PUSH_ROOM_CLOSED
            );*//*

			if (this.serviceSessionManager.closeActiveSession(sessionId)) {
				LogMessage.formedInfo(
					TAG,
					"serviceSessionManager",
					"closeActiveSession"
				);
				this.fileDataRepository.removeFiles(workspaceId, sessionId);
				return ResponseEntity.ok(apiResponse);
			}

			if (this.serviceSessionManager.closeNotActiveSession(sessionId)) {
				LogMessage.formedInfo(
					TAG,
					"serviceSessionManager",
					"closeNotActiveSession"
				);
				this.fileDataRepository.removeFiles(workspaceId, sessionId);
				return ResponseEntity.ok(apiResponse);
			}
		}
		return ResponseEntity.ok(apiResponse);*/
    }

    @ApiOperation(value = "Update Room Information", notes = "특정 원격협업 방 상세 정보를 수정하는 API 입니다.")
    @PostMapping(value = "room/{workspaceId}/{sessionId}/info")
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

        if (result.hasErrors()) {
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

        ApiResponse<RoomDetailInfoResponse> responseData = sessionService.updateRoom(
            workspaceId, sessionId, modifyRoomInfoRequest);
        return ResponseEntity.ok(responseData);

		/*return ResponseEntity.ok(
			this.sessionDataRepository.updateRoom(workspaceId, sessionId, modifyRoomInfoRequest)
		);*/
    }

    @ApiOperation(value = "Join a Specific Room", notes = "특정 원격협업 방에 접속하는 API 입니다.")
    @PostMapping(value = "room/{workspaceId}/{sessionId}/join")
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

        if (result.hasErrors()) {
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

        ApiResponse<RoomResponse> responseData = roomService.joinRoomById(
            workspaceId, sessionId, joinRoomRequest);
        return ResponseEntity.ok(responseData);

		/*DataProcess<Boolean> dataProcess = this.sessionDataRepository.prepareJoinRoom(
			workspaceId, sessionId, joinRoomRequest.getUuid());
		ApiResponse<RoomResponse> apiResponse;
		if (dataProcess.getData()) {
			// generate session id and token
			JsonObject sessionJson = serviceSessionManager.generateSession(sessionId);
			JsonObject tokenResult = serviceSessionManager.generateSessionToken(sessionJson);

			apiResponse = this.sessionDataRepository.joinRoom(
				workspaceId, sessionId, tokenResult.toString(), joinRoomRequest);
		} else {
			LogMessage.formedInfo(
				TAG,
				"REST API: POST " + REST_PATH + "/",
				"joinRoomById",
				"process data get false",
				dataProcess.getMessage()
			);

			apiResponse = new ApiResponse<>(new RoomResponse());
			apiResponse.setCode(dataProcess.getCode());
			apiResponse.setMessage(dataProcess.getMessage());
		}
		return ResponseEntity.ok(apiResponse);*/
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
        if (sessionId.isEmpty() || userId.isEmpty()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<ResultResponse> responseData = roomService.exitRoomBySessionIdAndUserId(
            workspaceId, sessionId, userId);
        return ResponseEntity.ok(responseData);

		/*return ResponseEntity.ok(
			this.sessionDataRepository.exitRoom(workspaceId, sessionId, userId)
		);*/
    }

    @ApiOperation(value = "Invite a Member to Specific Room", notes = "특정 멤버를 원격협업 방에 초대하는 API 입니다.")
    @PostMapping(value = "room/{workspaceId}/{sessionId}/member")
    public ResponseEntity<ApiResponse<ResultResponse>> inviteMember(
        @PathVariable("workspaceId") String workspaceId,
        @PathVariable("sessionId") String sessionId,
        @RequestBody @Valid InviteRoomRequest inviteRoomRequest,
        BindingResult result
    ) {

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

        if (result.hasErrors()) {
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

        ApiResponse<ResultResponse> responseData = roomService.inviteMember(
            workspaceId, sessionId, inviteRoomRequest);
        return ResponseEntity.ok(responseData);

		/*ApiResponse<InviteRoomResponse> response = this.sessionDataRepository.inviteMember(
			workspaceId, sessionId, inviteRoomRequest);
		ApiResponse<ResultResponse> resultResponse;
		if (response.getCode() == ErrorCode.ERR_SUCCESS.getCode()) {
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
		return ResponseEntity.ok(resultResponse);*/
    }

    @ApiOperation(value = "Kick out a specific member from a specific room", notes = "특정 멤버를 원격협업 방에서 내보내는 API 입니다.")
    @DeleteMapping(value = "room/{workspaceId}/{sessionId}/member")
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

        if (result.hasErrors()) {
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

        ApiResponse<ResultResponse> responseData = roomService.kickOutMember(
            workspaceId, sessionId, kickRoomRequest);
        return ResponseEntity.ok(responseData);

		/*ApiResponse<KickRoomResponse> apiResponse = this.sessionDataRepository.kickFromRoom(
			workspaceId, sessionId, kickRoomRequest);
		ApiResponse<ResultResponse> resultResponse = null;
		if (apiResponse.getCode() == ErrorCode.ERR_SUCCESS.getCode()) {
			String connectionId = apiResponse.getData().getConnectionId();
			if (connectionId == null || connectionId.isEmpty()) {
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

				if (jsonObject.has("error")) {
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
		return ResponseEntity.ok(resultResponse);*/
    }

    @ApiOperation(value = "send signal to the specific room session", notes = "특정 원격협업 방에 신호를 보내는 API 입니다.")
    @PostMapping(value = "room/{workspaceId}/signal")
    public ResponseEntity<ApiResponse<ResultResponse>> sendSignal(
        @PathVariable("workspaceId") String workspaceId,
        @RequestBody @Valid SendSignalRequest sendSignalRequest,
        BindingResult result
    ) {

        LogMessage.formedInfo(
            TAG,
            "REST API: POST "
                + REST_PATH
                + (workspaceId != null ? workspaceId : "{}")
                + (sendSignalRequest != null ? sendSignalRequest.toString() : "{}")
                + "/signal",
            "inviteMember"
        );

        if (result.hasErrors()) {
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

        ApiResponse<ResultResponse> responseData = roomService.sendSignal(
            workspaceId, sendSignalRequest);
        return ResponseEntity.ok(responseData);

		/*ApiResponse<ResultResponse> apiResponse = new ApiResponse<>();
		ResultResponse response = new ResultResponse();
		JsonObject jsonObject = serviceSessionManager.generateMessage(
			sendSignalRequest.getSessionId(),
			sendSignalRequest.getTo(),
			sendSignalRequest.getType(),
			sendSignalRequest.getData()
		);
		if (jsonObject.has("error")) {
			log.info("sendSignal :{}", jsonObject.get("error").getAsString());
			log.info("sendSignal :{}", jsonObject.get("status").getAsString());
			log.info("sendSignal :{}", jsonObject.get("message").getAsString());
			apiResponse.setCode(Integer.parseInt(jsonObject.get("status").getAsString()));
			apiResponse.setMessage(jsonObject.get("message").getAsString());
		} else {
			response.setResult(true);
		}
		apiResponse.setData(response);
		return ResponseEntity.ok(apiResponse);*/
    }

    @Deprecated
    @GetMapping("")
    public void shortMessageSend() {
        String obj = restTemplate.getForObject("", String.class);
    }

}
