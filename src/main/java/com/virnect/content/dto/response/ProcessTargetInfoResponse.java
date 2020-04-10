package com.virnect.content.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.03.13
 */
@Getter
@Setter
@ApiModel
public class ProcessTargetInfoResponse {
    @NotBlank
    @ApiModelProperty(value = "공정식별자", notes = "공정식별자", example = "1")
    private long id;

    @NotBlank
    @ApiModelProperty(value = "공정명", notes = "공정명", position = 1, example = "자제 절단 세부공정")
    private String name;

    @ApiModelProperty(value = "위치", notes = "위치 설명", position = 3, example = "우측")
    private String position;

    @ApiModelProperty(value = " 공정 시작일", notes = " 공정기간의 시작일", position = 10, example = "2020-01-16 13:14:02")
    private LocalDateTime startDate;

    @ApiModelProperty(value = " 공정 종료일", notes = " 공정기간의 종료일", position = 11, example = "2020-01-16 14:14:02")
    private LocalDateTime endDate;

    @ApiModelProperty(value = " 공정 생성일", notes = "공정 생성일", position = 12, example = "2020-01-16 14:14:02")
    private LocalDateTime createdDate;

    @ApiModelProperty(value = "공정 최종 수정일", notes = " 공정 최종 수정일", position = 13, example = "2020-01-16 14:14:02")
    private LocalDateTime updatedDate;

    @NotBlank
    @ApiModelProperty(value = "ARUCO id", notes = "ARUCO 식별자", position = 15)
    private long arucoId;
}
