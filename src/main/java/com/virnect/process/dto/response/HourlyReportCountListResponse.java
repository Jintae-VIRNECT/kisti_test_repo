package com.virnect.process.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class HourlyReportCountListResponse {
    @ApiModelProperty(value = "리포트 갯수의 해당시간대별 배열", notes = "조회된 해당 시간대별 리포트 갯수가 담긴 배열")
    private final List<HourlyReportCountOfaDayResponse> hourlyReportCountOfaDayResponses;
}
