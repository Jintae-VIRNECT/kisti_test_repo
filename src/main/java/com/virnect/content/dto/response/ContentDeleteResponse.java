package com.virnect.content.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@RequiredArgsConstructor
public class ContentDeleteResponse {
    @ApiModelProperty(value = "삭제 처리 결과", notes = "컨텐츠가 삭제되었으면 true, 아니면 false 값을 리턴합니다", example = "true")
    private final boolean result;
}
