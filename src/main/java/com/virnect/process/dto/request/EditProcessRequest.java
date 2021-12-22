package com.virnect.process.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EditProcessRequest {
    @NotNull
    @ApiModelProperty(value = "작업식별자", notes = "작업식별자(UUID)", required = true, example = "2")
    private Long taskId;


    @ApiModelProperty(value = "컨텐츠 업로드 유저 식별자", notes = "컨텐츠 업로드 유저 식별자", required = true, position = 1, example = "4ea61b4ad1dab12fb2ce8a14b02b7460")
    private String actorUUID = "";

    @NotNull
    @ApiModelProperty(value = "작업 시작일", notes = "작업 시작일", required = true, position = 2, example = "2020-01-16T11:20:33")
    private LocalDateTime startDate;

    @NotNull
    @ApiModelProperty(value = "작업 종료일", notes = "작업 종료일", required = true, position = 3, example = "2020-01-16T12:20:33")
    private LocalDateTime endDate;

    @ApiModelProperty(value = "작업 위치", notes = "작업 위치 정보", required = true, position = 4, example = "A 라인 2번 3번째 기계")
    private String position;

    @NotNull
    @ApiModelProperty(value = "세부 작업 정보 배열", notes = "해당 작업에서의 세부 작업 정보들이 담긴 배열", required = true, position = 5)
    private List<EditSubProcessRequest> subTaskList;

    @ApiModelProperty(value = "작업 담당 사용자 식별자", notes = "작업 담당 사용자의 식별자. 하위작업이 없는 경우에만 입력받습니다.", required = false, position = 6, example = "498b1839dc29ed7bb2ee90ad6985c608")
    private String workerUUID;

    @Override
    public String toString() {
        return "EditProcessRequest{" +
            "taskId=" + taskId +
            ", actorUUID='" + actorUUID + '\'' +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", position='" + position + '\'' +
            ", subTaskList=" + subTaskList +
            ", workerUUID='" + workerUUID + '\'' +
            '}';
    }
}