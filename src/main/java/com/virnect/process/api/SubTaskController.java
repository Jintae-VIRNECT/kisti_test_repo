package com.virnect.process.api;

import java.util.List;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.process.application.SubTaskService;
import com.virnect.process.domain.Conditions;
import com.virnect.process.dto.request.EditSubProcessRequest;
import com.virnect.process.dto.response.MyWorkListResponse;
import com.virnect.process.dto.response.SubProcessInfoResponse;
import com.virnect.process.dto.response.SubProcessListResponse;
import com.virnect.process.dto.response.SubProcessesOfTargetResponse;
import com.virnect.process.dto.response.SubProcessesResponse;
import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.common.ApiResponse;
import com.virnect.process.global.common.PageRequest;
import com.virnect.process.global.common.ResponseMessage;
import com.virnect.process.global.error.ErrorCode;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-08-27
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class SubTaskController {
	private final SubTaskService subTaskService;

	/**
	 * 하위작업목록조회
	 *
	 * @param taskId 작업 식별자
	 * @return
	 */
	@ApiOperation(value = "공정 내 하위작업목록조회")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "taskId", value = "작업 식별자", dataType = "string", paramType = "path", required = true, example = "1"),
		@ApiImplicitParam(name = "search", value = "검색어 - name을 검색", dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "작업상태 필터(WAIT,UNPROGRESSING,PROGRESSING,COMPLETED,INCOMPLETED,FAILED,SUCCESS,FAULT,NONE,ALL)", dataType = "object", paramType = "query", defaultValue = "PROGRESSING"),
		@ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
		@ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
		@ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건 - startDate, endDate, name, priority, reportedDate)", dataType = "String", paramType = "query", defaultValue = "updatedDate,desc"),
		@ApiImplicitParam(name = "userUUID", value = "사용자 식별자(내 작업)", dataType = "string", paramType = "query")
	})
	@GetMapping("{taskId}/subTasks")
	public ResponseEntity<ApiResponse<SubProcessListResponse>> getSubProcessList(
		@PathVariable("taskId") Long taskId
		, @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
		, @RequestParam(value = "search", required = false) String search
		, @RequestParam(value = "filter", required = false) List<Conditions> filter
		, @RequestParam(value = "userUUID", required = false) String userUUID
		, @ApiIgnore PageRequest pageable
	) {
		if (Objects.isNull(taskId)) {
			log.info("[taskId] => [{}]", taskId);
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<SubProcessListResponse> subProcessListResponseApiResponse = this.subTaskService.getSubProcessList(
			taskId, workspaceUUID, search, userUUID, filter, pageable.of());
		return ResponseEntity.ok(subProcessListResponseApiResponse);
	}

	/**
	 * 워크스페이스의 전체 하위작업목록조회
	 *
	 * @param taskId 작업 식별자
	 * @return
	 */
	@ApiOperation(value = "워크스페이스의 전체 하위작업목록조회")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "taskId", value = "작업 식별자", dataType = "string", paramType = "query", example = ""),
		@ApiImplicitParam(name = "search", value = "검색어 - user, name을 검색", dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
		@ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
		@ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건)", dataType = "string", paramType = "query", defaultValue = "updatedDate,desc"),
		@ApiImplicitParam(name = "filter", value = "작업상태 필터(WAIT,UNPROGRESSING,PROGRESSING,COMPLETED,INCOMPLETED,FAILED,SUCCESS,FAULT,NONE,ALL)", dataType = "object", paramType = "query", defaultValue = "PROGRESSING")
	})
	@GetMapping("/subTasks")
	public ResponseEntity<ApiResponse<SubProcessesResponse>> getSubProcesses(
		@RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
		, @RequestParam(required = false, value = "taskId") Long taskId
		, @RequestParam(value = "search", required = false) String search
		, @RequestParam(value = "filter", required = false) List<Conditions> filter
		, @ApiIgnore PageRequest pageable
	) {
		ApiResponse<SubProcessesResponse> subProcessesResponseApiResponse = this.subTaskService.getSubProcesses(
			workspaceUUID, taskId, search, pageable.of(), filter);
		return ResponseEntity.ok(subProcessesResponseApiResponse);
	}

	/**
	 * 하위작업 상세조회
	 *
	 * @param subTaskId 하위작업 식별자
	 * @return
	 */
	@ApiOperation(value = "하위작업 상세조회")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "subTaskId", value = "하위작업 식별자", dataType = "string", paramType = "path", required = true, example = "1"),
	})
	@GetMapping("/subTasks/{subTaskId}")
	public ResponseEntity<ApiResponse<SubProcessInfoResponse>> getSubProcess(
		@PathVariable("subTaskId") Long subTaskId
	) {
		ApiResponse<SubProcessInfoResponse> subProcessesResponseApiResponse = this.subTaskService.getSubProcess(
			subTaskId);
		return ResponseEntity.ok(subProcessesResponseApiResponse);
	}

	/**
	 * 내 하위작업(나에게 할당된 하위작업) 목록 조회
	 *
	 * @param pageable
	 * @return
	 */
	@ApiOperation(value = "내 하위작업(나에게 할당된 하위작업) 목록 조회", notes = "파라미터의 작업 식별자, 검색어로의 필터 기능은 아직 제공되지 않습니다.\n담당자의 하위작업 목록 조회만 가능합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "workerUUID", value = "담당자 식별자", dataType = "string", paramType = "path", required = true, defaultValue = "449ae69cee53b8a6819053828c94e496"),
		@ApiImplicitParam(name = "taskId", value = "작업 식별자", dataType = "string", paramType = "query", defaultValue = ""),
		@ApiImplicitParam(name = "search", value = "검색어(콘텐츠명/사용자명) - 미개발", dataType = "string", defaultValue = ""),
		@ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
		@ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
		@ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건)", dataType = "String", paramType = "query", defaultValue = "updatedDate,desc"),
		@ApiImplicitParam(name = "target", value = "컨텐츠의 타겟 타입(ALL, QR, VTarget)", dataType = "string", paramType = "query", defaultValue = "ALL")
	})
	@GetMapping("/myWorks/{workerUUID}")
	public ResponseEntity<ApiResponse<MyWorkListResponse>> getMyWorks(
		@RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
		, @PathVariable("workerUUID") String workerUUID
		, @RequestParam(required = false, value = "taskId") Long taskId
		, @RequestParam(required = false, value = "search") String search
		, @RequestParam(value = "target", required = false, defaultValue = "ALL") String targetType
		, @ApiIgnore PageRequest pageable
	) {
		if (workerUUID.isEmpty()) {
			log.info("[workerUUID] => [{}]", workerUUID);
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<MyWorkListResponse> myWorkListResponseApiResponse = this.subTaskService.getMyWorks(
			workspaceUUID, workerUUID, taskId, search, pageable.of(), targetType);
		return ResponseEntity.ok(myWorkListResponseApiResponse);
	}

	@ApiOperation(value = "타겟 데이터의 하위작업 목록 조회"
		, notes = "타겟 데이터가 URLEncodeing된 형태로 바뀜에 따라 pathVariable -> requestParam으로 변경")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "targetData", value = "타겟 데이터 식별자", dataType = "string", required = true, paramType = "query", defaultValue = ""),
		@ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
		@ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
		@ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건)", dataType = "String", paramType = "query", defaultValue = "createdDate,desc")
	})
	@GetMapping("/target")
	public ResponseEntity<ApiResponse<SubProcessesOfTargetResponse>> getSubProcessesOfTargetData(
		@RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
		, @RequestParam("targetData") String targetData
		, @ApiIgnore PageRequest pageable
	) {
		if (targetData.isEmpty()) {
			log.info("[targetData] => [{}]", targetData);
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}

		ApiResponse<SubProcessesOfTargetResponse> subProcessesOfTargetResponseApiResponse = this.subTaskService.getSubProcessesOfTarget(
			workspaceUUID, targetData, pageable.of());
		return ResponseEntity.ok(subProcessesOfTargetResponseApiResponse);
	}

	/**
	 * 하위작업편집
	 *
	 * @param subTaskId
	 * @return
	 */
	@ApiOperation(value = "하위작업편집")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "subTaskId", value = "하위작업 식별자", dataType = "string", paramType = "path", required = true, example = "1"),
	})
	@PostMapping("/subTasks/{subTaskId}")
	public ResponseEntity<ResponseMessage> updateSubProcess(
		@PathVariable("subTaskId") Long subTaskId
		, @RequestBody EditSubProcessRequest subProcessRequest
	) {
		if (subTaskId == null) {
			log.info("[subTaskId] => [{}]", subTaskId);
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ResponseMessage responseMessage = this.subTaskService.updateSubProcess(subTaskId, subProcessRequest);
		return ResponseEntity.ok(responseMessage);
	}
}
