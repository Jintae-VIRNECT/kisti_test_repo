package com.virnect.content.api;

import javax.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.application.LiveShareService;
import com.virnect.content.dto.request.LiveShareRoomLeaveRequest;
import com.virnect.content.dto.response.LiveShareJoinResponse;
import com.virnect.content.dto.response.LiveShareLeaveResponse;
import com.virnect.content.dto.response.LiveShareUserRoleUpdateResponse;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.common.ApiResponse;
import com.virnect.content.global.error.ErrorCode;
import com.virnect.content.global.util.CurrentUserUtils;

@Slf4j
@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
public class LiveShareController {
	private final LiveShareService liveShareService;

	@ApiOperation(value = "컨텐츠 실시간 공유 참여", notes = "컨텐츠 실시간 공유 협업 룸에 참여합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "contentUUID", value = "컨텐츠 식별자", dataType = "string", paramType = "path", required = true, example = "3ac931f7-5b3b-4807-ac6e-61ae5d138204"),
		@ApiImplicitParam(name = "Authorization", value = "인증 헤더", required = true, dataType = "string", paramType = "header", defaultValue = "Bearer "),})
	@PostMapping("/{contentUUID}/liveShare")
	public ResponseEntity<ApiResponse<LiveShareJoinResponse>> joinLiveShareRoom(
		@PathVariable("contentUUID") String contentUUID
	) {
		log.info("[JOIN_LIVE_SHARE_ROOM] CONTENT : {}, USER : {}", contentUUID, CurrentUserUtils.getUserUUID());
		if (StringUtils.isEmpty(contentUUID) || StringUtils.isEmpty(CurrentUserUtils.getUserUUID())) {
			throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		LiveShareJoinResponse responseMessage = liveShareService.joinLiveShareRoom(contentUUID);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(value = "컨텐츠 실시간 공유 종료", notes = "컨텐츠 실시간 공유 협업 룸에서 나갑니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "contentUUID", value = "컨텐츠 식별자", dataType = "string", paramType = "path", required = true, example = "3ac931f7-5b3b-4807-ac6e-61ae5d138204"),
		@ApiImplicitParam(name = "roomId", value = "협업 룸 식별자", dataType = "string", paramType = "path", required = true, example = "45"),
		@ApiImplicitParam(name = "Authorization", value = "인증 헤더", required = true, dataType = "string", paramType = "header", defaultValue = "Bearer "),})
	@PostMapping("/{contentUUID}/liveShare/rooms/{roomId}/leave")
	public ResponseEntity<ApiResponse<LiveShareLeaveResponse>> leaveLiveShareRoom(
		@PathVariable("contentUUID") String contentUUID, @PathVariable("roomId") Long roomId,
		@RequestBody LiveShareRoomLeaveRequest liveShareRoomLeaveRequest
	) {
		log.info(
			"[LEAVE_LIVE_SHARE_ROOM] CONTENT : {}, ROOM : {}, USER : {}", contentUUID, roomId,
			CurrentUserUtils.getUserUUID()
		);
		if (StringUtils.isEmpty(contentUUID) || StringUtils.isEmpty(CurrentUserUtils.getUserUUID()) || roomId == null) {
			throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		LiveShareLeaveResponse responseMessage = liveShareService.leaveLiveShareRoom(
			contentUUID, roomId, liveShareRoomLeaveRequest);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@ApiOperation(value = "컨텐츠 실시간 공유 리더 양도", notes = "컨텐츠 실시간 공유 협업 룸 내에서 리더 권한을 양도합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "contentUUID", value = "컨텐츠 식별자", dataType = "string", paramType = "path", required = true, example = "3ac931f7-5b3b-4807-ac6e-61ae5d138204"),
		@ApiImplicitParam(name = "roomId", value = "협업 룸 식별자", dataType = "string", paramType = "path", required = true, example = "45"),
		@ApiImplicitParam(name = "userUUID", value = "유저 식별자", dataType = "string", paramType = "path", required = true, example = "4a65aa94523efe5391b0541bbbcf97a3"),
		@ApiImplicitParam(name = "Authorization", value = "인증 헤더", required = true, dataType = "string", paramType = "header", defaultValue = "Bearer "),})
	@PutMapping("/{contentUUID}/liveShare/rooms/{roomId}/users/{userUUID}/role/LEADER")
	public ResponseEntity<ApiResponse<LiveShareUserRoleUpdateResponse>> updateLiveShareUserRole(
		@PathVariable("contentUUID") @NotBlank String contentUUID, @PathVariable("roomId") Long roomId,
		@PathVariable("userUUID") String updateUserUUID
	) {
		log.info(
			"[LEAVE_LIVE_SHARE_ROOM] CONTENT : {}, ROOM : {}, USER : {}, UPDATE_USER : {}", contentUUID, roomId,
			CurrentUserUtils.getUserUUID(), updateUserUUID
		);
		if (StringUtils.isEmpty(contentUUID) || StringUtils.isEmpty(CurrentUserUtils.getUserUUID())
			|| StringUtils.isEmpty(updateUserUUID)
			|| roomId == null) {
			throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		LiveShareUserRoleUpdateResponse responseMessage = liveShareService.updateLiveShareUserRole(
			contentUUID, updateUserUUID, roomId);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@MessageMapping("/api/contents/{contentUUID}/rooms/{roomId}")
	public void publishContentWriteMessage(
		@DestinationVariable("contentUUID") String contentUUID, @DestinationVariable("roomId") String roomId,
		@Payload String message
	) {
		log.info("[PUB_CONTENT_WRITING_MESSAGE] CONTENT : {}, ROOM : {}", contentUUID, roomId);
		liveShareService.publishContentWriteMessage(contentUUID, roomId, message);
	}
}
