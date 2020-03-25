package com.virnect.process.api;

import com.virnect.process.application.ProcessService;
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
import org.springframework.format.annotation.DateTimeFormat;
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
 * Project: SMIC_CUSTOM
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
public class ProcessController {
    private final String EXAMPLE_METADATA = "{\"aruco\":\"0\",\"contents\":[{\"id\":\"0\",\"name\":\"0\",\"managerUUID\":\"0\",\"subProcessTotal\":\"0\",\"sceneGroups\":[{\"id\":\"0\",\"priority\":\"0\",\"name\":\"0\",\"jobTotal\":\"0\",\"scenes\":[{\"id\":\"0\",\"priority\":\"0\",\"name\":\"0\",\"subJobTotal\":\"0\",\"reportObjects\":[{\"id\":\"0\",\"items\":[{\"id\":\"0\",\"priority\":\"0\",\"type\":\"0\",\"title\":\"0\"}]}],\"smartToolObjects\":[{\"id\":\"0\",\"jobId\":\"0\",\"normalTorque\":\"0\",\"items\":[{\"id\":\"0\",\"batchCount\":\"0\"}]}]}]}]}]}";
    private final ProcessService processService;

    /**
     * ARUCO 발급
     *
     * @return - aruco정보(aruco_id)
     */
    @ApiOperation(value = "ARUCO 발급", notes = "by 민항기\nARUCO 발급과 함께 컨텐츠 식별자도 함께 발급됩니다.\n발급된 ARUCO와 컨텐츠 식별자는 MARS파일과 메타데이터에 삽입됩니다.")
    @ApiImplicitParams({
    })
    @GetMapping("/aruco")
    public ResponseEntity<ApiResponse<ArucoWithContentUUIDResponse>> getAruco() {
        // TODO : 2020.02.06 hkmin - aruco가 더이상 없을 때의 예외처리
        ApiResponse<ArucoWithContentUUIDResponse> responseMessage = this.processService.getAruco();
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * ARUCO 회수
     *
     * @param deallocateRequest
     * @param result
     * @return
     */
    @ApiOperation(value = "ARUCO 회수", notes = "by 민항기\n컨텐츠가 삭제될 때 컨텐츠 식별자로 ARUCO의 삭제를 요청합니다.\n에러코드 200 : 회수가 정상처리됨.\n에러코드 5002 : ARUCO가 존재하지 않습니다.(입력된 컨텐츠 UUID로 등록된 ARUCO가 없습니다.)\n에러코드 5003 : 해당 컨텐츠 식별자는 아직 공정으로 생성되지 않았거나, 공정이 삭제되어 변환된 공정이 없습니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentUUID", value = "컨텐츠 식별자", dataType = "string", paramType = "form", required = true, example = "fae2100d-aa26-4ff6-b5dc-ffe3d06a7df4")
    })
    @PostMapping("/aruco")
    public ResponseEntity<ApiResponse<ArucoDeallocateResponse>> clearArucoRelation(@RequestBody ArucoDeallocateRequest deallocateRequest, BindingResult result) {
        if (result.hasErrors() || deallocateRequest.getContentUUID().isEmpty()) {
            log.info("[contentUUID] => [{}]", deallocateRequest.getContentUUID());
            log.error("[FIELD ERROR] => [{}] [{}]", result.getFieldError().getField(), result.getFieldError().getDefaultMessage());
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ArucoDeallocateResponse> responseMessage = this.processService.emptyAruco(deallocateRequest);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * 신규 할당된 세부공정 유무 조회
     *
     * @param workerUUID 작업자 식별자
     * @return By 장정현
     */
    @ApiOperation(value = "신규 할당된 세부공정 유무 조회", notes = "by 장정현")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workerUUID", value = "작업자 식별자", dataType = "string", paramType = "query", required = true, example = "4ea61b4ad1dab12fb2ce8a14b02b7460")
    })
    @GetMapping("/newWork")
    public ResponseEntity<ApiResponse<RecentSubProcessResponse>> getNewWork(@RequestParam(value = "workerUUID") String workerUUID) {
        if (workerUUID.isEmpty()) {
            log.info("[workerUUID] => [{}]", workerUUID);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<RecentSubProcessResponse> responseMessage = this.processService.getNewWork(workerUUID);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * 컨텐츠 파일의 공정 조회
     *
     * @param contentUUID 콘텐츠 식별자
     * @return
     */
    @ApiOperation(value = "컨텐츠 파일의 공정 조회",
            notes = "by 민항기\n컨텐츠 식별자를 통해 변환된 공정의 id를 가져옵니다.\n메이크에서 MARS파일을 수정하기 위해 open시 본 API로 공정으로 변환되었는지 확인 후 공정이 있다면 다른 이름으로 저장하도록 유도합니다.(메이크 협의 필요)" +
                    "\nARUCO가 없다면(컨텐츠가 없다면) 5002 반환" +
                    "\n해당 컨텐츠로 만들어진 공정이 없다면 5003 반환" +
                    "\n해당 컨텐츠로 만들어진 공정이 있다면 공정 식별자를 반환")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentUUID", value = "콘텐츠 식별자", dataType = "string", paramType = "path", required = true, example = "fae2100d-aa26-4ff6-b5dc-ffe3d06a7df4")
    })
    @GetMapping("/content/{contentUUID}")
    public ResponseEntity<ApiResponse<ProcessIdRetrieveResponse>> getProcessIdOfContent(@PathVariable("contentUUID") String contentUUID) {
        if (contentUUID.isEmpty()) {
            log.info("[contentUUID] => [{}]", contentUUID);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ProcessIdRetrieveResponse> responseMessage = this.processService.getProcessIdOfContent(contentUUID);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * 공정과 연계된 ContentUUID와 ARUCO 가져오기
     *
     * @param processId
     * @return
     */
    @ApiOperation(value = "공정과 연계된 ContentUUID와 ARUCO 가져오기", notes = "by 민항기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId", value = "공정 식별자", dataType = "string", paramType = "path", required = true, example = "1")
    })
    @GetMapping("/process/content/{processId}")
    public ResponseEntity<ApiResponse<ArucoWithContentUUIDResponse>> getRelatedInfoOfProcess(@PathVariable("processId") Long processId) {
        if (processId == null) {
            log.info("[processId] => [{}]", processId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ArucoWithContentUUIDResponse> responseMessage = this.processService.getRelatedInfoOfProcess(processId);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * 공정들의 메타데이터 가져오기
     *
     * @param processesId 공정 식별자 배열
     * @param workerUUID  작업자 식별자
     * @return By 민항기
     */
    @ApiOperation(value = "공정들의 메타데이터 가져오기", notes = "by 민항기\n뷰에서 필요한 공정들의 메타데이터를 가져온다. 다수 작업자가 동시 또는 개별 작업시 작업자별 작업이 저장됨.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processesId", value = "콘텐츠 식별자 배열(ex : processesId=3,7,1,10)", allowMultiple = true, dataType = "array", paramType = "query", required = true, example = "1,3,4,5,7,14,16,17,18,19,20,21,22,23"),
            @ApiImplicitParam(name = "workerUUID", value = "작업자 식별자", dataType = "string", paramType = "query", required = true, example = "4d8d02b431ccbccbae9355324551123e")
    })
    @GetMapping("/metadata")
    public ResponseEntity<ApiResponse<ProcessMetadataResponse.ProcessesMetadata>> getMetadataTheProcess(@RequestParam(value = "processesId") Long[] processesId, @RequestParam(value = "workerUUID") String workerUUID) {
        // 공정 여러개 동시 가져오기. 단 사용자 권한이 체크되어 해당 권한의 세부공정만 가져올 수 있음.
        if (processesId.length < 1) throw new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS);
        ApiResponse<ProcessMetadataResponse.ProcessesMetadata> processMetadataResponseApiResponse = this.processService.getMetadataTheProcess(processesId, workerUUID);
        return ResponseEntity.ok(processMetadataResponseApiResponse);
    }

    /**
     * 세부공정들의 메타데이터 가져오기
     *
     * @param subProcessesId 세부공정 식별자 배열
     * @param workerUUID     작업자 식별자
     * @return By 민항기
     */
    @ApiOperation(value = "세부공정들의 메타데이터 가져오기", notes = "by 민항기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subProcessesId", value = "세부공정 식별자 배열(ex : subProcessesId=2,4,12,45)", allowMultiple = true, dataType = "array", paramType = "query", required = true, example = "1"),
            @ApiImplicitParam(name = "workerUUID", value = "작업자 식별자", dataType = "string", paramType = "query", required = true, example = "4d8d02b431ccbccbae9355324551123e")
    })
    @GetMapping("/subProcesses/metadata")
    public ResponseEntity<ApiResponse<ProcessMetadataResponse.ProcessesMetadata>> getMetadataTheSubProcess(@RequestParam(value = "subProcessesId") Long[] subProcessesId, @RequestParam(value = "workerUUID") String workerUUID) {
        if (subProcessesId.length < 1) throw new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_SUBPROCESS);
        ApiResponse<ProcessMetadataResponse.ProcessesMetadata> processMetadataResponseApiResponse = this.processService.getMetadataTheSubProcess(subProcessesId, workerUUID);
        return ResponseEntity.ok(processMetadataResponseApiResponse);
    }

    /**
     * 업무결과 업로드(동기화)
     *
     * @param workResultSyncRequest - 업로드 요청 업무 결과 데이터
     * @param result                - 업로드 동기화 여부
     * @return By 장정현
     */
    @ApiOperation(value = "업무결과 업로드(동기화)", notes = "by 장정현")
    @PostMapping("/subProcesses/subProcess/work")
    public ResponseEntity<ApiResponse<WorkResultSyncResponse>> setWorkResult(@RequestBody @Valid WorkResultSyncRequest workResultSyncRequest, BindingResult result) {
        if (result.hasErrors()) {
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        log.info("Request Data: {}", workResultSyncRequest.toString());
        ApiResponse<WorkResultSyncResponse> responseMessage = this.processService.uploadOrSyncWorkResult(workResultSyncRequest);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * 해당일의 시간대별 세부공정보고수 조회
     *
     * @param targetDate 조회할 날짜
     * @param status     조회할 상태
     * @return
     */
    @ApiOperation(value = "해당일의 시간대별 세부공정보고수 조회", notes = "by 민항기", tags = "dev")
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
        ApiResponse<HourlyReportCountListResponse> apiResponse = this.processService.getHourlyReports(targetDate, status);
        return ResponseEntity.ok(apiResponse);
    }

    @ApiOperation(value = "해당월의 일별 통계 조회", notes = "by 민항기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "month", value = "연월(YYYY-MM)", dataType = "string", paramType = "query", required = true, example = "2020-03")
    })
    @GetMapping("/dailyTotalRateAtMonth")
    public ResponseEntity<ApiResponse<MonthlyStatisticsResponse>> getDailyTotalRateAtMonth(@RequestParam("month") String month) {
        if (month.equals(null)) {
            log.info("[month] => [{}]", month);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<MonthlyStatisticsResponse> dailyTotalApiResponse = this.processService.getDailyTotalRateAtMonth(month);
        return ResponseEntity.ok(dailyTotalApiResponse);
    }

    /**
     * 이슈 목록 조회
     *
     * @param searchType 검색분류
     * @param search     검색어
     * @param inout      공정내외 필터
     * @param pageable
     * @return
     */
    @ApiOperation(value = "이슈 목록 조회", notes = "by 민항기\nsearchType을 최우선 확인함. searchType이 NONE인 경우 검색어는 무시됨.\n정렬 컬럼명은 updated_at")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "searchType", value = "검색분류(PROCESS_NAME, SUBPROCESS_NAME, JOB_NAME, USER_NAME, NONE)", dataType = "object", paramType = "query", defaultValue = "NONE"),
            @ApiImplicitParam(name = "search", value = "검색어 - searchType이 NONE인 경우 검색어는 무시됨.", dataType = "string", paramType = "query", defaultValue = ""),
            @ApiImplicitParam(name = "inout", value = "공정내외 필터 - ALL, IN, OUT", dataType = "object", paramType = "query", defaultValue = "ALL"),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건)", dataType = "String", paramType = "query", defaultValue = "updated_at,desc")
    })
    @GetMapping("/issues")
    public ResponseEntity<ApiResponse<IssuesResponse>> getIssues(@RequestParam(value = "searchType", required = false) SearchType searchType, @RequestParam(value = "search", required = false) String search, @RequestParam(value = "inout", required = false) AllInOut inout, @ApiIgnore PageRequest pageable) {
        ApiResponse<IssuesResponse> issuesResponseApiResponse = null;
        // inout 기본값
        if (inout == null) inout = AllInOut.ALL;
        // 검색분류 null 체크.
        if (searchType == null) {
            searchType = SearchType.NONE;
        }
        // 검색어가 없다면 검색분류도 없는 것으로 처리.
        if (search == null || search.isEmpty()) {
            searchType = SearchType.NONE;
        }
        switch (searchType) {
            case USER_NAME:
                // 작업자명 검색시
                switch (inout) {
                    case IN:
                        // 공정내에서의 작업자명 검색시
                        issuesResponseApiResponse = this.processService.getIssuesInSearchUserName(search, pageable.of());
                        break;
                    case OUT:
                        // 공정외에서의 작업자명 검색시
                        issuesResponseApiResponse = this.processService.getIssuesOutSearchUserName(search, pageable.of());
                        break;
                    default:
                        // 모든 이슈의 작업자명 검색시
                        issuesResponseApiResponse = this.processService.getIssuesAllSearchUserName(search, pageable.of());
                }
                break;
            case NONE:
                // 검색어가 비어 있을 경우
                switch (inout) {
                    case IN:
                        issuesResponseApiResponse = this.processService.getIssuesIn(pageable.of());
                        break;
                    case OUT:
                        issuesResponseApiResponse = this.processService.getIssuesOut(pageable.of());
                        break;
                    default:
                        issuesResponseApiResponse = this.processService.getIssuesAll(pageable.of());
                }
                break;
            default:
                // 공정, 세부공정, 작업으로 검색시
                switch (inout) {
                    case OUT:
                        // 작업외 검색이므로 작업을 검색시 예외처리
                        throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
                    default:
                        // 작업 내에서 공정, 세부공정, 작업을 검색
                        switch (searchType) {
                            case PROCESS_NAME:
                                issuesResponseApiResponse = this.processService.getIssuesInSearchProcessTitle(search, pageable.of());
                                break;
                            case SUBPROCESS_NAME:
                                issuesResponseApiResponse = this.processService.getIssuesInSearchSubProcessTitle(search, pageable.of());
                                break;
                            case JOB_NAME:
                                issuesResponseApiResponse = this.processService.getIssuesInSearchJobTitle(search, pageable.of());
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
     * @param processId    공정 식별자
     * @param subProcessId 세부공정 식별자
     * @param pageable
     * @return
     */
    @ApiOperation(value = "리포트 목록 조회", notes = "by 민항기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId", value = "공정 식별자 - 공정내에서 조회", dataType = "string", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "subProcessId", value = "세부공정 식별자 - 세부공정내에서 조회", dataType = "string", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "search", value = "검색어 - searchType이 NONE인 경우 검색어는 무시됨.", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "reported", value = "보고일 필터링 여부(true, false)", dataType = "Boolean", paramType = "query", defaultValue = "false"),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건) - ex)reported_date,desc", dataType = "String", paramType = "query", defaultValue = "updated_at,desc")
    })
    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<ReportsResponse>> getReports(@RequestParam(value = "processId", required = false) Long processId, @RequestParam(value = "subProcessId", required = false) Long subProcessId, @RequestParam(value = "search", required = false) String search, @RequestParam(value = "reported", required = false) Boolean reported, @ApiIgnore PageRequest pageable) {
        ApiResponse<ReportsResponse> reportsResponseApiResponse = this.processService.getReports(processId, subProcessId, search, reported, pageable.of());
        return ResponseEntity.ok(reportsResponseApiResponse);
    }

    /**
     * 스마트툴 작업 목록 조회
     *
     * @param subProcessId
     * @param search
     * @param reported
     * @param pageable
     * @return
     */
    @ApiOperation(value = "스마트툴 작업 목록 조회", notes = "by 민항기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subProcessId", value = "세부공정 식별자 - 세부공정내에서 조회", dataType = "string", paramType = "query", defaultValue = ""),
            @ApiImplicitParam(name = "search", value = "검색어 - smartToolJobId를 검색", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "reported", value = "보고일 필터링 여부(true, false)", dataType = "Boolean", paramType = "query", defaultValue = "false"),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건)", dataType = "String", paramType = "query", defaultValue = "updated_at,desc")
    })
    @GetMapping("/smartToolJobs")
    public ResponseEntity<ApiResponse<SmartToolsResponse>> getSmartToolJobs(@RequestParam(value = "subProcessId", required = false) Long subProcessId, @RequestParam(value = "search", required = false) String search, @RequestParam(value = "reported", required = false) Boolean reported, @ApiIgnore PageRequest pageable) {
        ApiResponse<SmartToolsResponse> smartToolsResponseApiResponse = this.processService.getSmartToolJobs(subProcessId, search, reported, pageable.of());
        return ResponseEntity.ok(smartToolsResponseApiResponse);
    }

    /**
     * 전체 공정 진행률 조회
     *
     * @return
     */
    @ApiOperation(value = "전체 공정 진행률 조회", notes = "by 민항기")
    @GetMapping("/totalRate")
    public ResponseEntity<ResponseMessage> getTotalRate() {
        ResponseMessage responseMessage = this.processService.getTotalRate();
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * 전체 공정 진행률 및 공정진행상태별 현황 조회
     *
     * @return
     */
    @ApiOperation(value = "전체 공정 진행률 및 공정진행상태별 현황 조회", notes = "by 민항기")
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<ProcessesStatisticsResponse>> getStatistics() {
        ApiResponse<ProcessesStatisticsResponse> processesStatisticsResponseApiResponse = this.processService.getStatistics();
        return ResponseEntity.ok(processesStatisticsResponseApiResponse);
    }

    /**
     * 전체 공정의 목록을 조회
     *
     * @param search   검색어
     * @param pageable - 페이징 요청
     * @return
     */
    @ApiOperation(value = "전체 공정 목록 조회", notes = "by 민항기\n")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "search", value = "검색어 - searchType이 NONE인 경우 검색어는 무시됨.", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "filter", value = "공정상태 필터(WAIT,UNPROGRESSING,PROGRESSING,COMPLETED,INCOMPLETED,FAILED,SUCCESS,FAULT,NONE)", dataType = "object", paramType = "query", defaultValue = "PROGRESSING"),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건 - reported_date, name, start_date, end_date, state)", dataType = "String", paramType = "query", defaultValue = "updated_at,desc")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<ProcessListResponse>> getProcessList(@RequestParam(value = "search", required = false) String search, @RequestParam(value = "filter", required = false) List<Conditions> filter, @ApiIgnore PageRequest pageable) {
        // TODO : 필터 구현 필요
        ApiResponse<ProcessListResponse> processListApiResponse = this.processService.getProcessList(search, filter, pageable.of());
        return ResponseEntity.ok(processListApiResponse);
    }

    /**
     * 공정종료
     *
     * @param processId 공정 식별자
     * @return
     */
    @ApiOperation(value = "공정종료", notes = "by 민항기\n진행중인 공정을 종료합니다. 더이상 공정 수행을 할 수 없게 됩니다. 또한 활성화된 공정은 하나이기 때문에 공정이 종료되면 해당 컨텐츠를 삭제할 수 있습니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId", value = "공정 식별자", dataType = "string", paramType = "path", required = true, example = "1")
    })
    @PutMapping("/{processId}/closed")
    public ResponseEntity<ApiResponse<ProcessInfoResponse>> setClosedProcess(@PathVariable("processId") Long processId) {
        if (Objects.isNull(processId)) {
            log.info("[processId] => [{}]", processId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ProcessInfoResponse> processInfoResponseApiResponse = this.processService.setClosedProcess(processId);
        return ResponseEntity.ok(processInfoResponseApiResponse);
    }

    /**
     * 공정상세조회
     *
     * @param processId 공정 식별자
     * @return
     */
    @ApiOperation(value = "공정상세조회", notes = "by 민항기\n공정 상세내용을 조회합니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId", value = "공정 식별자", dataType = "string", paramType = "path", required = true, example = "1")
    })
    @GetMapping("/{processId}")
    public ResponseEntity<ApiResponse<ProcessInfoResponse>> getProcessInfo(@PathVariable("processId") Long processId) {
        if (Objects.isNull(processId)) {
            log.info("[processId] => [{}]", processId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ProcessInfoResponse> processInfoResponseApiResponse = this.processService.getProcessInfo(processId);
        return ResponseEntity.ok(processInfoResponseApiResponse);
    }

    /**
     * 공정생성
     *
     * @param registerNewProcess
     * @param result
     * @return
     * @throws IOException by 장정현
     */
    @ApiOperation(value = "공정생성", notes = "by 장정현")
    @PostMapping("/process")
    public ResponseEntity<ApiResponse<ProcessRegisterResponse>> createProcess(@RequestBody @Valid ProcessRegisterRequest registerNewProcess, BindingResult result) {
        if (result.hasErrors()) {
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ProcessRegisterResponse> responseMessage = this.processService.createTheProcess(registerNewProcess);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * 공정삭제
     *
     * @param processId
     * @return
     */
    @ApiOperation(value = "공정삭제", notes = "by 민항기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId", value = "공정 식별자", dataType = "string", paramType = "path", required = true, example = "10")
    })
    @DeleteMapping("/{processId}")
    public ResponseEntity<ApiResponse<ProcessSimpleResponse>> deleteProcessHandler(@PathVariable("processId") Long processId) {
        ApiResponse<ProcessSimpleResponse> processSimpleResponseApiResponse = this.processService.deleteTheProcess(processId);
        return ResponseEntity.ok(processSimpleResponseApiResponse);
    }

    /**
     * 공정편집
     *
     * @param processId 공정 식별자
     * @return BY 민항기
     */
    @ApiOperation(value = "공정편집", notes = "by 민항기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId", value = "공정 식별자", dataType = "string", paramType = "path", required = true, example = "1")
    })
    @PostMapping("/{processId}")
    public ResponseEntity<ResponseMessage> updateProcess(@PathVariable("processId") Long processId, @RequestBody @Valid EditProcessRequest editProcessRequest, BindingResult result) {
        if (Objects.isNull(processId) || result.hasErrors()) {
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ResponseMessage responseMessage = this.processService.updateProcess(editProcessRequest);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * 세부공정목록조회
     *
     * @param processId 공정 식별자
     * @return
     */
    @ApiOperation(value = "세부공정목록조회", notes = "by 민항기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId", value = "공정 식별자", dataType = "string", paramType = "path", required = true, example = "1"),
            @ApiImplicitParam(name = "search", value = "검색어 - name을 검색", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "filter", value = "공정상태 필터(WAIT,UNPROGRESSING,PROGRESSING,COMPLETED,INCOMPLETED,FAILED,SUCCESS,FAULT,NONE)", dataType = "object", paramType = "query", defaultValue = "PROGRESSING"),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건 - start_date, end_date, name, priority, reported_date)", dataType = "String", paramType = "query", defaultValue = "updated_at,desc")
    })
    @GetMapping("/{processId}/subProcesses")
    public ResponseEntity<ApiResponse<SubProcessListResponse>> getSubProcessList(@PathVariable("processId") Long processId, @RequestParam(value = "search", required = false) String search, @RequestParam(value = "filter", required = false) List<Conditions> filter, @ApiIgnore PageRequest pageable) {
        if (Objects.isNull(processId)) {
            log.info("[processId] => [{}]", processId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<SubProcessListResponse> subProcessListResponseApiResponse = this.processService.getSubProcessList(processId, search, filter, pageable.of());
        return ResponseEntity.ok(subProcessListResponseApiResponse);
    }

    /**
     * 워크스페이스의 전체 세부공정목록조회
     *
     * @param processId 공정 식별자
     * @return
     */
    @ApiOperation(value = "워크스페이스의 전체 세부공정목록조회", notes = "by 민항기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processId", value = "공정 식별자", dataType = "string", paramType = "query", example = ""),
            @ApiImplicitParam(name = "search", value = "검색어 - name을 검색", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건)", dataType = "String", paramType = "query", defaultValue = "updated_at,desc")
    })
    @GetMapping("/subProcesses")
    public ResponseEntity<ApiResponse<SubProcessesResponse>> getSubProcesses(@RequestParam(required = false, value = "processId") Long processId, @RequestParam(value = "search", required = false) String search, @ApiIgnore PageRequest pageable) {
        ApiResponse<SubProcessesResponse> subProcessesResponseApiResponse = this.processService.getSubProcesses(processId, search, pageable.of());
        return ResponseEntity.ok(subProcessesResponseApiResponse);
    }

    /**
     * 세부공정 상세조회
     *
     * @param subProcessId 세부공정 식별자
     * @return
     */
    @ApiOperation(value = "세부공정 상세조회", notes = "by 민항기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subProcessId", value = "세부공정 식별자", dataType = "string", paramType = "path", required = true, example = "1"),
    })
    @GetMapping("/subProcesses/{subProcessId}")
    public ResponseEntity<ApiResponse<SubProcessInfoResponse>> getSubProcess(@PathVariable("subProcessId") Long subProcessId) {
        ApiResponse<SubProcessInfoResponse> subProcessesResponseApiResponse = this.processService.getSubProcess(subProcessId);
        return ResponseEntity.ok(subProcessesResponseApiResponse);
    }

    /**
     * 내 작업(나에게 할당된 세부공정) 목록 조회
     *
     * @param pageable
     * @return
     */
    @ApiOperation(value = "내 작업(나에게 할당된 세부공정) 목록 조회", notes = "by 민항기\n파라미터의 공정 식별자, 검색어로의 필터 기능은 아직 제공되지 않습니다.\n담당자의 작업목록 조회만 가능합니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workerUUID", value = "담당자 식별자", dataType = "string", paramType = "path", required = true, defaultValue = "449ae69cee53b8a6819053828c94e496"),
            @ApiImplicitParam(name = "processId", value = "공정 식별자", dataType = "string", paramType = "query", defaultValue = ""),
            @ApiImplicitParam(name = "search", value = "검색어(콘텐츠명/사용자명) - 미개발", dataType = "string", defaultValue = ""),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건)", dataType = "String", paramType = "query", defaultValue = "updated_at,desc")
    })
    @GetMapping("/myWorks/{workerUUID}")
    public ResponseEntity<ApiResponse<MyWorkListResponse>> getMyWorks(@PathVariable("workerUUID") String workerUUID, @RequestParam(required = false, value = "processId") Long processId, @RequestParam(required = false, value = "search") String search, @ApiIgnore PageRequest pageable) {
        if (workerUUID.isEmpty()) {
            log.info("[workerUUID] => [{}]", workerUUID);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<MyWorkListResponse> myWorkListResponseApiResponse = this.processService.getMyWorks(workerUUID, processId, search, pageable.of());
        return ResponseEntity.ok(myWorkListResponseApiResponse);
    }

    @ApiOperation(value = "Aruco의 세부공정 목록 조회", notes = "by 민항기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "arucoId", value = "ARUCO 식별자", dataType = "string", required = true, paramType = "path", defaultValue = ""),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건)", dataType = "String", paramType = "query", defaultValue = "updated_at,desc")
    })
    @GetMapping("/aruco/{arucoId}")
    public ResponseEntity<ApiResponse<SubProcessesOfArucoResponse>> getSubProcessesOfAruco(@PathVariable("arucoId") Long arucoId, @ApiIgnore PageRequest pageable) {
        if (arucoId == null) {
            log.info("[arucoId] => [{}]", arucoId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<SubProcessesOfArucoResponse> subProcessesOfArucoResponseApiResponse = this.processService.getSubProcessesOfAruco(arucoId, pageable.of());
        return ResponseEntity.ok(subProcessesOfArucoResponseApiResponse);
    }

    /**
     * 세부공정편집
     *
     * @param subProcessId
     * @return
     */
    @ApiOperation(value = "세부공정편집", notes = "by 민항기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subProcessId", value = "세부공정 식별자", dataType = "string", paramType = "path", required = true, example = "1"),
    })
    @PostMapping("/subProcesses/{subProcessId}")
    public ResponseEntity<ResponseMessage> updateSubProcess(@PathVariable("subProcessId") Long subProcessId, @RequestBody EditSubProcessRequest subProcessRequest) {
        if (subProcessId == null) {
            log.info("[subProcessId] => [{}]", subProcessId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ResponseMessage responseMessage = this.processService.updateSubProcess(subProcessId, subProcessRequest);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * 작업목록조회
     *
     * @param subProcessId
     * @param pageable
     * @return
     */
    @ApiOperation(value = "작업목록조회", notes = "by 민항기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "subProcessId", value = "세부공정 식별자", dataType = "string", paramType = "path", required = true, example = "1"),
            @ApiImplicitParam(name = "search", value = "검색어 - name을 검색", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "filter", value = "공정상태 필터(WAIT,UNPROGRESSING,PROGRESSING,COMPLETED,INCOMPLETED,FAILED,SUCCESS,FAULT,NONE)", dataType = "object", paramType = "query", defaultValue = "PROGRESSING"),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건- priority, name, result, is_reported)", dataType = "String", paramType = "query", defaultValue = "updated_at,desc")
    })
    @GetMapping("/subProcesses/{subProcessId}/jobs")
    public ResponseEntity<ApiResponse<JobListResponse>> getJobs(@PathVariable("subProcessId") Long subProcessId, @RequestParam(value = "search", required = false) String search, @RequestParam(value = "filter", required = false) List<Conditions> filter, @ApiIgnore PageRequest pageable) {
        if (subProcessId == null) {
            log.info("[subProcessId] => [{}]", subProcessId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<JobListResponse> jobListResponseApiResponse = this.processService.getJobs(subProcessId, search, filter, pageable.of());
        return ResponseEntity.ok(jobListResponseApiResponse);
    }

    /**
     * 이슈상세조회
     *
     * @param issueId
     * @return
     */
    @ApiOperation(value = "이슈상세조회", notes = "by 민항기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "issueId", value = "issue 식별자", dataType = "string", paramType = "path", required = true, example = "1")
    })
    @GetMapping("/issue/{issueId}")
    public ResponseEntity<ApiResponse<IssueInfoResponse>> getIssueInfo(@PathVariable("issueId") Long issueId) {
        if (Objects.isNull(issueId)) {
            log.info("[issueId] => [{}]", issueId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<IssueInfoResponse> issueInfoResponseApiResponse = this.processService.getIssueInfo(issueId);
        return ResponseEntity.ok(issueInfoResponseApiResponse);
    }

    /**
     * 리포트상세조회
     *
     * @param reportId
     * @return
     */
    @ApiOperation(value = "리포트상세조회", notes = "by 민항기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reportId", value = "report 식별자", dataType = "string", paramType = "path", required = true, example = "1")
    })
    @GetMapping("/report/{reportId}")
    public ResponseEntity<ApiResponse<ReportInfoResponse>> getReportInfo(@PathVariable("reportId") Long reportId) {
        if (Objects.isNull(reportId)) {
            log.info("[reportId] => [{}]", reportId);
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ReportInfoResponse> reportInfoResponseApiResponse = this.processService.getReportInfo(reportId);
        return ResponseEntity.ok(reportInfoResponseApiResponse);
    }

    @ApiOperation(value = "aruco 값으로 활성화된(State.CREATED) 공정 조회", notes = "by 장정현")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetId", value = "공정에 할당된 aruco 값", paramType = "path", required = true, example = "1")
    })
    @GetMapping("/target/{targetId}")
    public ResponseEntity<ApiResponse<ProcessTargetInfoResponse>> getProcessInfoByTargetValue(@PathVariable("targetId") Long targetId) {
        if (targetId <= 0) {
            throw new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS);
        }
        ApiResponse<ProcessTargetInfoResponse> responseMessage = this.processService.getProcessInfoByTarget(targetId);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "작업자에게 할당된 세부공정수 및 진행중인 세부공정수 조회", notes = "by 민항기")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workerUUID", value = "작업자 uuid", paramType = "path", required = true, example = "449ae69cee53b8a6819053828c94e496")
    })
    @GetMapping("/subProcesses/count/onWorker/{workerUUID}")
    public ResponseEntity<ApiResponse<CountSubProcessOnWorkerResponse>> getCountSubProcessOnWorker(@PathVariable("workerUUID") String workerUUID) {
        if (workerUUID.isEmpty()) {
            throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<CountSubProcessOnWorkerResponse> apiResponse = this.processService.getCountSubProcessOnWorker(workerUUID);
        return ResponseEntity.ok(apiResponse);
    }
}