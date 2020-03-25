package com.virnect.process.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class EditSubProcessRequest {
    @NotNull
    @ApiModelProperty(value = "세부공정식별자", notes = "세부공정식별자", required = true, example = "7")
    private Long subProcessId;

    @NotNull
    @ApiModelProperty(value = "세부 공정 시작 기간", notes = "세부 공정 시작 기간", required = true, position = 1, example = "2020-01-16T13:14:02")
    private LocalDateTime startDate;

    @NotNull
    @ApiModelProperty(value = "세부 공정 종료 기간", notes = "세부 공정 종료 기간", required = true, position = 2, example = "2020-01-16T14:14:02")
    private LocalDateTime endDate;

    @NotNull
    @ApiModelProperty(value = "작업 담당 사용자 식별자", notes = "작업 담당 사용자의 식별자", required = true, position = 3, example = "498b1839dc29ed7bb2ee90ad6985c608")
    private String workerUUID;
}
