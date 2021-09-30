package com.virnect.process.api;

import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.process.application.TaskService;
import com.virnect.process.domain.Conditions;
import com.virnect.process.dto.request.CheckProcessOwnerRequest;
import com.virnect.process.dto.request.EditProcessRequest;
import com.virnect.process.dto.request.ProcessDuplicateRequest;
import com.virnect.process.dto.request.ProcessRegisterRequest;
import com.virnect.process.dto.request.WorkResultSyncRequest;
import com.virnect.process.dto.response.ProcessContentAndTargetResponse;
import com.virnect.process.dto.response.ProcessInfoResponse;
import com.virnect.process.dto.response.ProcessListResponse;
import com.virnect.process.dto.response.ProcessRegisterResponse;
import com.virnect.process.dto.response.ProcessSimpleResponse;
import com.virnect.process.dto.response.TaskSecessionResponse;
import com.virnect.process.dto.response.WorkResultSyncResponse;
import com.virnect.process.dto.response.WorkSyncResponse;
import com.virnect.process.dto.response.WorkspaceUserListResponse;
import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.common.ApiResponse;
import com.virnect.process.global.common.PageRequest;
import com.virnect.process.global.common.ResponseMessage;
import com.virnect.process.global.error.ErrorCode;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-14
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Api(produces = MediaType.APPLICATION_JSON_VALUE, value = "작업 서버 API Controller 입니다.")
@Slf4j
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
	private final TaskService taskService;

	/**
	 * 작업과 연계된 ContentUUID와 ARUCO 가져오기
	 *
	 * @param taskId
	 * @return
	 */
	@ApiOperation(value = "작업과 연계된 ContentUUID와 작업의 target data 가져오기")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "taskId", value = "작업 식별자", dataType = "string", paramType = "path", required = true, example = "1")
	})
	//    @GetMapping("/process/content/{taskId}")
	@GetMapping("/task/content/{taskId}")
	public ResponseEntity<ApiResponse<ProcessContentAndTargetResponse>> getRelatedInfoOfProcess(
		@PathVariable("taskId") Long taskId
	) {
		if (taskId == null) {
			log.info("[taskId] => [{}]", taskId);
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<ProcessContentAndTargetResponse> responseMessage = this.taskService.getRelatedInfoOfProcess(taskId);
		return ResponseEntity.ok(responseMessage);
	}

	@ApiOperation(value = "커스텀 메타데이터 가져오기", notes = "요청한 사용자가 할당된 작업만 조회됨.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "taskId", value = "작업 식별자", dataType = "string", paramType = "query", required = true, example = "1"),
		@ApiImplicitParam(name = "subTaskIds", value = "하위작업 식별자 배열(ex : subTask Ids=2,4,12,45)", allowMultiple = true, dataType = "array", paramType = "query", example = "1"),
	})
	@GetMapping("/sync")
	public ResponseEntity<ApiResponse<WorkSyncResponse>> getSyncData(
		@RequestParam(value = "taskId") Long taskId
		, @RequestParam(value = "subTaskIds", required = false) Long[] subTaskIds
	) {
		ApiResponse<WorkSyncResponse> apiResponse = this.taskService.getSyncMeta(taskId, subTaskIds);

		return ResponseEntity.ok(apiResponse);
	}

	/**
	 * 수행결과 업로드(동기화)
	 *
	 * @param workResultSyncRequest - 업로드 요청 수행 결과 데이터
	 * @param result                - 업로드 동기화 여부
	 * @return
	 */
	@ApiOperation(value = "수행결과 업로드(동기화)", notes = "smic와 변경된 점 없음.")
	@PostMapping("/sync")
	public ResponseEntity<ApiResponse<WorkResultSyncResponse>> setWorkResult(
		@RequestBody @Valid WorkResultSyncRequest workResultSyncRequest, BindingResult result
	) {
		if (result.hasErrors()) {
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		log.info("Request Data: {}", workResultSyncRequest.toString());
		ApiResponse<WorkResultSyncResponse> responseMessage = this.taskService.uploadOrSyncWorkResult(
			workResultSyncRequest);
		return ResponseEntity.ok(responseMessage);
	}

	/**
	 * 전체 작업의 목록을 조회
	 *
	 * @param workspaceUUID
	 * @param search
	 * @param myUUID
	 * @param filter
	 * @param pageable
	 * @return
	 */
	@ApiOperation(value = "전체 작업 목록 조회")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "search", value = "검색어 - 작업 제목, 사용자 이메일, 닉네임 검색", dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "myUUID", value = "사용자 식별자 (내 작업)", dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "작업상태 필터(WAIT,UNPROGRESSING,PROGRESSING,COMPLETED,INCOMPLETED,FAILED,SUCCESS,FAULT,NONE,ALL)", dataType = "object", paramType = "query", defaultValue = "PROGRESSING"),
		@ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
		@ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
		@ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건 - reportedDate, name, startDate, endDate, state)", dataType = "String", paramType = "query", defaultValue = "createdDate,desc"),
		@ApiImplicitParam(name = "target", value = "컨텐츠의 타겟 타입(ALL, QR, VTarget, VR)", dataType = "string", paramType = "query", defaultValue = "ALL")
	})
	@GetMapping
	public ResponseEntity<ApiResponse<ProcessListResponse>> getProcessList(
		@RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
		, @RequestParam(value = "search", required = false) String search
		, @RequestParam(value = "filter", required = false) List<Conditions> filter
		, @RequestParam(value = "myUUID", required = false) String myUUID
		, @RequestParam(value = "target", required = false, defaultValue = "ALL") String targetType
		, @ApiIgnore PageRequest pageable
	) {
		ApiResponse<ProcessListResponse> processListApiResponse = this.taskService.getProcessList(
			myUUID, workspaceUUID, search, filter, pageable.of(), targetType);
		return ResponseEntity.ok(processListApiResponse);
	}

	/**
	 * 작업종료
	 *
	 * @return
	 */
	@ApiOperation(value = "작업종료", notes = "진행중인 작업을 종료합니다. 더이상 작업 수행을 할 수 없게 됩니다. 또한 활성화된 작업은 하나이기 때문에 작업이 종료되면 해당 컨텐츠를 삭제할 수 있습니다.")
	@PutMapping("{taskId}/closed")
	public ResponseEntity<ApiResponse<ProcessInfoResponse>> setClosedProcess(
		@PathVariable(value = "taskId") Long taskId,
		@RequestBody @Valid CheckProcessOwnerRequest checkProcessOwnerRequest,
		BindingResult result
	) {
		if (result.hasErrors()) {
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<ProcessInfoResponse> processInfoResponseApiResponse = this.taskService.setClosedProcess(
			taskId, checkProcessOwnerRequest);
		return ResponseEntity.ok(processInfoResponseApiResponse);
	}

	/**
	 * 작업생성
	 *
	 * @param registerNewProcess
	 * @param result
	 * @return
	 */
	@ApiOperation(value = "작업생성", notes = "테스트시 contentUUID 및 subProcessList.id는 컨텐츠 uuid와 메타데이터의 sceneGroup id, workspace uuid가 일치해야 함에 유의\n워크스페이스는 컨텐츠의 워크스페이스와 동일.\n워크스페이스의 아무 컨텐츠로나 작업을 생성할 수 있는지는 아직 확인되지 않음.\n개발 완료 후 기획쪽과 협의 필요.")
	@PostMapping("/task")
	public ResponseEntity<ApiResponse<ProcessRegisterResponse>> createProcess(
		@RequestBody @Valid ProcessRegisterRequest registerNewProcess, BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> {
				log.error("[CONTENTS > TASK] ERROR MESSAGE >> {}", message);
			});
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<ProcessRegisterResponse> responseMessage = this.taskService.createTheProcess(registerNewProcess);
		return ResponseEntity.ok(responseMessage);
	}

	/**
	 * 작업복제
	 *
	 * @param duplicateRequest
	 * @param result
	 * @return
	 */
	@ApiOperation(value = "작업복제", notes = "작업 생성과 동일. 테스트시 contentUUID 및 subProcessList.id는 컨텐츠 uuid와 메타데이터의 sceneGroup id, workspace uuid가 일치해야 함에 유의\n워크스페이스는 컨텐츠의 워크스페이스와 동일.\n워크스페이스의 아무 컨텐츠로나 작업을 생성할 수 있는지는 아직 확인되지 않음.\n개발 완료 후 기획쪽과 협의 필요.")
	@PostMapping("/duplicate")
	public ResponseEntity<ApiResponse<ProcessRegisterResponse>> duplicateProcess(
		@RequestBody @Valid ProcessDuplicateRequest duplicateRequest, BindingResult result
	) {
		if (result.hasErrors()) {
			result.getAllErrors().forEach(message -> {
				log.error("[TASK > TASK] ERROR MESSAGE >> {}", message);
			});
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<ProcessRegisterResponse> responseMessage = this.taskService.duplicateTheProcess(duplicateRequest);
		return ResponseEntity.ok(responseMessage);
	}

	/**
	 * 작업삭제
	 *
	 * @return
	 */
	@ApiOperation(value = "작업삭제", notes = "actorUUID는 작업의 contentManagerUUID와 동일해야 함.")
	@DeleteMapping
	public ResponseEntity<ApiResponse<ProcessSimpleResponse>> deleteProcessHandler(
		@RequestBody @Valid CheckProcessOwnerRequest checkProcessOwnerRequest, BindingResult result
	) {
		if (result.hasErrors()) {
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<ProcessSimpleResponse> processSimpleResponseApiResponse = this.taskService.deleteTheProcess(
			checkProcessOwnerRequest);
		return ResponseEntity.ok(processSimpleResponseApiResponse);
	}

	/**
	 * 작업편집
	 *
	 * @param taskId 작업 식별자
	 * @return
	 */
	@ApiOperation(value = "작업편집")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "taskId", value = "작업 식별자", dataType = "string", paramType = "path", required = true, example = "1")
	})
	@PostMapping("/{taskId}")
	public ResponseEntity<ResponseMessage> updateProcess(
		@PathVariable("taskId") Long taskId
		, @RequestBody @Valid EditProcessRequest editTaskRequest, BindingResult result
	) {
		if (Objects.isNull(taskId) || result.hasErrors()) {
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ResponseMessage responseMessage = this.taskService.updateProcess(editTaskRequest);
		return ResponseEntity.ok(responseMessage);
	}

	/**
	 * 작업상세조회
	 *
	 * @param taskId 작업 식별자
	 * @return
	 */
	@ApiOperation(value = "작업상세조회", notes = "작업 상세내용을 조회합니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "taskId", value = "작업 식별자", dataType = "string", paramType = "path", required = true, example = "1")
	})
	@GetMapping("/{taskId}")
	public ResponseEntity<ApiResponse<ProcessInfoResponse>> getProcessInfo(@PathVariable("taskId") Long taskId) {
		if (Objects.isNull(taskId)) {
			log.info("[taskId] => [{}]", taskId);
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<ProcessInfoResponse> processInfoResponseApiResponse = this.taskService.getProcessInfo(taskId);
		return ResponseEntity.ok(processInfoResponseApiResponse);
	}

	@ApiOperation(value = "워크스페이스 내 사용자 정보", notes = "정렬 아직 안됨")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "path", required = true, defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8"),
		@ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
		@ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
	})
	@GetMapping("{workspaceUUID}/info")
	public ResponseEntity<ApiResponse<WorkspaceUserListResponse>> workspaceInfo(
		@PathVariable("workspaceUUID") String workspaceUUID
		, @ApiIgnore PageRequest pageable
	) {
		ApiResponse<WorkspaceUserListResponse> apiResponse = this.taskService.getWorkspaceUserInfo(
			workspaceUUID, pageable.of());
		return ResponseEntity.ok(apiResponse);
	}

	@ApiOperation(value = "작업 관련 정보 삭제 - 회원탈퇴", tags = "user server only")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", paramType = "path", example = "4d6eab0860969a50acbfa4599fbb5ae8"),
		@ApiImplicitParam(name = "serviceID", value = "요청 서버 명", paramType = "header", example = "user-server"),
		@ApiImplicitParam(name = "userUUID", value = "유저 식별자", paramType = "query", example = "498b1839dc29ed7bb2ee90ad6985c608"),
	})
	@DeleteMapping("/secession/{workspaceUUID}")
	public ResponseEntity<ApiResponse<TaskSecessionResponse>> taskSecessionRequest(
		@PathVariable("workspaceUUID") String workspaceUUID, @RequestHeader("serviceID") String requestServiceID,
		@RequestParam("userUUID") String userUUID
	) {
		if (!StringUtils.hasText(workspaceUUID) || !StringUtils.hasText(requestServiceID) || !requestServiceID.equals(
			"user-server") || !StringUtils.hasText(userUUID)) {
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		TaskSecessionResponse taskSecessionResponse = this.taskService.deleteAllTaskInfo(workspaceUUID, userUUID);
		return ResponseEntity.ok(new ApiResponse<>(taskSecessionResponse));
	}
}