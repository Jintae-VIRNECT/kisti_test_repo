package com.virnect.content.api;

import org.springframework.http.ResponseEntity;
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
import com.virnect.content.dto.response.LiveShareResponse;
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
		@ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query", required = true, example = "4d6eab0860969a50acbfa4599fbb5ae8"),
		@ApiImplicitParam(name = "userUUID", value = "유저 식별자", dataType = "string", paramType = "query", required = true, example = "498b1839dc29ed7bb2ee90ad6985c608"),
	})
	@PostMapping("/{contentUUID}/liveShare")
	public ResponseEntity<ApiResponse<LiveShareResponse>> joinLiveShareRoom(
		@PathVariable("contentUUID") String contentUUID,
		@RequestParam("workspaceUUID") String workspaceUUID,
		@RequestParam("userUUID") String userUUID
	) {
		if (StringUtils.isEmpty(contentUUID) || StringUtils.isEmpty(workspaceUUID) || StringUtils.isEmpty(userUUID)) {
			throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		LiveShareResponse responseMessage = liveShareService.joinLiveShareRoom(
			contentUUID, workspaceUUID, userUUID);
		return ResponseEntity.ok(new ApiResponse<>(responseMessage));
	}
}
