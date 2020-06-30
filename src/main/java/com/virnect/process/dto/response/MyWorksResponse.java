package com.virnect.process.dto.response;

import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.State;
import com.virnect.process.domain.YesOrNo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class MyWorksResponse {
    @NotBlank
    @ApiModelProperty(value = "작업식별자", notes = "작업식별자(UUID)", example = "1")
    private final long taskId;

    @NotBlank
    @ApiModelProperty(value = "작업명", notes = "작업명", position = 1, example = "작업이름")
    private final String taskName;

    @NotBlank
    @ApiModelProperty(value = "컨텐츠식별자", notes = "컨텐츠식별자(UUID)", position = 2, example = "498b1839dc29ed7bb2ee90ad6985c608")
    private final String contentUUID;

    @NotBlank
    @ApiModelProperty(value = "다운로드 경로", notes = "다운로드 API의 서버 주소 및 포트를 제외한 경로", position = 3, example = "http://~~~")
    private final String downloadPath;

    @NotBlank
    @ApiModelProperty(value = "세부작업식별자", notes = "세부작업식별자", position = 4, example = "1")
    private final long subTaskId;

    @NotBlank
    @ApiModelProperty(value = "세부작업명", notes = "세부작업명(씬그룹명)", position = 5, example = "자제 절단")
    private final String subTaskName;

    @NotBlank
    @ApiModelProperty(value = "세부 작업 순서", notes = "세부 작업의 순서 번호", position = 6, example = "1")
    private final int priority;

    @ApiModelProperty(value = "하위 작업 개수", notes = "세부 작업 하위에 작업의 개수", position = 7, example = "3")
    private final int stepTotal;

    @ApiModelProperty(value = "세부 작업 시작 일시", notes = "세부 작업 시작 일시", position = 8, example = "2020-01-16T13:14:02")
    private final LocalDateTime startDate;

    @ApiModelProperty(value = "세부 작업 종료 일시", notes = "세부 작업 종료 일시", position = 9, example = "2020-01-16T14:14:02")
    private final LocalDateTime endDate;

    @ApiModelProperty(value = "세부 작업 보고일", notes = "세부 작업 작업 보고일", position = 10, example = "2020-01-16T14:14:02")
    private final LocalDateTime reportedDate;

    @ApiModelProperty(value = "세부 작업 상태", notes = "세부 작업의 작업진행 상태", position = 11, example = "PROGRESS")
    private final Conditions conditions;

    @ApiModelProperty(value = "세부 작업 진행률", notes = "세부 작업 작업의 진행률(%)", position = 12, example = "30")
    private final int progressRate;

    @ApiModelProperty(value = "세부 작업 신규할당 여부", notes = "세부 작업의 신규 작업할당 되었는지의 여부", position = 13, example = "true")
    private final YesOrNo isRecent;

    @ApiModelProperty(value = "작업 담당 사용자 식별자", notes = "작업 담당 사용자의 식별자", position = 14, example = "498b1839dc29ed7bb2ee90ad6985c608")
    private final String workerUUID;

    @ApiModelProperty(value = "사용자 이름", position = 15, example = "VIRNECT Master")
    private String workerName;

    @ApiModelProperty(value = "사용자 프로필 이미지 URL", position = 16, example = "VIRNECT 워크스페이스 유저")
    private String workerProfile;

    @ApiModelProperty(value = "완료된 작업수", notes = "완료된 작업의 개수", position = 17, example = "1")
    private final int doneCount;

    @ApiModelProperty(value = "작업 생명주기 상태", notes = "작업 생명주기에서의 생성, 종료, 삭제 등의 상태", position = 18, example = "CREATED")
    private final State state;

    @ApiModelProperty(value = "하위 이슈 수", notes = "하위작업의 이슈 수", position = 19, example = "3")
    private final Long issueTotal;

    @Builder
    public MyWorksResponse(@NotBlank long taskId, @NotBlank String taskName, @NotBlank String contentUUID, @NotBlank String downloadPath, @NotBlank long subTaskId, @NotBlank String subTaskName, @NotBlank int priority, int stepTotal, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime reportedDate, Conditions conditions, int progressRate, YesOrNo isRecent, String workerUUID, String workerName, String workerProfile, int doneCount, State state, Long issueTotal) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.contentUUID = contentUUID;
        this.downloadPath = downloadPath;
        this.subTaskId = subTaskId;
        this.subTaskName = subTaskName;
        this.priority = priority;
        this.stepTotal = stepTotal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reportedDate = reportedDate;
        this.conditions = conditions;
        this.progressRate = progressRate;
        this.isRecent = isRecent;
        this.workerUUID = workerUUID;
        this.workerName = workerName;
        this.workerProfile = workerProfile;
        this.doneCount = doneCount;
        this.state = state;
        this.issueTotal = issueTotal;
    }
}
