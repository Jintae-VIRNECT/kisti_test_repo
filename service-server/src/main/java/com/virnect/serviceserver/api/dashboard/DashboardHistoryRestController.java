package com.virnect.serviceserver.api.dashboard;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.dashboard.application.DashboardHistoryService;
import com.virnect.dashboard.dto.request.RoomHistoryDetailRequest;
import com.virnect.dashboard.dto.request.RoomHistoryListRequest;
import com.virnect.dashboard.dto.request.RoomHistoryStatsRequest;
import com.virnect.dashboard.dto.response.HistoryCountResponse;
import com.virnect.dashboard.dto.response.RoomHistoryDetailInfoResponse;
import com.virnect.dashboard.dto.response.RoomHistoryInfoListResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote/dashboard/")
public class DashboardHistoryRestController {

	private final DashboardHistoryService historyService;
	/*
	 *  1. 워크스페이스 내의 모든 협업 기록을 반환하는 API
	 *	4. 워크스페이스의 지정일 내에서 발생한 시간별 개인 & 전체 협업 수
	 *	5. 워크스페이스의 지정 월 내에서 발생한 일별 개인 & 전체 협업 수
	 *	10.워크스페이스 내에 본인이 참여한 협업 목록 반환하는 API
	 *  14.협업 상세정보를 반환하는 API
	 */

