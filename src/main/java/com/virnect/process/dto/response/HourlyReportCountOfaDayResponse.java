package com.virnect.process.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class HourlyReportCountOfaDayResponse {
    @ApiModelProperty(value = "시간대", notes = "00~24", example = "12")
    private final String hour;

    @ApiModelProperty(value = "해당 시간대의 리포트 갯수", notes = "전체 작업의 해당 시간대에 리포팅한 갯수", example = "4")
    private final int reportCount;
}
