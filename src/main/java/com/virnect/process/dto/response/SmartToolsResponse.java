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
public class SmartToolsResponse {
    @ApiModelProperty(value = "스마트툴 목록", notes = "스마트툴의 목록")
    private final List<SmartToolResponse> smartTools;

    @ApiModelProperty(value = "페이지 정보", position = 1, notes = "pangenation 정보")
    private final PageMetadataResponse pageMeta;
}
