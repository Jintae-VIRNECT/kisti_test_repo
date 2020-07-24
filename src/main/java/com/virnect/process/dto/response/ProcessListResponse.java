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
public class ProcessListResponse {
    @ApiModelProperty(value = "작업 목록", notes = "조회한 작업의 배열")
    private final List<ProcessInfoResponse> tasks;

    @ApiModelProperty(value = "페이지 정보", position = 1, notes = "pangenation 정보")
    private final PageMetadataResponse pageMeta;
}
