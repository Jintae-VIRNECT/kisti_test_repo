package com.virnect.process.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class NewSubProcessRequest {
    @NotBlank
    @ApiModelProperty(value = "씬그룹 식별자", notes = "씬그룹 식별자", required = true, example = "dc634ea5-b67f-4588-891e-286171271126")
    private String id;

    @NotBlank
    @ApiModelProperty(value = "세부공정명(씬그룹명)", notes = "세부공정명(씬그룹명)", required = true, position = 1, example = "자제 절단")
    private String name;

    @NotBlank
    @ApiModelProperty(value = "세부 공정 순서(씬그룹 우선순위)", notes = "세부 공정의 순서 번호", required = true, position = 2, example = "1")
    private int priority;

    @NotBlank
    @ApiModelProperty(value = "세부 공정 시작 기간", notes = "세부 공정 시작 기간", required = true, position = 3, example = "2020-01-16T13:14:02")
    private LocalDateTime startDate;

    @NotBlank
    @ApiModelProperty(value = "세부 공정 종료 기간", notes = "세부 공정 종료 기간", required = true, position = 4, example = "2020-01-16T14:14:02")
    private LocalDateTime endDate;

    @NotBlank
    @ApiModelProperty(value = "작업 담당 사용자 식별자", notes = "작업 담당 사용자의 식별자", required = true, position = 5, example = "498b1839dc29ed7bb2ee90ad6985c608")
    private String workerUUID;

    @Override
    public String toString() {
        return "NewSubProcessRequest{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", priority=" + priority +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", workerUUID='" + workerUUID + '\'' +
                '}';
    }
}
