package com.virnect.process.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class SmartToolResponse {

    @ApiModelProperty(value = "공정 식별자", notes = "공정 식별자", example = "uuid")
    private final long processId;

    @ApiModelProperty(value = "세부공정 식별자", notes = "세부공정 식별자", position = 1, example = "uuid")
    private final long subProcessId;

    @ApiModelProperty(value = "작업 식별자", notes = "작업 식별자", position = 2, example = "1")
    private final long jobId;

    @ApiModelProperty(value = "스마트툴 식별자", notes = "스마트툴 식별자", position = 3, example = "uuid")
    private final long smartToolId;

    @ApiModelProperty(value = "보고일시", notes = "보고일시", position = 4, example = "2020-02-15T16:32:13.305")
    private final LocalDateTime reportedDate;

    @ApiModelProperty(value = "공정명", notes = "공정명", position = 5, example = "A공정")
    private final String processName;

    @ApiModelProperty(value = "세부공정명", notes = "세부공정명", position = 6, example = "1세부공정")
    private final String subProcessName;

    @ApiModelProperty(value = "작업명", notes = "작업명", position = 7, example = "가작업")
    private final String jobName;

    @ApiModelProperty(value = "스마트툴 jobId", notes = "스마트툴 기기에서의 jobId", position = 8, example = "-1")
    private final String smartToolJobId;

    @ApiModelProperty(value = "체결수", notes = "현재 작업의 체결 완료한 개수", position = 9, example = "3")
    private long smartToolWorkedCount;

    @ApiModelProperty(value = "체결 총수", notes = "체결해야할 총 개수", position = 10, example = "12")
    private int smartToolBatchTotal;

    @ApiModelProperty(value = "체결 기준 토크", notes = "체결해야할 토크 기준값", position = 11, example = "12.21")
    private final String normalToque;

    @ApiModelProperty(value = "작업자 식별자", notes = "작업자 식별자", position = 12, example = "uuid")
    private final String workerUUID;

    @ApiModelProperty(value = "리포트 항목", notes = "리포트의 항목들", position = 13)
    private List<SmartToolItemResponse> smartToolItems;

    @Builder
    public SmartToolResponse(long processId, long subProcessId, long jobId, long smartToolId, LocalDateTime reportedDate, String processName, String subProcessName, String jobName, String smartToolJobId, long smartToolWorkedCount, int smartToolBatchTotal, String normalToque, String workerUUID, List<SmartToolItemResponse> smartToolItems) {
        this.processId = processId;
        this.subProcessId = subProcessId;
        this.jobId = jobId;
        this.smartToolId = smartToolId;
        this.reportedDate = reportedDate;
        this.processName = processName;
        this.subProcessName = subProcessName;
        this.jobName = jobName;
        this.smartToolJobId = smartToolJobId;
        this.smartToolWorkedCount = smartToolWorkedCount;
        this.smartToolBatchTotal = smartToolBatchTotal;
        this.normalToque = normalToque;
        this.workerUUID = workerUUID;
        this.smartToolItems = smartToolItems;
    }
}
