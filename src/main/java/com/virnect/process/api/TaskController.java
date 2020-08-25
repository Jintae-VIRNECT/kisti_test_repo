package com.virnect.process.api;

import com.virnect.process.application.TaskService;
import com.virnect.process.domain.Conditions;
import com.virnect.process.dto.request.*;
import com.virnect.process.dto.response.*;
import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.common.ApiResponse;
import com.virnect.process.global.common.PageRequest;
import com.virnect.process.global.common.ResponseMessage;
import com.virnect.process.global.error.ErrorCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
    public ResponseEntity<ApiResponse<ProcessContentAndTargetResponse>> getRelatedInfoOfProcess(@PathVariable("taskId") Long taskId) {
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
            @RequestBody @Valid WorkResultSyncRequest workResultSyncRequest, BindingResult result) {
        if (result.hasErrors()) {
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        log.info("Request Data: {}", workResultSyncRequest.toString());
        ApiResponse<WorkResultSyncResponse> responseMessage = this.taskService.uploadOrSyncWorkResult(workResultSyncRequest);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "트러블메모 업로드", tags = "Issues")
    @PostMapping("/troubleMemo/upload")
    public ResponseEntity<ApiResponse<TroubleMemoUploadResponse>> setTroubleMemo(
            @RequestBody @Valid TroubleMemoUploadRequest troubleMemoUploadRequest, BindingResult result
    ) {
        if (result.hasErrors()) {
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<TroubleMemoUploadResponse> responseMessage = this.taskService.uploadTroubleMemo(troubleMemoUploadRequest);

        return ResponseEntity.ok(responseMessage);
    }

    /**
     *  ISSUES API Part
     */

    /**
     * 이슈 목록 조회 (이전 /tasks/issues?inout=IN)
     *
     * @param workspaceUUID
     * @param search
     * @param myUUID
     * @param pageable
     * @return
     */
    @ApiOperation(value = "이슈 목록 조회", tags = "Issues", notes = "searchType 없앰. 정렬 컬럼명은 updatedDate")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "search", value = "검색어(사용자 이메일, 닉네임, 작업명, 하위 작업명, 이슈 내용, 단계명)", dataType = "string", paramType = "query", defaultValue = ""),
            @ApiImplicitParam(name = "myUUID", value = "사용자 식별자 (내 이슈용)", dataType = "string", paramType = "query", defaultValue = ""),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건)", dataType = "String", paramType = "query", defaultValue = "updatedDate,desc"),
            @ApiImplicitParam(name = "stepId", value = "스텝ID", dataType = "string", paramType = "query")
    })
    @GetMapping("/issues")
    public ResponseEntity<ApiResponse<IssuesResponse>> getIssues(
            @RequestParam(value = "workspaceUUID", required = false, defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8") String workspaceUUID
            , @RequestParam(value = "search", required = false) String search
            , @RequestParam(value = "myUUID", required = false) String myUUID
            , @RequestParam(value = "stepId", required = false) Long stepId
            , @ApiIgnore PageRequest pageable) {
        ApiResponse<IssuesResponse> issuesResponseApiResponse = this.taskService.getIssuesIn(myUUID, workspaceUUID, search, stepId, pageable.of());
        // 검색어가 없다면 검색분류도 없는 것으로 처리.
//        if (search == null || search.isEmpty()) {
//            searchType = SearchType.NONE;
//        }
//        switch (searchType) {
//            // 사용자명 검색시
//            case USER_NAME:
//                // 작업내에서의 사용자명 검색시
//                issuesResponseApiResponse = this.taskService.getIssuesInSearchUserName(null, workspaceUUID, search, pageable.of());
//                break;
//            // 검색어가 비어 있을 경우
//            case NONE:
//                issuesResponseApiResponse = this.taskService.getIssuesIn(null, workspaceUUID, pageable.of());
//                break;
//            // 작업, 하위작업, 단계으로 검색시
//            default:
//                // 단계 내에서 작업, 하위작업, 단계을 검색
//                switch (searchType) {
//                    // 작업으로 검색시
//                    case TASK_NAME:
//                        issuesResponseApiResponse = this.taskService.getIssuesInSearchProcessTitle(workspaceUUID, search, pageable.of());
//                        break;
//                    // 하위 작업으로 검색시
//                    case SUBTASK_NAME:
//                        issuesResponseApiResponse = this.taskService.getIssuesInSearchSubProcessTitle(workspaceUUID, search, pageable.of());
//                        break;
//                    // 단계로 검색시
//                    case STEP_NAME:
//                        issuesResponseApiResponse = this.taskService.getIssuesInSearchJobTitle(workspaceUUID, search, pageable.of());
//                        break;
//                    default:
//                        throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
//                }
//        }

        return ResponseEntity.ok(issuesResponseApiResponse);
    }

    /**
     * 이슈상세조회
     *
     * @param issueId
     * @return
     */
    @ApiOperation(value = "이슈상세조회", tags = "Issues")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "issueId", value = "issue 식별자", dataType = "string", paramType = "path", required = true, example = "1")
    })
    @GetMapping("/issue/{issueId}")
    public ResponseEntity<ApiResponse<IssueInfoResponse>> getIssueInfo(@PathVariable("issueId") Long issueId) {
        if (Objects.isNull(issueId)) {
            log.info("[issueId] => [{}]", issueId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<IssueInfoResponse> issueInfoResponseApiResponse = this.taskService.getIssueInfo(issueId);
        return ResponseEntity.ok(issueInfoResponseApiResponse);
    }

    /**
     * 트러블 메모 목록 조회 (이전 /tasks/issues?inout=OUT)
     *
     * @param workspaceUUID
     * @param search
     * @param myUUID
     * @param pageable
     * @return
     */
    @ApiOperation(value = "트러블 메모 목록 조회", tags = "Issues", notes = "searchType을 최우선 확인함. searchType이 NONE인 경우 검색어는 무시됨.\n정렬 컬럼명은 updatedDate.\n 특정 작업 등에 얽매이지 않는다. 현재는 JOB_ID가 null인 것을 리턴.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "search", value = "검색어 - searchType이 NONE인 경우 검색어는 무시됨.", dataType = "string", paramType = "query", defaultValue = ""),
            @ApiImplicitParam(name = "myUUID", value = "사용자 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건)", dataType = "String", paramType = "query", defaultValue = "updatedDate,desc")
    })
    @GetMapping("/troubleMemos")
    public ResponseEntity<ApiResponse<IssuesResponse>> getTroubleMemo(
            @RequestParam(value = "workspaceUUID", required = true, defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8") String workspaceUUID
            , @RequestParam(value = "search", required = false) String search
            , @RequestParam(value = "myUUID", required = false) String myUUID
            , @ApiIgnore PageRequest pageable) {
        if (Objects.isNull(workspaceUUID)) {
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<IssuesResponse> issuesResponseApiResponse = this.taskService.getIssuesOut(myUUID, workspaceUUID, search, pageable.of());

        return ResponseEntity.ok(issuesResponseApiResponse);
    }

    /**
     * 트러블 메모 상세 조회
     *
     * @param troubleMemoId
     * @return
     */
    @ApiOperation(value = "트러블 메모 상세 조회", tags = "Issues")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "troubleMemoId", value = "troubleMemo 식별자", dataType = "string", paramType = "path", required = true, example = "1")
    })
    @GetMapping("/troubleMemo/{troubleMemoId}")
    public ResponseEntity<ApiResponse<IssueInfoResponse>> getTroubleMemoInfo(@PathVariable("troubleMemoId") Long troubleMemoId) {
        if (Objects.isNull(troubleMemoId)) {
            log.info("[troubleMemoId] => [{}]", troubleMemoId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<IssueInfoResponse> issueInfoResponseApiResponse = this.taskService.getIssueInfo(troubleMemoId);
        return ResponseEntity.ok(issueInfoResponseApiResponse);
    }

    /**
     * PAPERS API Part
     */

    /**
     * 페이퍼 목록 조회 (이전 리포트 목록 조회)
     *
     * @param taskId    작업 식별자
     * @param subTaskId 하위작업 식별자
     * @param pageable
     * @return
     */
    @ApiOperation(value = "페이퍼 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "taskId", value = "작업 식별자 - 작업내에서 조회", dataType = "string", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "subTaskId", value = "하위작업 식별자 - 하위작업내에서 조회", dataType = "string", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "search", value = "검색어 - 사용자 이메일, 사용자 닉네임.", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "reported", value = "보고일 필터링 여부(true, false)", dataType = "Boolean", paramType = "query", defaultValue = "false"),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건) - ex)reported_date,desc", dataType = "String", paramType = "query", defaultValue = "updatedDate,desc"),
            @ApiImplicitParam(name = "myUUID", value = "사용자 식별자 (내 페이퍼 보기용)", dataType = "String", paramType = "query")
    })
    @GetMapping("/papers")
    public ResponseEntity<ApiResponse<ReportsResponse>> getPapers(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
            , @RequestParam(value = "taskId", required = false) Long taskId
            , @RequestParam(value = "subTaskId", required = false) Long subTaskId
            , @RequestParam(value = "search", required = false) String search
            , @RequestParam(value = "reported", required = false) Boolean reported
            , @RequestParam(value = "myUUID", required = false) String myUUID
            , @ApiIgnore PageRequest pageable) {
        ApiResponse<ReportsResponse> reportsResponseApiResponse = this.taskService.getReports(myUUID, workspaceUUID, taskId, subTaskId, search, reported, pageable.of());
        return ResponseEntity.ok(reportsResponseApiResponse);
    }


    /**
     * 페이퍼 상세조회 (구 리포트 상세조회)
     *
     * @param paperId
     * @return
     */
    @ApiOperation(value = "페이퍼 상세조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paperId", value = "paper 식별자", dataType = "string", paramType = "path", required = true, example = "1")
    })
    @GetMapping("/paper/{paperId}")
    public ResponseEntity<ApiResponse<ReportInfoResponse>> getReportInfo(@PathVariable("paperId") Long paperId) {
        if (Objects.isNull(paperId)) {
            log.info("[paperId] => [{}]", paperId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ReportInfoResponse> reportInfoResponseApiResponse = this.taskService.getReportInfo(paperId);
        return ResponseEntity.ok(reportInfoResponseApiResponse);
    }

    /**
     * Statics API Part
     */

    /**
     * 전체 작업 진행률 조회
     *
     * @return
     */
    @ApiOperation(value = "전체 작업 진행률 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query")
    })
    @GetMapping("/totalRate")
    public ResponseEntity<ResponseMessage> getTotalRate(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID) {
        ResponseMessage responseMessage = this.taskService.getTotalRate(workspaceUUID);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * 전체 작업 진행률 및 작업진행상태별 현황 조회
     *
     * @return
     */
    @ApiOperation(value = "전체 작업 진행률 및 작업진행상태별 현황 조회", tags = "Statistics")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query")
    })
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<ProcessesStatisticsResponse>> getStatistics(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID) {
        ApiResponse<ProcessesStatisticsResponse> processesStatisticsResponseApiResponse = this.taskService.getStatistics(workspaceUUID);
        return ResponseEntity.ok(processesStatisticsResponseApiResponse);
    }

    /**
     * 해당 월의 일별 통계 조회
     *
     * @param workspaceUUID
     * @param month
     * @return
     */
    @ApiOperation(value = "해당월의 일별 통계 조회", tags = "Statistics")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "month", value = "연월(YYYY-MM)", dataType = "string", paramType = "query", required = true, example = "2020-03")
    })
    @GetMapping("/dailyTotalRateAtMonth")
    public ResponseEntity<ApiResponse<MonthlyStatisticsResponse>> getDailyTotalRateAtMonth(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
            , @RequestParam("month") String month) {
        if (month.isEmpty()) {
            log.info("[workspaceUUID] => [{}], [month] => [{}]", workspaceUUID, month);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<MonthlyStatisticsResponse> dailyTotalApiResponse = this.taskService.getDailyTotalRateAtMonth(workspaceUUID, month);
        return ResponseEntity.ok(dailyTotalApiResponse);
    }

    /**
     * Process API Part
     */

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
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건 - reportedDate, name, startDate, endDate, state)", dataType = "String", paramType = "query", defaultValue = "createdDate,desc")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<ProcessListResponse>> getProcessList(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
            , @RequestParam(value = "search", required = false) String search
            , @RequestParam(value = "filter", required = false) List<Conditions> filter
            , @RequestParam(value = "myUUID", required = false) String myUUID
            , @ApiIgnore PageRequest pageable) {
        ApiResponse<ProcessListResponse> processListApiResponse = this.taskService.getProcessList(myUUID, workspaceUUID, search, filter, pageable.of());
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
            BindingResult result) {
        if (result.hasErrors()) {
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ProcessInfoResponse> processInfoResponseApiResponse = this.taskService.setClosedProcess(taskId, checkProcessOwnerRequest);
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
    public ResponseEntity<ApiResponse<ProcessRegisterResponse>> createProcess(@RequestBody @Valid ProcessRegisterRequest registerNewProcess, BindingResult result) {
        if (result.hasErrors()) {
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
    public ResponseEntity<ApiResponse<ProcessRegisterResponse>> duplicateProcess(@RequestBody @Valid ProcessDuplicateRequest duplicateRequest, BindingResult result) {
        if (result.hasErrors()) {
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
            @RequestBody @Valid CheckProcessOwnerRequest checkProcessOwnerRequest, BindingResult result) {
        if (result.hasErrors()) {
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ProcessSimpleResponse> processSimpleResponseApiResponse = this.taskService.deleteTheProcess(checkProcessOwnerRequest);
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
            , @RequestBody @Valid EditProcessRequest editTaskRequest, BindingResult result) {
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

    /**
     * SubTask API Part
     */

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
            , @ApiIgnore PageRequest pageable) {
        if (Objects.isNull(taskId)) {
            log.info("[taskId] => [{}]", taskId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<SubProcessListResponse> subProcessListResponseApiResponse = this.taskService.getSubProcessList(taskId, workspaceUUID, search, userUUID, filter, pageable.of());
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
            @ApiImplicitParam(name = "filter", value = "필터명", dataType = "string", paramType = "query", defaultValue = "ALL"),
    })
    @GetMapping("/subTasks")
    public ResponseEntity<ApiResponse<SubProcessesResponse>> getSubProcesses(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
            , @RequestParam(required = false, value = "taskId") Long taskId
            , @RequestParam(value = "search", required = false) String search
            , @RequestParam(value = "filter", required = false) Conditions filter
            , @ApiIgnore PageRequest pageable) {
        ApiResponse<SubProcessesResponse> subProcessesResponseApiResponse = this.taskService.getSubProcesses(workspaceUUID, taskId, search, pageable.of(), filter);
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
    public ResponseEntity<ApiResponse<SubProcessInfoResponse>> getSubProcess(@PathVariable("subTaskId") Long subTaskId) {
        ApiResponse<SubProcessInfoResponse> subProcessesResponseApiResponse = this.taskService.getSubProcess(subTaskId);
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
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건)", dataType = "String", paramType = "query", defaultValue = "updatedDate,desc")
    })
    @GetMapping("/myWorks/{workerUUID}")
    public ResponseEntity<ApiResponse<MyWorkListResponse>> getMyWorks(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
            , @PathVariable("workerUUID") String workerUUID
            , @RequestParam(required = false, value = "taskId") Long taskId
            , @RequestParam(required = false, value = "search") String search
            , @ApiIgnore PageRequest pageable) {
        if (workerUUID.isEmpty()) {
            log.info("[workerUUID] => [{}]", workerUUID);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<MyWorkListResponse> myWorkListResponseApiResponse = this.taskService.getMyWorks(workspaceUUID, workerUUID, taskId, search, pageable.of());
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
            , @ApiIgnore PageRequest pageable) {
        if (targetData.isEmpty()) {
            log.info("[targetData] => [{}]", targetData);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<SubProcessesOfTargetResponse> subProcessesOfTargetResponseApiResponse = this.taskService.getSubProcessesOfTarget(workspaceUUID, targetData, pageable.of());
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
    public ResponseEntity<ResponseMessage> updateSubProcess(@PathVariable("subTaskId") Long subTaskId
            , @RequestBody EditSubProcessRequest subProcessRequest) {
        if (subTaskId == null) {
            log.info("[subTaskId] => [{}]", subTaskId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ResponseMessage responseMessage = this.taskService.updateSubProcess(subTaskId, subProcessRequest);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * Step API Part
     */

    /**
     * 단계 목록 조회
     *
     * @param subTaskId
     * @param pageable
     * @return
     */
    @ApiOperation(value = "단계목록조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subTaskId", value = "하위작업 식별자", dataType = "string", paramType = "path", required = true, example = "1"),
            @ApiImplicitParam(name = "search", value = "검색어 - name을 검색", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "filter", value = "작업상태 필터(WAIT,UNPROGRESSING,PROGRESSING,COMPLETED,INCOMPLETED,FAILED,SUCCESS,FAULT,NONE,ALL)", dataType = "object", paramType = "query", defaultValue = "PROGRESSING"),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건- priority, name, result, isReported)", dataType = "String", paramType = "query", defaultValue = "updatedDate,desc"),
            @ApiImplicitParam(name = "myUUID", value = "사용자 식별자(내 단계용)", dataType = "string", paramType = "query")
    })
    @GetMapping("/subTasks/{subTaskId}/steps")
    public ResponseEntity<ApiResponse<JobListResponse>> getJobs(
            @PathVariable("subTaskId") Long subTaskId
            , @RequestParam(value = "search", required = false) String search
            , @RequestParam(value = "filter", required = false) List<Conditions> filter
            , @RequestParam(value = "myUUID", required = false) String myUUID
            , @ApiIgnore PageRequest pageable) {
        if (subTaskId == null) {
            log.info("[subTaskId] => [{}]", subTaskId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<JobListResponse> jobListResponseApiResponse = this.taskService.getJobs(myUUID, subTaskId, search, filter, pageable.of());
        return ResponseEntity.ok(jobListResponseApiResponse);
    }

    @ApiOperation(value = "워크스페이스 내 사용자 정보", tags = "next", notes = "정렬 아직 안됨")
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
        ApiResponse<WorkspaceUserListResponse> apiResponse = this.taskService.getWorkspaceUserInfo(workspaceUUID, pageable.of());
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "콘텐츠UUID로 다운로드", tags = "Download")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentUUID", value = "콘텐츠 식별자", dataType = "string", paramType = "path", required = true, defaultValue = "e1bd3914-2b69-475f-9f9d-117477dfae05"),
            @ApiImplicitParam(name = "memberUUID", value = "사용자 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "498b1839dc29ed7bb2ee90ad6985c608")
    })
    @GetMapping("/download/contentUUID/{contentUUID}")
    public ResponseEntity<byte[]> contentDownloadForUUIDRequestHandler(
            @PathVariable("contentUUID") String contentUUID
            , @RequestParam("memberUUID") String memberUUID
    ) throws IOException {
        if (contentUUID.isEmpty() || memberUUID.isEmpty()) {
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        log.info("[DOWNLOAD] USER: [{}] => contentUUID: [{}]", memberUUID, contentUUID);

        return this.taskService.contentDownloadForUUIDHandler(contentUUID, memberUUID);
    }

    @ApiOperation(value = "타겟 데이터로 다운로드", tags = "Download")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetData", value = "타겟 데이터", dataType = "string", paramType = "query", required = true, defaultValue = "a73cca48-dc17-4de8-a1aa-c19316cf773b"),
            @ApiImplicitParam(name = "memberUUID", value = "사용자 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "498b1839dc29ed7bb2ee90ad6985c608")
    })
    @GetMapping("/download")
    public ResponseEntity<byte[]> contentDownloadForTargetHandler(
            @RequestParam("targetData") String targetData
            , @RequestParam("memberUUID") String memberUUID
    ) throws IOException {
        if (targetData.isEmpty() || memberUUID.isEmpty()) {
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        log.info("[DOWNLOAD] USER: [{}] => targetData: [{}]", memberUUID, targetData);

        return this.taskService.contentDownloadForTargetHandler(targetData, memberUUID);
    }

}