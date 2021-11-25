package com.virnect.serviceserver.serviceremote.api;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
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

import com.virnect.data.dto.PushSendRequest;
import com.virnect.data.dto.push.SendSignalRequest;
import com.virnect.data.dto.request.PageRequest;
import com.virnect.data.dto.request.room.InviteRoomRequest;
import com.virnect.data.dto.request.room.JoinRoomRequest;
import com.virnect.data.dto.request.room.KickRoomRequest;
import com.virnect.data.dto.request.room.ModifyRoomInfoRequest;
import com.virnect.data.dto.request.room.RoomRequest;
import com.virnect.data.dto.request.session.ForceLogoutRequest;
import com.virnect.data.dto.response.ResultResponse;
import com.virnect.data.dto.response.member.MemberInfoListResponse;
import com.virnect.data.dto.response.room.RoomDeleteResponse;
import com.virnect.data.dto.response.room.RoomDetailInfoResponse;
import com.virnect.data.dto.response.room.RoomInfoListResponse;
import com.virnect.data.dto.response.room.RoomResponse;
import com.virnect.data.dto.rest.PushResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.serviceremote.application.PushMessageClient;
import com.virnect.serviceserver.serviceremote.application.RoomService;
import com.virnect.serviceserver.serviceremote.application.ServiceSessionManager;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class SessionRestController {

	private static final String TAG = SessionRestController.class.getSimpleName();
	private static final String REST_PATH = "/remote/room";

	private final ServiceSessionManager serviceSessionManager;
	private final PushMessageClient pushMessageClient;
	private final RoomService roomService;

	@ApiOperation(value = "Service Push Message ", notes = "푸시 메시지를 발행 하는 API 입니다.")
	@PostMapping(value = "message/push")
	public ResponseEntity<ApiResponse<PushResponse>> sendPushMessageHandler(
		@RequestBody @Valid PushSendRequest pushSendRequest,
		BindingResult result
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: POST "
				+ REST_PATH + "::"
				+ "PushSendRequest:" + pushSendRequest.toString(),
			"sendPushMessageHandler"
		);
		if (result.hasErrors()) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<PushResponse> response = this.pushMessageClient.sendPush(pushSendRequest);
		return ResponseEntity.ok(response);
	}

	@ApiOperation(value = "Initialize a Remote Room with Company Code", notes = "Generate Remote Session")
	@PostMapping(value = "room/{userId}")
	public ResponseEntity<ApiResponse<RoomResponse>> createRoomRequestHandlerByUserId(
		@RequestHeader(name = "client", required = false) String client,
		@PathVariable(name = "userId") String userId,
		@RequestBody @Valid RoomRequest roomRequest,
		@RequestParam(name = "companyCode") int companyCode,
		BindingResult result
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: POST "
				+ REST_PATH + "/"
				+ userId + "::"
				+ "client:" + client + "/"
				+ "companyCode:" + companyCode + "/"
				+ "RoomRequest:" + roomRequest.toString(),
			"createRoomRequestHandlerByUserId"
		);
		if (result.hasErrors()) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<RoomResponse> responseData = roomService.initRoomByClient(
			client,
			userId,
			roomRequest,
			companyCode
		);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "Initialize a Remote Room with Company Code", notes = "This api will be deprecated")
	@PostMapping(value = "room")
	public ResponseEntity<ApiResponse<RoomResponse>> createRoomRequestHandler(
		@RequestBody @Valid RoomRequest roomRequest,
		@RequestParam(name = "companyCode") int companyCode,
		BindingResult result
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: POST "
				+ REST_PATH + "::"
				+ "CompanyCode:" + companyCode + "/"
				+ "RoomRequest:" + roomRequest.toString(),
			"createRoomRequestHandler"
		);
		if (result.hasErrors()) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<RoomResponse> responseData = roomService.initRoom(
			roomRequest,
			companyCode
		);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "Load Room Information List", notes = "원격협헙 방 리스트 조회하는 API 입니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "paging", value = "검색 결과 페이지네이션 여부", dataType = "boolean", allowEmptyValue = true, defaultValue = "false"),
		@ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
		@ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", paramType = "query", defaultValue = "0"),
		@ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc")
	})
	@GetMapping(value = "room")
	public ResponseEntity<ApiResponse<RoomInfoListResponse>> getRoomList(
		@RequestParam(name = "workspaceId") String workspaceId,
		@RequestParam(name = "userId") String userId,
		@RequestParam(name = "paging") boolean paging,
		@ApiIgnore PageRequest pageRequest
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: GET "
				+ REST_PATH + "::"
				+ "workspaceId:" + workspaceId + "/"
				+ "userId:" + userId,
			"getRoomList"
		);
		if (Strings.isBlank(workspaceId) || Strings.isBlank(userId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		RoomInfoListResponse responseData = roomService.getRoomList(
			workspaceId,
			userId,
			paging,
			pageRequest.ofSortBy()
		);
		return ResponseEntity.ok(new ApiResponse<>(responseData));
	}

	@ApiOperation(value = "Search Room Information List", notes = "검색 기준으로 원격협헙 방 리스트 조회하는 API 입니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "2"),
		@ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", paramType = "query", defaultValue = "0"),
		@ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc")
	})
	@GetMapping(value = "room/search")
	public ResponseEntity<ApiResponse<RoomInfoListResponse>> getRoomListBySearch(
		@RequestParam(name = "workspaceId") String workspaceId,
		@RequestParam(name = "userId") String userId,
		@RequestParam(name = "search", required = false) String search,
		@ApiIgnore PageRequest pageRequest
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: GET "
				+ REST_PATH + "::"
				+ "workspaceId:" + workspaceId + "/"
				+ "userId:" + userId + "/"
				+ "search:" + search,
			"getRoomListBySearch"
		);
		if (Strings.isBlank(workspaceId) || Strings.isBlank(userId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}

		RoomInfoListResponse responseData;
		if (StringUtils.isBlank(search)) {
			responseData = roomService.getRoomList(
				workspaceId,
				userId,
				true,
				pageRequest.ofSortBy()
			);
		} else {
			responseData = roomService.getRoomListStandardSearch(
				workspaceId,
				userId,
				search,
				pageRequest.ofSortBy()
			);
		}

		return ResponseEntity.ok(new ApiResponse<>(responseData));
	}

	@ApiOperation(value = "Load Room Detail Information", notes = "특정 원격협업 방 상세 정보를 조회하는 API 입니다.")
	@GetMapping(value = "room/{workspaceId}/{sessionId}")
	public ResponseEntity<ApiResponse<RoomDetailInfoResponse>> getRoomByWorkspaceIdAndSessionId(
		@PathVariable("workspaceId") String workspaceId,
		@PathVariable("sessionId") String sessionId
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: GET "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ sessionId,
			"getRoomByWorkspaceIdAndSessionId"
		);
		if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<RoomDetailInfoResponse> responseData = roomService.getRoomDetailBySessionId(
			workspaceId,
			sessionId
		);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "Delete Specific Room", notes = "특정 원격협업 방을 삭제하는 API 입니다.")
	@DeleteMapping(value = "room/{workspaceId}/{sessionId}/{userId}")
	public ResponseEntity<ApiResponse<RoomDeleteResponse>> deleteRoomByWorkspaceIdAndSessionIdAndUserId(
		@PathVariable("workspaceId") String workspaceId,
		@PathVariable("sessionId") String sessionId,
		@PathVariable("userId") String userId
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: DELETE "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ sessionId + "/"
				+ userId,
			"deleteRoomByWorkspaceIdAndSessionIdAndUserId"
		);
		if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId) || StringUtils.isBlank(userId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<RoomDeleteResponse> responseData = roomService.deleteRoomById(
			workspaceId,
			sessionId,
			userId
		);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "Update Room Information", notes = "특정 원격협업 방 상세 정보를 수정하는 API 입니다.")
	@PostMapping(value = "room/{workspaceId}/{sessionId}/info")
	public ResponseEntity<ApiResponse<RoomDetailInfoResponse>> updateRoomByWorkspaceIdAndSessionId(
		@PathVariable("workspaceId") String workspaceId,
		@PathVariable("sessionId") String sessionId,
		@RequestBody @Valid ModifyRoomInfoRequest modifyRoomInfoRequest,
		BindingResult result
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: POST "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ sessionId + "::"
				+ "ModifyRoomInfoRequest:" + modifyRoomInfoRequest.toString(),
			"updateRoomByWorkspaceIdAndSessionId"
		);
		if (result.hasErrors() || Strings.isBlank(workspaceId) || Strings.isBlank(sessionId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<RoomDetailInfoResponse> responseData = roomService.updateRoom(
			workspaceId,
			sessionId,
			modifyRoomInfoRequest
		);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "Join a Specific Room", notes = "특정 원격협업 방에 접속하는 API 입니다.")
	@PostMapping(value = "room/{workspaceId}/{sessionId}/join")
	public ResponseEntity<ApiResponse<RoomResponse>> joinRoomByWorkspaceIdAndSessionId(
		@PathVariable("workspaceId") String workspaceId,
		@PathVariable("sessionId") String sessionId,
		@RequestBody @Valid JoinRoomRequest joinRoomRequest,
		BindingResult result
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: POST "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ sessionId + "::"
				+ "JoinRoomRequest:" + joinRoomRequest.toString(),
			"joinRoomByWorkspaceIdAndSessionId"
		);
		if (result.hasErrors() || Strings.isBlank(workspaceId) || Strings.isBlank(sessionId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<RoomResponse> responseData = roomService.joinRoomById(
			workspaceId,
			sessionId,
			joinRoomRequest
		);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "Exit Specific Room", notes = "특정 원격협업 방을 나가는 API 입니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sessionId", value = "원격협업 방 Session ID", dataType = "string", paramType = "path", required = true),
		@ApiImplicitParam(name = "userId", value = "사용자 uuid", dataType = "string", paramType = "query", required = true)
	})
	@DeleteMapping(value = "room/{workspaceId}/{sessionId}/exit")
	public ResponseEntity<ApiResponse<ResultResponse>> exitRoomByWorkspaceIdAndSessionId(
		@PathVariable("workspaceId") String workspaceId,
		@PathVariable("sessionId") String sessionId,
		@RequestParam("userId") String userId
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: POST "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ sessionId + "::"
				+ "userId:" + userId,
			"exitRoomByWorkspaceIdAndSessionId"
		);
		if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId) || StringUtils.isBlank(userId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<ResultResponse> responseData = roomService.exitRoomBySessionIdAndUserId(
			workspaceId,
			sessionId,
			userId
		);
		return ResponseEntity.ok(responseData);
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
				+ workspaceId + "/"
				+ sessionId + "::"
				+ "InviteRoomRequest:" + inviteRoomRequest.toString(),
			"inviteMember"
		);
		if (result.hasErrors() || StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<ResultResponse> responseData = roomService.inviteMember(
			workspaceId,
			sessionId,
			inviteRoomRequest
		);
		return ResponseEntity.ok(responseData);
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
				+ workspaceId + "/"
				+ sessionId + "::"
				+ "KickRoomRequest:" + kickRoomRequest.toString(),
			"kickOutMember"
		);
		if (result.hasErrors() || StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<ResultResponse> responseData = roomService.kickOutMember(
			workspaceId,
			sessionId,
			kickRoomRequest
		);
		return ResponseEntity.ok(responseData);
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
				+ REST_PATH + "/"
				+ workspaceId + "::"
				+ "SendSignalRequest:" + sendSignalRequest.toString(),
			"sendSignal"
		);
		if (result.hasErrors() || StringUtils.isBlank(workspaceId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<ResultResponse> responseData = roomService.sendSignal(
			workspaceId,
			sendSignalRequest
		);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "Forced Logout", notes = "강제 로그아웃 API 입니다")
	@PostMapping(value = "message/push/forced-logout")
	ResponseEntity<ApiResponse<MemberInfoListResponse>> forcedLogout(
		@RequestBody @Valid ForceLogoutRequest forceLogoutRequest,
		BindingResult result
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: POST "
				+ REST_PATH
				+ forceLogoutRequest.toString()
				+ "/message/push/forced-logout",
			"forcedLogout"
		);
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message ->
				LogMessage.formedError(
					TAG,
					"REST API: POST " + REST_PATH + "/message/push/forced-logout",
					"forcedLogout",
					LogMessage.PARAMETER_ERROR,
					message.toString()
				)
			);
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<MemberInfoListResponse> responseData = serviceSessionManager.forceLogout(forceLogoutRequest);
		return ResponseEntity.ok(responseData);
	}
}