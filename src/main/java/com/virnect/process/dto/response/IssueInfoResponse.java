package com.virnect.process.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class IssueInfoResponse {
    @ApiModelProperty(value = "이슈 식별자", notes = "이슈 식별자", example = "1")
    private long issueId;

    @ApiModelProperty(value = "사진 파일 경로", notes = "사진 파일 경로", position = 1, example = "http://localhost:8083/process/issue/photo/1.jpg")
    private String photoFilePath;

    @ApiModelProperty(value = "사진 설명", notes = "사진 설명", position = 2, example = "사진의 설명입니다.")
    private String caption;

    @ApiModelProperty(value = "작업 식별자", notes = "작업 식별자", position = 3, example = "1")
    private long taskId;

    @ApiModelProperty(value = "세부 작업 식별자", notes = "세부 작업 식별자", position = 4, example = "1")
    private long subTaskId;

    @ApiModelProperty(value = "작업 식별자", notes = "작업 식별자", position = 5, example = "1")
    private long stepId;

    @ApiModelProperty(value = "보고일시", notes = "보고일시", position = 6, example = "2020-02-15T16:32:13.305")
    private LocalDateTime reportedDate;

    @ApiModelProperty(value = "작업명", notes = "작업명", position = 7, example = "A작업")
    private String taskName;

    @ApiModelProperty(value = "세부 작업명", notes = "세부 작업명", position = 8, example = "1세부작업")
    private String subTaskName;

    @ApiModelProperty(value = "단계명", notes = "단계명", position = 9, example = "가작업")
    private String stepName;

    @ApiModelProperty(value = "작업자 식별자", notes = "작업자 식별자", position = 10, example = "uuid")
    private String workerUUID;

    @ApiModelProperty(value = "작업자 명", notes = "작업자 이름", position = 11, example = "홍길동")
    private String workerName;

    @ApiModelProperty(value = "작업자 프로필", notes = "작업자 프로필 사진 경로", position = 12, example = "http://~~~")
    private String workerProfile;

    @Builder
    public IssueInfoResponse(long issueId, String photoFilePath, String caption, long taskId, long subTaskId, long stepId, LocalDateTime reportedDate, String taskName, String subTaskName, String stepName, String workerUUID, String workerName, String workerProfile) {
        this.issueId = issueId;
        this.photoFilePath = photoFilePath;
        this.caption = caption;
        this.taskId = taskId;
        this.subTaskId = subTaskId;
        this.stepId = stepId;
        this.reportedDate = reportedDate;
        this.taskName = taskName;
        this.subTaskName = subTaskName;
        this.stepName = stepName;
        this.workerUUID = workerUUID;
        this.workerName = workerName;
        this.workerProfile = workerProfile;
    }
}
