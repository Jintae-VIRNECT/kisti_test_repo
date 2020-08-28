package com.virnect.process.api;

import com.virnect.process.application.StaticsService;
import com.virnect.process.dto.response.MonthlyStatisticsResponse;
import com.virnect.process.dto.response.ProcessesStatisticsResponse;
import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.common.ApiResponse;
import com.virnect.process.global.common.ResponseMessage;
import com.virnect.process.global.error.ErrorCode;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
public class StaticsController {
    private final StaticsService staticsService;

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
        ResponseMessage responseMessage = this.staticsService.getTotalRate(workspaceUUID);
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
        ApiResponse<ProcessesStatisticsResponse> processesStatisticsResponseApiResponse = this.staticsService.getStatistics(workspaceUUID);
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
        ApiResponse<MonthlyStatisticsResponse> dailyTotalApiResponse = this.staticsService.getDailyTotalRateAtMonth(workspaceUUID, month);
        return ResponseEntity.ok(dailyTotalApiResponse);
    }
}
