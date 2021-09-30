package com.virnect.serviceserver.servicedashboard.api;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.servicedashboard.application.DashboardHistoryService;
import com.virnect.serviceserver.servicedashboard.dto.response.RoomDetailInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote/dashboard")
public class DashboardSessionRestController {

	private static final String TAG = DashboardSessionRestController.class.getSimpleName();
	private static final String REST_PATH = "/remote/dashboard";

	private final DashboardHistoryService historyService;

	@ApiOperation(value = "특정 원격협업 방 상세 정보를 조회하는 API 입니다.")
	@GetMapping(value = "room/{workspaceId}/{sessionId}")
	ResponseEntity<ApiResponse<RoomDetailInfoResponse>> getRoomDetailInfoRequestHandler(
		@PathVariable("workspaceId") String workspaceId,
		@PathVariable("sessionId") String sessionId
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: GET "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ sessionId,
			"getRoomDetailInfoRequestHandler"
		);
		if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		return ResponseEntity.ok(
			new ApiResponse<>(historyService.getOngoingRoomDetail(workspaceId, sessionId))
		);
	}
}
