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
public class IssuesResponse {
    @ApiModelProperty(value = "이슈 목록", notes = "이슈의 목록")
    private final List<IssueInfoResponse> issues;

    @ApiModelProperty(value = "페이지 정보", position = 1, notes = "pangenation 정보")
    private final PageMetadataResponse pageMeta;
}
