package com.virnect.serviceserver.serviceremote.api;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.domain.member.MemberType;
import com.virnect.data.dto.request.guest.EventRequest;
import com.virnect.data.dto.request.room.JoinRoomRequest;
import com.virnect.data.dto.response.guest.GuestInfoResponse;
import com.virnect.data.dto.response.room.RoomInfoResponse;
import com.virnect.data.dto.response.room.RoomResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.serviceremote.application.GuestService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class GuestRestController {

	private static final String TAG = GuestRestController.class.getSimpleName();
	private static final String REST_PATH = "/remote/guest";

	private final GuestService guestService;

	@ApiOperation(value = "Get Guest member info", notes = "Guest 정보를 반환합니다")
	@GetMapping(value = "invitation/guest/{workspaceId}")
	ResponseEntity<ApiResponse<GuestInfoResponse>> getGuestMemberInfo(
		@PathVariable("workspaceId") String workspaceId,
		HttpServletRequest request
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: GET "
				+ REST_PATH
				+ workspaceId,
			"getGuestMemberInfo"
		);
		ApiResponse<GuestInfoResponse> responseData = guestService.getGuestInfo(workspaceId, request);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "Get open room info", notes = "Guest가 참여할 Open room 정보를 반환합니다")
	@GetMapping(value = "guest/room/{workspaceId}/{sessionId}")
	ResponseEntity<ApiResponse<RoomInfoResponse>> getOpenRoomInfo(
		@PathVariable("workspaceId") String workspaceId,
		@PathVariable("sessionId") String sessionId
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: GET "
				+ REST_PATH
				+ workspaceId + "/"
				+ sessionId,
			"getOpenRoomInfo"
		);
		ApiResponse<RoomInfoResponse> responseData = guestService.getOpenRoomInfo(workspaceId, sessionId);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "Join open room (use only guest)", notes = "Guest가 오픈방에 참여합니다")
	@PostMapping(value = "guest/room/{workspaceId}/{sessionId}/join")
	ResponseEntity<ApiResponse<RoomResponse>> joinOpenRoomByGuest(
		@PathVariable("workspaceId") String workspaceId,
		@PathVariable("sessionId") String sessionId,
		@RequestBody @Valid JoinRoomRequest joinRoomRequest
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: POST "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ sessionId + "::"
				+ "joinRoomRequest:" + joinRoomRequest.toString(),
			"joinOpenRoomByGuest"
		);
		if (Strings.isBlank(workspaceId) || Strings.isBlank(sessionId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		joinRoomRequest.setMemberType(MemberType.GUEST);
		ApiResponse<RoomResponse> responseData = guestService.joinRoomOnlyGuest(
			workspaceId,
			sessionId,
			joinRoomRequest
		);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "Guest member event", notes = "게스트 멤버 이벤트")
	@PostMapping(value = "guest/event")
	public void guestMemberEvent(
		@ModelAttribute @Valid EventRequest eventRequest,
		BindingResult result
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: POST "
				+ REST_PATH + "/"
				+ "guestEvent:" + eventRequest.toString(),
			"guestMemberEvent"
		);
		if (result.hasErrors()) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		guestService.guestEvent(eventRequest);
	}

}
