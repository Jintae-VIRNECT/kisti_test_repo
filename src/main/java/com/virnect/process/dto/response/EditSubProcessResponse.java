package com.virnect.process.dto.response;

import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.YesOrNo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class EditSubProcessResponse {
    @NotBlank
    @ApiModelProperty(value = "세부 작업 식별자", notes = "세부 작업 식별자", example = "1")
    private final long subTaskId;

    @NotBlank
    @ApiModelProperty(value = "세부 작업명", notes = "세부 작업명(씬그룹명)", position = 1, example = "자제 절단")
    private final String subTaskName;

    @NotBlank
    @ApiModelProperty(value = "세부 작업 순서", notes = "세부 작업의 순서 번호", position = 2, example = "1")
    private final int priority;

    @ApiModelProperty(value = "하위 작업 개수", notes = "세부 작업 하위에 작업의 개수", position = 3, example = "3")
    private final int stepTotal;

    @ApiModelProperty(value = "세부 작업 상태", notes = "세부 작업의 작업진행 상태", position = 4, example = "progress")
    private final Conditions conditions;

    @ApiModelProperty(value = "세부 작업 시작 기간", notes = "세부 작업 시작 기간", position = 5, example = "2020-01-16 13:14:02")
    private final LocalDateTime startDate;

    @ApiModelProperty(value = "세부 작업 종료 기간", notes = "세부 작업 종료 기간", position = 6, example = "2020-01-16 14:14:02")
    private final LocalDateTime endDate;

    @ApiModelProperty(value = "세부 작업 진행률", notes = "세부 작업 작업의 진행률(%)", position = 7, example = "30")
    private final int progressRate;

    @ApiModelProperty(value = "세부 작업 보고일", notes = "세부 작업 작업 보고일", position = 8, example = "2020-01-16 14:14:02")
    private final LocalDateTime reportedDate;

    @ApiModelProperty(value = "세부 작업 신규할당 여부", notes = "세부 작업의 신규 작업할당 되었는지의 여부", position = 9, example = "true")
    private final YesOrNo isRecent;

    @ApiModelProperty(value = "작업 담당 사용자 식별자", notes = "작업 담당 사용자의 식별자", position = 10, example = "498b1839dc29ed7bb2ee90ad6985c608")
    private final String workerUUID;

    @ApiModelProperty(value = "사용자 이름", position = 11, example = "VIRNECT Master")
    private String workerName;

    @ApiModelProperty(value = "사용자 프로필 이미지 URL", position = 12, example = "VIRNECT 워크스페이스 유저")
    private String workerProfile;

    @ApiModelProperty(value = "하위 이슈수", notes = "세부 작업 하위에 이슈의 개수", position = 13, example = "3")
    private final Long issuesTotal;

    @ApiModelProperty(value = "완료된 작업수", notes = "완료된 작업의 개수", position = 14, example = "1")
    private int doneCount;

    @Builder
    public EditSubProcessResponse(@NotBlank long subTaskId, @NotBlank String subTaskName, @NotBlank int priority, int stepTotal, Conditions conditions, LocalDateTime startDate, LocalDateTime endDate, int progressRate, LocalDateTime reportedDate, YesOrNo isRecent, String workerUUID, String workerName, String workerProfile, Long issuesTotal, int doneCount) {
        this.subTaskId = subTaskId;
        this.subTaskName = subTaskName;
        this.priority = priority;
        this.stepTotal = stepTotal;
        this.conditions = conditions;
        this.startDate = startDate;
        this.endDate = endDate;
        this.progressRate = progressRate;
        this.reportedDate = reportedDate;
        this.isRecent = isRecent;
        this.workerUUID = workerUUID;
        this.workerName = workerName;
        this.workerProfile = workerProfile;
        this.issuesTotal = issuesTotal;
        this.doneCount = doneCount;
    }
}
