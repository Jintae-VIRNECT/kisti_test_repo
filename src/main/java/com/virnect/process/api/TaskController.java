package com.virnect.process.api;

import com.virnect.process.application.TaskService;
import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.State;
import com.virnect.process.dto.request.EditProcessRequest;
import com.virnect.process.dto.request.EditSubProcessRequest;
import com.virnect.process.dto.request.ProcessRegisterRequest;
import com.virnect.process.dto.request.WorkResultSyncRequest;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-14
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@CrossOrigin
@Api(produces = MediaType.APPLICATION_JSON_VALUE, value = "공정 서버 API Controller 입니다.")
@Slf4j
@RestController
@RequestMapping("/processes")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    /**
     * 신규 할당된 하위작업 유무 조회
     *
     * @param workerUUID 사용자 식별자
     * @return
     */
    @ApiOperation(value = "신규 할당된 하위작업 유무 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "workerUUID", value = "사용자 식별자", dataType = "string", paramType = "query", required = true, example = "4ea61b4ad1dab12fb2ce8a14b02b7460")
    })
    @GetMapping("/newWork")
    public ResponseEntity<ApiResponse<RecentSubProcessResponse>> getNewWork(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID,
            @RequestParam(value = "workerUUID") String workerUUID) {
        if (workerUUID.isEmpty()) {
            log.info("[workerUUID] => [{}]", workerUUID);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<RecentSubProcessResponse> responseMessage = this.taskService.getNewWork(workspaceUUID, workerUUID);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * 컨텐츠 파일의 작업 조회
     *
     * @param contentUUID 콘텐츠 식별자
     * @return
     */
    @ApiOperation(value = "컨텐츠 파일의 작업 조회",
            notes = "컨텐츠 식별자에 해당하는 작업의 id를 가져옵니다." +
                    "\n컨텐츠가 없다면 5002 반환" +
                    "\n해당 컨텐츠로 만들어진 작업이 없다면 5003 반환" +
                    "\n해당 컨텐츠로 만들어진 작업이 있다면 작업 식별자를 반환")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentUUID", value = "콘텐츠 식별자", dataType = "string", paramType = "path", required = true, example = "fae2100d-aa26-4ff6-b5dc-ffe3d06a7df4")
    })
    @GetMapping("/content/{contentUUID}")
    public ResponseEntity<ApiResponse<ProcessIdRetrieveResponse>> getProcessIdOfContent(@PathVariable("contentUUID") String contentUUID) {
        if (contentUUID.isEmpty()) {
            log.info("[contentUUID] => [{}]", contentUUID);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ProcessIdRetrieveResponse> responseMessage = this.taskService.getProcessIdOfContent(contentUUID);
        return ResponseEntity.ok(responseMessage);
    }

    // TODO : ARUCO가 아닌 target 가져오기로 변경

    /**
     * 작업과 연계된 ContentUUID와 ARUCO 가져오기
     *
     * @param processId
     * @return
     */
    @ApiOperation(value = "작업과 연계된 ContentUUID와 작업의 target data 가져오기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId", value = "작업 식별자", dataType = "string", paramType = "path", required = true, example = "1")
    })
    @GetMapping("/process/content/{processId}")
    public ResponseEntity<ApiResponse<ProcessContentAndTargetResponse>> getRelatedInfoOfProcess(@PathVariable("processId") Long processId) {
        if (processId == null) {
            log.info("[processId] => [{}]", processId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ProcessContentAndTargetResponse> responseMessage = this.taskService.getRelatedInfoOfProcess(processId);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * 작업들의 메타데이터 가져오기
     *
     * @param processesId 작업 식별자 배열
     * @param workerUUID  사용자 식별자
     * @return
     */
    @ApiOperation(value = "작업들의 메타데이터 가져오기", notes = "뷰에서 필요한 작업들의 메타데이터를 가져온다. 요청한 사용자가 할당된 작업만 조회됨.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processesId", value = "콘텐츠 식별자 배열(ex : processesId=3,7,1,10)", allowMultiple = true, dataType = "array", paramType = "query", required = true, example = "1,3,4,5,7,14,16,17,18,19,20,21,22,23"),
            @ApiImplicitParam(name = "workerUUID", value = "사용자 식별자", dataType = "string", paramType = "query", required = true, example = "4d8d02b431ccbccbae9355324551123e")
    })
    @GetMapping("/metadata")
    public ResponseEntity<ApiResponse<ProcessMetadataResponse.ProcessesMetadata>> getMetadataTheProcess(@RequestParam(value = "processesId") Long[] processesId, @RequestParam(value = "workerUUID") String workerUUID) {
        // 작업 여러개 동시 가져오기. 단 사용자 권한이 체크되어 해당 권한의 하위작업만 가져올 수 있음.
        if (processesId.length < 1) throw new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS);
        ApiResponse<ProcessMetadataResponse.ProcessesMetadata> processMetadataResponseApiResponse = this.taskService.getMetadataTheProcess(processesId, workerUUID);
        return ResponseEntity.ok(processMetadataResponseApiResponse);
    }

    /**
     * 하위작업들의 메타데이터 가져오기
     *
     * @param subProcessesId 하위작업 식별자 배열
     * @param workerUUID     사용자 식별자
     * @return
     */
    @ApiOperation(value = "하위작업들의 메타데이터 가져오기", notes = "요청한 사용자가 할당된 작업만 조회됨.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subProcessesId", value = "하위작업 식별자 배열(ex : subProcessesId=2,4,12,45)", allowMultiple = true, dataType = "array", paramType = "query", required = true, example = "1"),
            @ApiImplicitParam(name = "workerUUID", value = "사용자 식별자", dataType = "string", paramType = "query", required = true, example = "4d8d02b431ccbccbae9355324551123e")
    })
    @GetMapping("/subProcesses/metadata")
    public ResponseEntity<ApiResponse<ProcessMetadataResponse.ProcessesMetadata>> getMetadataTheSubProcess(@RequestParam(value = "subProcessesId") Long[] subProcessesId, @RequestParam(value = "workerUUID") String workerUUID) {
        if (subProcessesId.length < 1) throw new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_SUBPROCESS);
        ApiResponse<ProcessMetadataResponse.ProcessesMetadata> processMetadataResponseApiResponse = this.taskService.getMetadataTheSubProcess(subProcessesId, workerUUID);
        return ResponseEntity.ok(processMetadataResponseApiResponse);
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

    /**
     * 해당일의 시간대별 하위작업보고수 조회
     *
     * @param targetDate 조회할 날짜
     * @param status     조회할 상태
     * @return
     */
    @ApiOperation(value = "해당일의 시간대별 하위작업보고수 조회", tags = "unused")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetDate", value = "조회할 날짜", dataType = "string", paramType = "path", required = true),
            @ApiImplicitParam(name = "status", value = "조회할 상태", dataType = "string", paramType = "query", required = false)
    })
    @GetMapping("/hourlyReports/{targetDate}")
    public ResponseEntity<ApiResponse<HourlyReportCountListResponse>> getHourlyReports(@PathVariable("targetDate") @DateTimeFormat(pattern = "yyyy-MM-dd") String targetDate, @RequestParam(required = false, value = "status") String status) {
        if (targetDate.isEmpty()) {
            log.info("[targetDate] => [{}]", targetDate);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<HourlyReportCountListResponse> apiResponse = this.taskService.getHourlyReports(targetDate, status);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "해당월의 일별 통계 조회")
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
     * 이슈 목록 조회
     *
     * @param searchType 검색분류
     * @param search     검색어
     * @param inout      작업내외 필터
     * @param pageable
     * @return
     */
    @ApiOperation(value = "이슈 목록 조회", notes = "searchType을 최우선 확인함. searchType이 NONE인 경우 검색어는 무시됨.\n정렬 컬럼명은 updated_at")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "searchType", value = "검색분류(PROCESS_NAME, SUBPROCESS_NAME, JOB_NAME, USER_NAME, NONE)", dataType = "object", paramType = "query", defaultValue = "NONE"),
            @ApiImplicitParam(name = "search", value = "검색어 - searchType이 NONE인 경우 검색어는 무시됨.", dataType = "string", paramType = "query", defaultValue = ""),
            @ApiImplicitParam(name = "inout", value = "작업내외 필터 - ALL, IN, OUT", dataType = "object", paramType = "query", defaultValue = "ALL"),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건)", dataType = "String", paramType = "query", defaultValue = "updated_at,desc")
    })
    @GetMapping("/issues")
    public ResponseEntity<ApiResponse<IssuesResponse>> getIssues(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
            , @RequestParam(value = "searchType", required = false, defaultValue = "NONE") SearchType searchType
            , @RequestParam(value = "search", required = false) String search
            , @RequestParam(value = "inout", required = false, defaultValue = "ALL") AllInOut inout
            , @ApiIgnore PageRequest pageable) {
        ApiResponse<IssuesResponse> issuesResponseApiResponse = null;
        // 검색어가 없다면 검색분류도 없는 것으로 처리.
        if (search == null || search.isEmpty()) {
            searchType = SearchType.NONE;
        }
        switch (searchType) {
            case USER_NAME:
                // 사용자명 검색시
                switch (inout) {
                    case IN:
                        // 작업내에서의 사용자명 검색시
                        issuesResponseApiResponse = this.taskService.getIssuesInSearchUserName(workspaceUUID, search, pageable.of());
                        break;
                    case OUT:
                        // 작업외에서의 사용자명 검색시
                        issuesResponseApiResponse = this.taskService.getIssuesOutSearchUserName(search, pageable.of());
                        break;
                    default:
                        // 모든 이슈의 사용자명 검색시
                        issuesResponseApiResponse = this.taskService.getIssuesAllSearchUserName(workspaceUUID, search, pageable.of());
                }
                break;
            case NONE:
                // 검색어가 비어 있을 경우
                switch (inout) {
                    case IN:
                        issuesResponseApiResponse = this.taskService.getIssuesIn(workspaceUUID, pageable.of());
                        break;
                    case OUT:
                        issuesResponseApiResponse = this.taskService.getIssuesOut(pageable.of());
                        break;
                    default:
                        issuesResponseApiResponse = this.taskService.getIssuesAll(workspaceUUID, pageable.of());
                }
                break;
            default:
                // 작업, 하위작업, 단계으로 검색시
                switch (inout) {
                    case OUT:
                        // 단계외 검색이므로 단계을 검색시 예외처리
                        throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
                    default:
                        // 단계 내에서 작업, 하위작업, 단계을 검색
                        switch (searchType) {
                            case PROCESS_NAME:
                                issuesResponseApiResponse = this.taskService.getIssuesInSearchProcessTitle(workspaceUUID, search, pageable.of());
                                break;
                            case SUBPROCESS_NAME:
                                issuesResponseApiResponse = this.taskService.getIssuesInSearchSubProcessTitle(workspaceUUID, search, pageable.of());
                                break;
                            case JOB_NAME:
                                issuesResponseApiResponse = this.taskService.getIssuesInSearchJobTitle(workspaceUUID, search, pageable.of());
                                break;
                            default:
                                throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
                        }
                }
        }

        return ResponseEntity.ok(issuesResponseApiResponse);
    }

    /**
     * 리포트 목록 조회
     *
     * @param processId    작업 식별자
     * @param subProcessId 하위작업 식별자
     * @param pageable
     * @return
     */
    @ApiOperation(value = "리포트 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "processId", value = "작업 식별자 - 작업내에서 조회", dataType = "string", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "subProcessId", value = "하위작업 식별자 - 하위작업내에서 조회", dataType = "string", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "search", value = "검색어 - searchType이 NONE인 경우 검색어는 무시됨.", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "reported", value = "보고일 필터링 여부(true, false)", dataType = "Boolean", paramType = "query", defaultValue = "false"),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건) - ex)reported_date,desc", dataType = "String", paramType = "query", defaultValue = "updated_at,desc")
    })
    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<ReportsResponse>> getReports(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
            , @RequestParam(value = "processId", required = false) Long processId
            , @RequestParam(value = "subProcessId", required = false) Long subProcessId
            , @RequestParam(value = "search", required = false) String search
            , @RequestParam(value = "reported", required = false) Boolean reported
            , @ApiIgnore PageRequest pageable) {
        ApiResponse<ReportsResponse> reportsResponseApiResponse = this.taskService.getReports(workspaceUUID, processId, subProcessId, search, reported, pageable.of());
        return ResponseEntity.ok(reportsResponseApiResponse);
    }

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
    @ApiOperation(value = "전체 작업 진행률 및 작업진행상태별 현황 조회")
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
     * 전체 작업의 목록을 조회
     *
     * @param search   검색어
     * @param pageable - 페이징 요청
     * @return
     */
    @ApiOperation(value = "전체 작업 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "search", value = "검색어 - searchType이 NONE인 경우 검색어는 무시됨.", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "filter", value = "작업상태 필터(WAIT,UNPROGRESSING,PROGRESSING,COMPLETED,INCOMPLETED,FAILED,SUCCESS,FAULT,NONE)", dataType = "object", paramType = "query", defaultValue = "PROGRESSING"),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건 - reported_date, name, start_date, end_date, state)", dataType = "String", paramType = "query", defaultValue = "updated_at,desc")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<ProcessListResponse>> getProcessList(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
            , @RequestParam(value = "search", required = false) String search
            , @RequestParam(value = "filter", required = false) List<Conditions> filter
            , @ApiIgnore PageRequest pageable) {
        ApiResponse<ProcessListResponse> processListApiResponse = this.taskService.getProcessList(workspaceUUID, search, filter, pageable.of());
        return ResponseEntity.ok(processListApiResponse);
    }

    /**
     * 작업종료
     *
     * @param processId 작업 식별자
     * @return
     */
    @ApiOperation(value = "작업종료", notes = "진행중인 작업을 종료합니다. 더이상 작업 수행을 할 수 없게 됩니다. 또한 활성화된 작업은 하나이기 때문에 작업이 종료되면 해당 컨텐츠를 삭제할 수 있습니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId", value = "작업 식별자", dataType = "string", paramType = "path", required = true, example = "1"),
            @ApiImplicitParam(name = "actorUUID", value = "행위자 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "449ae69cee53b8a6819053828c94e496")
    })
    @PutMapping("/closed/{processId}")
    public ResponseEntity<ApiResponse<ProcessInfoResponse>> setClosedProcess(@PathVariable("processId") Long processId, @RequestParam(value = "actorUUID") String actorUUID) {
        if (Objects.isNull(processId) || actorUUID.isEmpty()) {
            log.info("[processId] => [{}]", processId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ProcessInfoResponse> processInfoResponseApiResponse = this.taskService.setClosedProcess(processId, actorUUID);
        return ResponseEntity.ok(processInfoResponseApiResponse);
    }

    /**
     * 작업상세조회
     *
     * @param processId 작업 식별자
     * @return
     */
    @ApiOperation(value = "작업상세조회", notes = "작업 상세내용을 조회합니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId", value = "작업 식별자", dataType = "string", paramType = "path", required = true, example = "1")
    })
    @GetMapping("/{processId}")
    public ResponseEntity<ApiResponse<ProcessInfoResponse>> getProcessInfo(@PathVariable("processId") Long processId) {
        if (Objects.isNull(processId)) {
            log.info("[processId] => [{}]", processId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ProcessInfoResponse> processInfoResponseApiResponse = this.taskService.getProcessInfo(processId);
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
    @PostMapping("/process")
    public ResponseEntity<ApiResponse<ProcessRegisterResponse>> createProcess(@RequestBody @Valid ProcessRegisterRequest registerNewProcess, BindingResult result) {
        if (result.hasErrors()) {
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ProcessRegisterResponse> responseMessage = this.taskService.createTheProcess(registerNewProcess);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * 작업삭제
     *
     * @param processId
     * @return
     */
    @ApiOperation(value = "작업삭제", notes = "actorUUID는 작업의 contentManagerUUID와 동일해야 함.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId", value = "작업 식별자", dataType = "string", paramType = "path", required = true, example = "10"),
            @ApiImplicitParam(name = "actorUUID", value = "행위자 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "449ae69cee53b8a6819053828c94e496")
    })
    @DeleteMapping("/{processId}")
    public ResponseEntity<ApiResponse<ProcessSimpleResponse>> deleteProcessHandler(
            @PathVariable("processId") Long processId
            , @RequestParam(value = "actorUUID") String actorUUID) {
        if (Objects.isNull(processId) || actorUUID.isEmpty()) {
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ProcessSimpleResponse> processSimpleResponseApiResponse = this.taskService.deleteTheProcess(processId, actorUUID);
        return ResponseEntity.ok(processSimpleResponseApiResponse);
    }

    /**
     * 작업편집
     *
     * @param processId 작업 식별자
     * @return
     */
    @ApiOperation(value = "작업편집")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "actorUUID", value = "행위자 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "498b1839dc29ed7bb2ee90ad6985c608"),
            @ApiImplicitParam(name = "processId", value = "작업 식별자", dataType = "string", paramType = "path", required = true, example = "1")
    })
    @PostMapping("/{processId}")
    public ResponseEntity<ResponseMessage> updateProcess(@PathVariable("processId") Long processId, @RequestParam(value = "actorUUID") String actorUUID, @RequestBody @Valid EditProcessRequest editProcessRequest, BindingResult result) {
        if (actorUUID.isEmpty() || Objects.isNull(processId) || result.hasErrors()) {
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ResponseMessage responseMessage = this.taskService.updateProcess(actorUUID, editProcessRequest);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * 하위작업목록조회
     *
     * @param processId 작업 식별자
     * @return
     */
    @ApiOperation(value = "공정 내 하위작업목록조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "processId", value = "작업 식별자", dataType = "string", paramType = "path", required = true, example = "1"),
            @ApiImplicitParam(name = "search", value = "검색어 - name을 검색", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "filter", value = "작업상태 필터(WAIT,UNPROGRESSING,PROGRESSING,COMPLETED,INCOMPLETED,FAILED,SUCCESS,FAULT,NONE)", dataType = "object", paramType = "query", defaultValue = "PROGRESSING"),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건 - start_date, end_date, name, priority, reported_date)", dataType = "String", paramType = "query", defaultValue = "updated_at,desc")
    })
    @GetMapping("/subProcesses/{processId}")
    public ResponseEntity<ApiResponse<SubProcessListResponse>> getSubProcessList(
            @PathVariable("processId") Long processId
            , @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
            , @RequestParam(value = "search", required = false) String search
            , @RequestParam(value = "filter", required = false) List<Conditions> filter
            , @ApiIgnore PageRequest pageable) {
        if (Objects.isNull(processId)) {
            log.info("[processId] => [{}]", processId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<SubProcessListResponse> subProcessListResponseApiResponse = this.taskService.getSubProcessList(processId, workspaceUUID, search, filter, pageable.of());
        return ResponseEntity.ok(subProcessListResponseApiResponse);
    }

    /**
     * 워크스페이스의 전체 하위작업목록조회
     *
     * @param processId 작업 식별자
     * @return
     */
    @ApiOperation(value = "워크스페이스의 전체 하위작업목록조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "processId", value = "작업 식별자", dataType = "string", paramType = "query", example = ""),
            @ApiImplicitParam(name = "search", value = "검색어 - user, name을 검색", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건)", dataType = "String", paramType = "query", defaultValue = "updatedDate,desc")
    })
    @GetMapping("/subProcesses")
    public ResponseEntity<ApiResponse<SubProcessesResponse>> getSubProcesses(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
            , @RequestParam(required = false, value = "processId") Long processId
            , @RequestParam(value = "search", required = false) String search
            , @ApiIgnore PageRequest pageable) {
        ApiResponse<SubProcessesResponse> subProcessesResponseApiResponse = this.taskService.getSubProcesses(workspaceUUID, processId, search, pageable.of());
        return ResponseEntity.ok(subProcessesResponseApiResponse);
    }

    /**
     * 하위작업 상세조회
     *
     * @param subProcessId 하위작업 식별자
     * @return
     */
    @ApiOperation(value = "하위작업 상세조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subProcessId", value = "하위작업 식별자", dataType = "string", paramType = "path", required = true, example = "1"),
    })
    @GetMapping("/subProcess/{subProcessId}")
    public ResponseEntity<ApiResponse<SubProcessInfoResponse>> getSubProcess(@PathVariable("subProcessId") Long subProcessId) {
        ApiResponse<SubProcessInfoResponse> subProcessesResponseApiResponse = this.taskService.getSubProcess(subProcessId);
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
            @ApiImplicitParam(name = "processId", value = "작업 식별자", dataType = "string", paramType = "query", defaultValue = ""),
            @ApiImplicitParam(name = "search", value = "검색어(콘텐츠명/사용자명) - 미개발", dataType = "string", defaultValue = ""),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건)", dataType = "String", paramType = "query", defaultValue = "updated_at,desc")
    })
    @GetMapping("/myWorks/{workerUUID}")
    public ResponseEntity<ApiResponse<MyWorkListResponse>> getMyWorks(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
            , @PathVariable("workerUUID") String workerUUID
            , @RequestParam(required = false, value = "processId") Long processId
            , @RequestParam(required = false, value = "search") String search
            , @ApiIgnore PageRequest pageable) {
        if (workerUUID.isEmpty()) {
            log.info("[workerUUID] => [{}]", workerUUID);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<MyWorkListResponse> myWorkListResponseApiResponse = this.taskService.getMyWorks(workspaceUUID, workerUUID, processId, search, pageable.of());
        return ResponseEntity.ok(myWorkListResponseApiResponse);
    }

    @ApiOperation(value = "타겟 데이터의 하위작업 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "targetData", value = "타겟 데이터 식별자", dataType = "string", required = true, paramType = "path", defaultValue = ""),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건)", dataType = "String", paramType = "query", defaultValue = "updated_at,desc")
    })
    @GetMapping("/target/{targetData}")
    public ResponseEntity<ApiResponse<SubProcessesOfTargetResponse>> getSubProcessesOfTarget(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
            , @PathVariable("targetData") String targetData
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
     * @param subProcessId
     * @return
     */
    @ApiOperation(value = "하위작업편집")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subProcessId", value = "하위작업 식별자", dataType = "string", paramType = "path", required = true, example = "1"),
    })
    @PostMapping("/subProcesses/{subProcessId}")
    public ResponseEntity<ResponseMessage> updateSubProcess(@PathVariable("subProcessId") Long subProcessId, @RequestBody EditSubProcessRequest subProcessRequest) {
        if (subProcessId == null) {
            log.info("[subProcessId] => [{}]", subProcessId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ResponseMessage responseMessage = this.taskService.updateSubProcess(subProcessId, subProcessRequest);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * 단계목록조회
     *
     * @param subProcessId
     * @param pageable
     * @return
     */
    @ApiOperation(value = "단계목록조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subProcessId", value = "하위작업 식별자", dataType = "string", paramType = "path", required = true, example = "1"),
            @ApiImplicitParam(name = "search", value = "검색어 - name을 검색", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "filter", value = "작업상태 필터(WAIT,UNPROGRESSING,PROGRESSING,COMPLETED,INCOMPLETED,FAILED,SUCCESS,FAULT,NONE)", dataType = "object", paramType = "query", defaultValue = "PROGRESSING"),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건- priority, name, result, is_reported)", dataType = "String", paramType = "query", defaultValue = "updated_at,desc")
    })
    @GetMapping("/subProcesses/jobs/{subProcessId}")
    public ResponseEntity<ApiResponse<JobListResponse>> getJobs(@PathVariable("subProcessId") Long subProcessId, @RequestParam(value = "search", required = false) String search, @RequestParam(value = "filter", required = false) List<Conditions> filter, @ApiIgnore PageRequest pageable) {
        if (subProcessId == null) {
            log.info("[subProcessId] => [{}]", subProcessId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<JobListResponse> jobListResponseApiResponse = this.taskService.getJobs(subProcessId, search, filter, pageable.of());
        return ResponseEntity.ok(jobListResponseApiResponse);
    }

    /**
     * 이슈상세조회
     *
     * @param issueId
     * @return
     */
    @ApiOperation(value = "이슈상세조회")
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
     * 리포트상세조회
     *
     * @param reportId
     * @return
     */
    @ApiOperation(value = "리포트상세조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reportId", value = "report 식별자", dataType = "string", paramType = "path", required = true, example = "1")
    })
    @GetMapping("/report/{reportId}")
    public ResponseEntity<ApiResponse<ReportInfoResponse>> getReportInfo(@PathVariable("reportId") Long reportId) {
        if (Objects.isNull(reportId)) {
            log.info("[reportId] => [{}]", reportId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ReportInfoResponse> reportInfoResponseApiResponse = this.taskService.getReportInfo(reportId);
        return ResponseEntity.ok(reportInfoResponseApiResponse);
    }

    @ApiOperation(value = "타겟 데이터 값으로 활성화된(State.CREATED) 작업 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetData", value = "작업에 할당된 targetData 값", paramType = "path", required = true, example = "1")
    })
    @GetMapping("/created/target/{targetData}")
    public ResponseEntity<ApiResponse<ProcessInfoResponse>> getProcessInfoByTargetValue(@PathVariable("targetData") String targetData) {
        if (targetData.isEmpty()) {
            throw new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS);
        }
        ApiResponse<ProcessInfoResponse> responseMessage = this.taskService.getProcessInfoByTarget(targetData);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "사용자에게 할당된 하위작업수 및 진행중인 하위작업수 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "workerUUID", value = "사용자 uuid", paramType = "path", required = true, example = "449ae69cee53b8a6819053828c94e496")
    })
    @GetMapping("/subProcesses/count/onWorker/{workerUUID}")
    public ResponseEntity<ApiResponse<CountSubProcessOnWorkerResponse>> getCountSubProcessOnWorker(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
            , @PathVariable("workerUUID") String workerUUID) {
        if (workerUUID.isEmpty()) {
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<CountSubProcessOnWorkerResponse> apiResponse = this.taskService.getCountSubProcessOnWorker(workspaceUUID, workerUUID);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "작업을 컨텐츠로의 전환", tags = "next", notes = "작업을 삭제 후 컨텐츠로 전환함.\n전환 조건은 서버에서 체크하지 않음. 단, 동일 담당자 확인만 함.\n내부적으로는 컨텐츠 공정 모두 남아있지만, 삭제, 전환 flag로 노출되지 않도록 구현했음.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId", value = "작업 식별자", dataType = "string", paramType = "path", required = true, defaultValue = ""),
            @ApiImplicitParam(name = "workerUUID", value = "담당자 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "449ae69cee53b8a6819053828c94e496"),
            @ApiImplicitParam(name = "state", value = "컨텐츠 전환시 변경할 작업의 상태(CLOSED, DELETED)", dataType = "string", paramType = "query", required = true, defaultValue = "CLOSED")
    })
    @PutMapping("/convert/{processId}")
    public ResponseEntity<ApiResponse<ProcessInfoResponse>> processConvertHandler(
            @PathVariable("processId") Long processId
            , @PathVariable("workerUUID") String workerUUID
            , @PathVariable("state") State state
    ) {
        if (processId == null || workerUUID.isEmpty() || state == null) {
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ProcessInfoResponse> apiResponse = this.taskService.convertProcess(processId, workerUUID, state);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/healthcheck")
    public ResponseEntity<String> checkHealth() {
        return ResponseEntity.ok("200 OK");
    }
}