package com.virnect.process.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class IssueInfoResponse {
    @ApiModelProperty(value = "이슈 식별자", notes = "이슈 식별자", example = "1")
    private long issueId;

    @ApiModelProperty(value = "사진파일 경로", notes = "사진파일 경로", position = 1, example = "http://localhost:8083/process/issue/photo/1.jpg")
    private String photoFilePath;

    @ApiModelProperty(value = "사진설명", notes = "사진설명", position = 2, example = "사진의 설명입니다.")
    private String caption;

    @ApiModelProperty(value = "공정 식별자", notes = "공정 식별자", position = 3, example = "1")
    private long processId;

    @ApiModelProperty(value = "세부공정 식별자", notes = "세부공정 식별자", position = 4, example = "1")
    private long subProcessId;

    @ApiModelProperty(value = "작업 식별자", notes = "작업 식별자", position = 5, example = "1")
    private long jobId;

    @ApiModelProperty(value = "보고일시", notes = "보고일시", position = 6, example = "2020-02-15T16:32:13.305")
    private LocalDateTime reportedDate;

    @ApiModelProperty(value = "공정명", notes = "공정명", position = 7, example = "A공정")
    private String processName;

    @ApiModelProperty(value = "세부공정명", notes = "세부공정명", position = 8, example = "1세부공정")
    private String subProcessName;

    @ApiModelProperty(value = "작업명", notes = "작업명", position = 9, example = "가작업")
    private String jobName;

    @ApiModelProperty(value = "작업자 식별자", notes = "작업자 식별자", position = 10, example = "uuid")
    private String workerUUID;

    @ApiModelProperty(value = "작업자 명", notes = "작업자 이름", position = 10, example = "홍길동")
    private String workerName;

    @ApiModelProperty(value = "작업자 프로필", notes = "작업자 프로필 사진 경로", position = 10, example = "http://~~~")
    private String workerProfile;

    @Builder
    public IssueInfoResponse(long issueId, String photoFilePath, String caption, long processId, long subProcessId, long jobId, LocalDateTime reportedDate, String processName, String subProcessName, String jobName, String workerUUID, String workerName, String workerProfile) {
        this.issueId = issueId;
        this.photoFilePath = photoFilePath;
        this.caption = caption;
        this.processId = processId;
        this.subProcessId = subProcessId;
        this.jobId = jobId;
        this.reportedDate = reportedDate;
        this.processName = processName;
        this.subProcessName = subProcessName;
        this.jobName = jobName;
        this.workerUUID = workerUUID;
        this.workerName = workerName;
        this.workerProfile = workerProfile;
    }
}
