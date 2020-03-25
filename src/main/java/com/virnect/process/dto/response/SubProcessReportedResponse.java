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
    @ApiModelProperty(value = "공정식별자", notes = "공정식별자(UUID)", example = "1")
    private final long processId;

    @NotBlank
    @ApiModelProperty(value = "공정명", notes = "공정명", position = 1, example = "공정이름")
    private final String processName;

    @NotBlank
    @ApiModelProperty(value = "세부공정식별자", notes = "세부공정식별자", position = 2, example = "1")
    private final long subProcessId;

    @NotBlank
    @ApiModelProperty(value = "세부공정명", notes = "세부공정명(씬그룹명)", position = 3, example = "자제 절단")
    private final String name;

    @ApiModelProperty(value = "세부 공정 상태", notes = "세부 공정의 작업진행 상태", position = 4, example = "progress")
    private final Conditions conditions;

    @ApiModelProperty(value = "세부 공정 보고일", notes = "세부 공정 작업 보고일", position = 5, example = "2020-01-16 14:14:02")
    private final LocalDateTime reportedDate;

    @ApiModelProperty(value = "작업 담당 사용자 식별자", notes = "작업 담당 사용자의 식별자", position = 6, example = "498b1839dc29ed7bb2ee90ad6985c608")
    private final String workerUUID;

    @Builder
    public SubProcessReportedResponse(@NotBlank long processId, @NotBlank String processName, @NotBlank long subProcessId, @NotBlank String name, Conditions conditions, LocalDateTime reportedDate, String workerUUID) {
        this.processId = processId;
        this.processName = processName;
        this.subProcessId = subProcessId;
        this.name = name;
        this.conditions = conditions;
        this.reportedDate = reportedDate;
        this.workerUUID = workerUUID;
    }
}
