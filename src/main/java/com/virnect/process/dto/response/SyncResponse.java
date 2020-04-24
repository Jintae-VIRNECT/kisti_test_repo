package com.virnect.process.dto.response;

import com.virnect.process.global.common.PageMetadataResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class SyncResponse {
    @ApiModelProperty(value = "세부공정 목록", notes = "조회한 세부공정의 배열")
    private final List<SubProcessReportedResponse> subProcesses;

    @ApiModelProperty(value = "페이지 정보", notes = "pangenation 정보", position = 1)
    private final PageMetadataResponse pageMeta;
}
