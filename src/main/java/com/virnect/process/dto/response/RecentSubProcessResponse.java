package com.virnect.process.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RecentSubProcessResponse {
    @ApiModelProperty(value = "신규 세부 공정 유무 결과", notes = "신규 세부공정이 존재하는지 여부 확인, 있으면 true 없으면 false 반환", example = "true")
    private final boolean result;
}
