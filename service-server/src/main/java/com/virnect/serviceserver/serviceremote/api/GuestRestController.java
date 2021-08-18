package com.virnect.serviceserver.serviceremote.api;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dto.request.room.JoinRoomRequest;
import com.virnect.data.dto.response.guest.GuestInfoResponse;
import com.virnect.data.dto.response.guest.GuestInviteUrlResponse;
import com.virnect.data.dto.response.room.RoomResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.serviceremote.application.RoomService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class GuestRestController {

	private static final String TAG = GuestRestController.class.getSimpleName();
	private static final String REST_PATH = "/remote/guest";

	private final RoomService roomService;

	@ApiOperation(value = "Create Guest invite URL", notes = "Guest 멤버를 초대하는 URL을 생성합니다")
	@GetMapping(value = "guest/url/{workspaceId}/{sessionId}")
	ResponseEntity<ApiResponse<GuestInviteUrlResponse>> createGuestInviteUrl(
		@PathVariable("workspaceId") String workspaceId,
		@PathVariable("sessionId") String sessionId
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: GET "
				+ REST_PATH
				+ workspaceId + "/"
				+ sessionId,
			"createGuestInviteUrl"
		);
		ApiResponse<GuestInviteUrlResponse> responseData = roomService.createGuestInviteUrl(workspaceId, sessionId);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "Get Guest and open room info", notes = "Guest 정보 및 Open room 정보를 반환합니다")
	@GetMapping(value = "invitation/guest/{workspaceId}/{sessionId}")
	ResponseEntity<ApiResponse<GuestInfoResponse>> createGuestAndRoomInfo(
		@PathVariable("workspaceId") String workspaceId,
		@PathVariable("sessionId") String sessionId,
		HttpServletRequest request
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: GET "
				+ REST_PATH
				+ workspaceId + "/"
				+ sessionId,
			"createGuestAndRoomInfo"
		);
		ApiResponse<GuestInfoResponse> responseData = roomService.getGuestAndRoomInfoResponse(workspaceId, sessionId, request);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "Join open room (use only guest)", notes = "Guest가 오픈방에 참여합니다")
	@PostMapping(value = "guest/room/{workspaceId}/{sessionId}/join")
	ResponseEntity<ApiResponse<RoomResponse>> joinOpenRoomByGuest(
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
			"joinOpenRoomByGuest"
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

}
