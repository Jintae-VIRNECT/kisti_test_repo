package com.virnect.process.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProcessIdRetrieveResponse {
    @ApiModelProperty(value = "공정 식별자", notes = "공정을 식별하기 위한 식별자", example = "1")
    private final List<Long> processeIds;
}
