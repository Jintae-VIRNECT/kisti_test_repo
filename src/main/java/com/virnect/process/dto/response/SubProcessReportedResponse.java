package com.virnect.process.dto.response;

import com.virnect.process.domain.Conditions;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class SubProcessReportedResponse {
    @NotBlank
    @ApiModelProperty(value = "작업 식별자", notes = "작업 식별자(UUID)", example = "1")
    private final long taskId;

    @NotBlank
    @ApiModelProperty(value = "작업명", notes = "작업명", position = 1, example = "작업이름")
    private final String taskName;

    @NotBlank
    @ApiModelProperty(value = "세부 작업 식별자", notes = "세부 작업 식별자", position = 2, example = "1")
    private final long subTaskId;

    @NotBlank
    @ApiModelProperty(value = "세부 작업명", notes = "세부 작업명(씬그룹명)", position = 3, example = "자제 절단")
    private final String subTaskName;

    @ApiModelProperty(value = "세부 작업 상태", notes = "세부 작업의 작업진행 상태", position = 4, example = "progress")
    private final Conditions conditions;

    @ApiModelProperty(value = "세부 작업 보고일", notes = "세부 작업 작업 보고일", position = 5, example = "2020-01-16 14:14:02")
    private final LocalDateTime reportedDate;

    @ApiModelProperty(value = "작업 담당 사용자 식별자", notes = "작업 담당 사용자의 식별자", position = 6, example = "498b1839dc29ed7bb2ee90ad6985c608")
    private final String workerUUID;

    @ApiModelProperty(value = "사용자 이름", position = 7, example = "VIRNECT Master")
    private String workerName;

    @ApiModelProperty(value = "사용자 프로필 이미지 URL", position = 8, example = "VIRNECT 워크스페이스 유저")
    private String workerProfile;

    @Builder
    public SubProcessReportedResponse(@NotBlank long taskId, @NotBlank String taskName, @NotBlank long subTaskId, @NotBlank String subTaskName, Conditions conditions, LocalDateTime reportedDate, String workerUUID, String workerName, String workerProfile) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.subTaskId = subTaskId;
        this.subTaskName = subTaskName;
        this.conditions = conditions;
        this.reportedDate = reportedDate;
        this.workerUUID = workerUUID;
        this.workerName = workerName;
        this.workerProfile = workerProfile;
    }
}
