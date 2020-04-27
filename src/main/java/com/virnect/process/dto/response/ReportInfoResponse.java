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
public class ReportInfoResponse {

    @ApiModelProperty(value = "공정 식별자", notes = "공정 식별자", example = "1")
    private final long processId;

    @ApiModelProperty(value = "세부공정 식별자", notes = "세부공정 식별자", position = 1, example = "1")
    private final long subProcessId;

    @ApiModelProperty(value = "작업 식별자", notes = "작업 식별자", position = 2, example = "1")
    private final long jobId;

    @ApiModelProperty(value = "리포트 식별자", notes = "이슈 식별자", position = 3, example = "uuid")
    private final long reportId;

    @ApiModelProperty(value = "보고일시", notes = "보고일시", position = 4, example = "2020-02-15T16:32:13.305")
    private final LocalDateTime reportedDate;

    @ApiModelProperty(value = "공정명", notes = "공정명", position = 5, example = "A공정")
    private final String processName;

    @ApiModelProperty(value = "세부공정명", notes = "세부공정명", position = 6, example = "1세부공정")
    private final String subProcessName;

    @ApiModelProperty(value = "작업명", notes = "작업명", position = 7, example = "가작업")
    private final String jobName;

    @ApiModelProperty(value = "작업자 식별자", notes = "작업자 식별자", position = 8, example = "uuid")
    private final String workerUUID;

    @ApiModelProperty(value = "사용자 이름", position = 9, example = "VIRNECT Master")
    private String workerName;

    @ApiModelProperty(value = "사용자 프로필 이미지 URL", position = 10, example = "VIRNECT 워크스페이스 유저")
    private String workerProfile;

    @ApiModelProperty(value = "리포트 항목", position = 11, notes = "리포트의 항목들")
    private List<ItemResponse> reportItems;

    @Builder
    public ReportInfoResponse(long processId, long subProcessId, long jobId, long reportId, LocalDateTime reportedDate, String processName, String subProcessName, String jobName, String workerUUID, String workerName, String workerProfile, List<ItemResponse> reportItems) {
        this.processId = processId;
        this.subProcessId = subProcessId;
        this.jobId = jobId;
        this.reportId = reportId;
        this.reportedDate = reportedDate;
        this.processName = processName;
        this.subProcessName = subProcessName;
        this.jobName = jobName;
        this.workerUUID = workerUUID;
        this.workerName = workerName;
        this.workerProfile = workerProfile;
        this.reportItems = reportItems;
    }
}