	/**
	 * 1. 워크스페이스 내의 모든 협업 기록을 반환하는 API
	 */
	@ApiOperation(value = "[워크스페이스] 내의 모든 협업 기록을 반환하는 API")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceId", value = "워크스페이스 ID", defaultValue = "4bfcfbfa4375b2f5a85fbfbb277612ff", required = true),
		@ApiImplicitParam(name = "paging", value = "검색 결과 페이지네이션 여부", dataType = "boolean", paramType = "query", allowEmptyValue = true, defaultValue = "true"),
		@ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", paramType = "query", defaultValue = "0"),
		@ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "20"),
		@ApiImplicitParam(name = "fromTo", value = "조회 기간", paramType = "query", defaultValue = "2020-12-01,2020-12-30"),
		@ApiImplicitParam(name = "status", value = "협업 진행 유무(전체(ALL)/진행중(ONGOING)/종료됨(END))", paramType = "query", defaultValue = "ALL"),
		@ApiImplicitParam(name = "searchWord", value = "검색어", paramType = "query"),
		@ApiImplicitParam(name = "sortProperties",
				value = "정렬(NO, TITLE, LEADER_NICK_NAME, ACTIVE_DATE, STATUS, SERVER_RECORD_FILE_COUNT, LOCAL_RECORD_FILE_COUNT, ATTACHED_FILE_COUNT)",
				paramType = "query", defaultValue = "ACTIVE_DATE"),
		@ApiImplicitParam(name = "sortOrder",
				value = "ASC or DESC",
				paramType = "query", defaultValue = "ASC")
	})
	@GetMapping(value = "history/{workspaceId}")
	ResponseEntity<ApiResponse<RoomHistoryInfoListResponse>> getRoomHistoryRequestHandler(
		@PathVariable(name = "workspaceId") String workspaceId,
		@ApiIgnore RoomHistoryListRequest option,
		BindingResult bindingResult
	) {
		if (bindingResult.hasErrors() || workspaceId.isEmpty()) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}

		RoomHistoryInfoListResponse responseData = historyService.getRoomHistory(option, workspaceId, null);

		return ResponseEntity.ok(new ApiResponse<>(responseData));
	}

	/**
	 * 4. 워크스페이스의 지정일 내에서 발생한 시간별 개인 & 전체 협업 수
	 */
	@ApiOperation(value = "[워크스페이스] 내의 지정 '일' 내에서 발생한 시간별 개인 & 전체 협업 수를 반환하는 API")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceId", value = "워크스페이스 ID", defaultValue = "4bfcfbfa4375b2f5a85fbfbb277612ff", required = true),
		@ApiImplicitParam(name = "userId", value = "유저 uuid", defaultValue = "40467b0c2dd94a83a8c69d70fc54038d", required = true),
		@ApiImplicitParam(name = "selectedDate", value = "지정 일", paramType = "query", defaultValue = "2020-11-03", required = true),
		@ApiImplicitParam(name = "timeDifference", value = "시차 시간(단위:분)", paramType = "query", defaultValue = "-540")
	})
	@GetMapping(value = "history/count/date/{workspaceId}/{userId}")
	ResponseEntity<ApiResponse<HistoryCountResponse>> getRoomHistoryWithInDateRequestHandler(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "userId") String userId,
		@RequestParam(name = "selectedDate") String selectedDate,
		@RequestParam(name = "timeDifference") int timeDifference
	) {
		if (workspaceId.isEmpty() || userId.isEmpty() || selectedDate.isEmpty()) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		RoomHistoryStatsRequest option = RoomHistoryStatsRequest.builder()
			.workspaceId(workspaceId)
			.userId(userId)
			.period(selectedDate)
			.diffTime(timeDifference)
			.build();

		HistoryCountResponse responseData = historyService.getRoomHistoryStatsInDate(option);

		return ResponseEntity.ok(new ApiResponse<>(responseData));
	}

	/**
	 * 5. 워크스페이스의 지정 월 내에서 발생한 일별 개인 & 전체 협업 수
	 */
	@ApiOperation(value = "[특정 워크스페이스] 내의 지정 '월' 내에서 발생한 시간별 개인 & 전체 협업 수를 반환하는 API")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceId", value = "워크스페이스 ID", defaultValue = "4bfcfbfa4375b2f5a85fbfbb277612ff", required = true),
		@ApiImplicitParam(name = "userId", value = "유저 uuid", defaultValue = "40467b0c2dd94a83a8c69d70fc54038d", required = true),
		@ApiImplicitParam(name = "selectedMonth", value = "지정 월", paramType = "query", defaultValue = "2020-11", required = true),
		@ApiImplicitParam(name = "timeDifference", value = "시차 시간(단위:분)", paramType = "query", defaultValue = "-540")
	})
	@GetMapping(value = "history/count/month/{workspaceId}/{userId}")
	ResponseEntity<ApiResponse<HistoryCountResponse>> getRoomHistoryWithInMonthRequestHandler(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "userId") String userId,
		@RequestParam(name = "selectedMonth") String selectedMonth,
		@RequestParam(name = "timeDifference") int timeDifference
	) {
		if (workspaceId.isEmpty() || userId.isEmpty() || selectedMonth.isEmpty()) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}

		RoomHistoryStatsRequest option = RoomHistoryStatsRequest.builder()
			.workspaceId(workspaceId)
			.userId(userId)
			.period(selectedMonth)
			.diffTime(timeDifference)
			.build();

		HistoryCountResponse responseData = historyService.getRoomHistoryStatsOnMonth(option);

		return ResponseEntity.ok(new ApiResponse<>(responseData));
	}

	/**
	 * 10. 워크스페이스 내에 본인이 참여한 협업 목록 반환하는 API
	 */
	@ApiOperation(value = "[워크스페이스] 내에 본인이 참여한 협업 목록 반환")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceId", value = "워크스페이스 ID", defaultValue = "4bfcfbfa4375b2f5a85fbfbb277612ff", required = true),
		@ApiImplicitParam(name = "userId", value = "유저 uuid", defaultValue = "4d127135f699616fb88e6bc4fa6d784a", required = true),
		@ApiImplicitParam(name = "paging", value = "검색 결과 페이지네이션 여부", dataType = "boolean", paramType = "query", allowEmptyValue = true, defaultValue = "true"),
		@ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", paramType = "query", defaultValue = "0"),
		@ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "20"),
		@ApiImplicitParam(name = "fromTo", value = "조회 기간", paramType = "query", defaultValue = "2020-12-01,2020-12-30"),
		@ApiImplicitParam(name = "status", value = "협업 진행 유무(전체(ALL)/진행중(ONGOING)/종료됨(END))", paramType = "query", defaultValue = "ALL"),
		@ApiImplicitParam(name = "searchWord", value = "검색어", paramType = "query"),
		@ApiImplicitParam(name = "sortProperties",
				value = "정렬(NO, TITLE, LEADER_NICK_NAME, ACTIVE_DATE, STATUS, SERVER_RECORD_FILE_COUNT, LOCAL_RECORD_FILE_COUNT, ATTACHED_FILE_COUNT)",
				paramType = "query", defaultValue = "ACTIVE_DATE"),
		@ApiImplicitParam(name = "sortOrder",
				value = "ASC or DESC",
				paramType = "query", defaultValue = "ASC")
	})
	@GetMapping(value = "my-history/{workspaceId}/{userId}")
	ResponseEntity<ApiResponse<RoomHistoryInfoListResponse>> getRoomHistoryMineRequestHandler(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "userId") String userId,
		@ApiIgnore RoomHistoryListRequest option,
		BindingResult bindingResult
	) {
		if (workspaceId.isEmpty() || userId.isEmpty() || bindingResult.hasErrors()) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}

		option.setWorkspaceId(workspaceId);
		option.setUserId(userId);
		RoomHistoryInfoListResponse responseData = historyService.getRoomHistory(option, workspaceId, userId);

		return ResponseEntity.ok(new ApiResponse<>(responseData));
	}

	/**
	 * 14. 협업 상세정보를 반환하는 API
	 */
	@ApiOperation(value = "특정 원격협업 방 최근 기록 상세 정보를 조회하는 API 입니다.")
	@GetMapping(value = "history/{workspaceId}/{sessionId}")
	ResponseEntity<ApiResponse<RoomHistoryDetailInfoResponse>> getRoomHistoryDetailRequestHandler(
		@PathVariable("workspaceId") String workspaceId,
		@PathVariable("sessionId") String sessionId
	) {
		if (sessionId.isEmpty()) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}

		RoomHistoryDetailRequest option = RoomHistoryDetailRequest.builder()
			.workspaceId(workspaceId)
			.sessionId(sessionId)
			.build();

		RoomHistoryDetailInfoResponse responseData = historyService.getEndRoomDetail(option);

		return ResponseEntity.ok(new ApiResponse<>(responseData));
	}

}
