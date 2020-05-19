package com.virnect.process.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ProcessManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.03.13
 */
@Getter
@Setter
@ApiModel
public class ProcessTargetInfoResponse {
    @NotBlank
    @ApiModelProperty(value = "작업 식별자", notes = "작업 식별자", example = "1")
    private long id;

    @NotBlank
    @ApiModelProperty(value = "작업명", notes = "작업명", position = 1, example = "자제 절단 세부작업")
    private String name;

    @ApiModelProperty(value = "위치", notes = "위치 설명", position = 3, example = "우측")
    private String position;

    @ApiModelProperty(value = " 작업 시작일", notes = " 작업기간의 시작일", position = 10, example = "2020-01-16 13:14:02")
    private LocalDateTime startDate;

    @ApiModelProperty(value = " 작업 종료일", notes = " 작업기간의 종료일", position = 11, example = "2020-01-16 14:14:02")
    private LocalDateTime endDate;

    @ApiModelProperty(value = " 작업 생성일", notes = "작업 생성일", position = 12, example = "2020-01-16 14:14:02")
    private LocalDateTime createdDate;

    @ApiModelProperty(value = "작업 최종 수정일", notes = " 작업 최종 수정일", position = 13, example = "2020-01-16 14:14:02")
    private LocalDateTime updatedDate;
}
