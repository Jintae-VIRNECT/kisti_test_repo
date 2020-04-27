package com.virnect.process.dto.response;

import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.YesOrNo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class SubProcessOfTargetResponse {

    @NotBlank
    @ApiModelProperty(value = "세부공정식별자", notes = "세부공정식별자", example = "1")
    private final long subProcessId;

    @NotBlank
    @ApiModelProperty(value = "세부공정명", notes = "세부공정명(씬그룹명)", position = 1, example = "자제 절단")
    private final String name;

    @NotBlank
    @ApiModelProperty(value = "세부 공정 순서", notes = "세부 공정의 순서 번호", position = 2, example = "1")
    private final int priority;

    @ApiModelProperty(value = "하위 작업 개수", notes = "세부 공정 하위에 작업의 개수", position = 3, example = "3")
    private final int jobTotal;

    @ApiModelProperty(value = "세부 공정 시작 일시", notes = "세부 공정 시작 일시", position = 4, example = "2020-01-16 13:14:02")
    private final LocalDateTime startDate;

    @ApiModelProperty(value = "세부 공정 종료 일시", notes = "세부 공정 종료 일시", position = 5, example = "2020-01-16 14:14:02")
    private final LocalDateTime endDate;

    @ApiModelProperty(value = "세부 공정 보고일", notes = "세부 공정 작업 보고일", position = 6, example = "2020-01-16 14:14:02")
    private final LocalDateTime reportedDate;

    @ApiModelProperty(value = "세부 공정 상태", notes = "세부 공정의 작업진행 상태", position = 7, example = "progress")
    private final Conditions conditions;

    @ApiModelProperty(value = "세부 공정 진행률", notes = "세부 공정 작업의 진행률(%)", position = 8, example = "30")
    private final int progressRate;

    @ApiModelProperty(value = "세부 공정 신규할당 여부", notes = "세부 공정의 신규 작업할당 되었는지의 여부", position = 9, example = "true")
    private final YesOrNo isRecent;

    @ApiModelProperty(value = "작업 담당 사용자 식별자", notes = "작업 담당 사용자의 식별자", position = 10, example = "498b1839dc29ed7bb2ee90ad6985c608")
    private final String workerUUID;

    @ApiModelProperty(value = "사용자 이름", position = 11, example = "VIRNECT Master")
    private String workerName;

    @ApiModelProperty(value = "사용자 프로필 이미지 URL", position = 12, example = "VIRNECT 워크스페이스 유저")
    private String workerProfile;

    @Builder
    public SubProcessOfTargetResponse(@NotBlank long subProcessId, @NotBlank String name, @NotBlank int priority, int jobTotal, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime reportedDate, Conditions conditions, int progressRate, YesOrNo isRecent, String workerUUID, String workerName, String workerProfile) {
        this.subProcessId = subProcessId;
        this.name = name;
        this.priority = priority;
        this.jobTotal = jobTotal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reportedDate = reportedDate;
        this.conditions = conditions;
        this.progressRate = progressRate;
        this.isRecent = isRecent;
        this.workerUUID = workerUUID;
        this.workerName = workerName;
        this.workerProfile = workerProfile;
    }
}
