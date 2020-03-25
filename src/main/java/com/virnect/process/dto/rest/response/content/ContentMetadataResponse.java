package com.virnect.process.dto.rest.response.content;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ContentMetadataResponse {
    @ApiModelProperty(value = "컨텐츠 식별자", notes = "컨텐츠를 식별하기 위해 사용되는 식별자", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    private final String contentUUID;
    @ApiModelProperty(value = "컨텐츠의 메타데이터", position = 1, notes = "메타데이터 객체")
    private final MetadataInfoResponse metadata;
}
