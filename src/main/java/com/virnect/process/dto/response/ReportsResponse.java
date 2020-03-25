package com.virnect.process.dto.response;

import com.virnect.process.global.common.PageMetadataResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ReportsResponse {
    @ApiModelProperty(value = "리포트 목록", notes = "리포트의 목록")
    private final List<ReportInfoResponse> reports;

    @ApiModelProperty(value = "페이지 정보", notes = "pangenation 정보", position = 1)
    private final PageMetadataResponse pageMeta;
}
