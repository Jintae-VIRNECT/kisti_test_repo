package com.virnect.content.api;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.application.LiveShareService;
import com.virnect.content.dto.response.LiveShareJoinResponse;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.common.ApiResponse;
import com.virnect.content.global.error.ErrorCode;

@Slf4j
@RestController
@RequestMapping("/contents")
@RequiredArgsConstructor
public class LiveShareController {
	private final LiveShareService liveShareService;

	@ApiOperation(value = "컨텐츠 실시간 공유 참여", notes = "컨텐츠 실시간 공유 협업 룸에 참여합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "contentUUID", value = "컨텐츠 식별자", dataType = "string", paramType = "path", required = true, example = "3ac931f7-5b3b-4807-ac6e-61ae5d138204"),
		@ApiImplicitParam(name = "userUUID", value = "유저 식별자", dataType = "string", paramType = "query", required = true, example = "4a65aa94523efe5391b0541bbbcf97a3"),
	})
	@PostMapping("/{contentUUID}/liveShare")
	public ResponseEntity<ApiResponse<LiveShareJoinResponse>> joinLiveShareRoom(
		@PathVariable("contentUUID") String contentUUID,
		@RequestParam("userUUID") String userUUID
	) {
		log.info(
			"[JOIN_LIVE_SHARE_ROOM] CONTENT : {}, USER : {}", contentUUID, userUUID);
		if (StringUtils.isEmpty(contentUUID) || StringUtils.isEmpty(userUUID)) {
			throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		LiveShareJoinResponse responseMessage = liveShareService.joinLiveShareRoom(
			contentUUID, userUUID);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}

	@MessageMapping("/api/contents/{contentUUID}/room/{roomId}")
	public void publishContentWriteMessage(
		@DestinationVariable("contentUUID") String contentUUID, @DestinationVariable("roomId") String roomId,
		@Payload String message
	) {
		log.info("[PUB_CONTENT_WRITING_MESSAGE] CONTENT : {}, ROOM : {}", contentUUID, roomId);
		liveShareService.publishContentWriteMessage(contentUUID, roomId, message);
	}
}
