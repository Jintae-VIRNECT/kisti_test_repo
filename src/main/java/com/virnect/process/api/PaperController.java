package com.virnect.process.api;

import com.virnect.process.application.PaperService;
import com.virnect.process.dto.response.ReportInfoResponse;
import com.virnect.process.dto.response.ReportsResponse;
import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.common.ApiResponse;
import com.virnect.process.global.common.PageRequest;
import com.virnect.process.global.error.ErrorCode;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Objects;

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
public class PaperController {
    private final PaperService paperService;
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
            @ApiImplicitParam(name = "myUUID", value = "사용자 식별자 (내 페이퍼 보기용)", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "stepId", value = " 단계 식별자", dataType = "string", paramType = "query")
    })
    @GetMapping("/papers")
    public ResponseEntity<ApiResponse<ReportsResponse>> getPapers(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
            , @RequestParam(value = "taskId", required = false) Long taskId
            , @RequestParam(value = "subTaskId", required = false) Long subTaskId
            , @RequestParam(value = "search", required = false) String search
            , @RequestParam(value = "reported", required = false) Boolean reported
            , @RequestParam(value = "myUUID", required = false) String myUUID
            , @RequestParam(value = "stepId", required = false) Long stepId
            , @ApiIgnore PageRequest pageable) {
        ApiResponse<ReportsResponse> reportsResponseApiResponse = this.paperService.getReports(myUUID, workspaceUUID, taskId, subTaskId, search, reported, pageable.of(), stepId);
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
        ApiResponse<ReportInfoResponse> reportInfoResponseApiResponse = this.paperService.getReportInfo(paperId);
        return ResponseEntity.ok(reportInfoResponseApiResponse);
    }
}
