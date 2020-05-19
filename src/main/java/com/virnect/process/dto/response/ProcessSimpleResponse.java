package com.virnect.process.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ProcessSimpleResponse {
    @ApiModelProperty(value = "삭제 처리 결과", notes = "작업의 삭제 처리 정상여부 true, false", example = "true")
    private final boolean result;
}
