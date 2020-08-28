package com.virnect.process.api;

import com.virnect.process.application.StepService;
import com.virnect.process.domain.Conditions;
import com.virnect.process.dto.response.JobListResponse;
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

import java.util.List;

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
public class StepController {
    private final StepService stepService;

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
        ApiResponse<JobListResponse> jobListResponseApiResponse = this.stepService.getJobs(myUUID, subTaskId, search, filter, pageable.of());
        return ResponseEntity.ok(jobListResponseApiResponse);
    }

}
