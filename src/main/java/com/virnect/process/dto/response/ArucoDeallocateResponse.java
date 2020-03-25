package com.virnect.process.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ArucoDeallocateResponse {
    @ApiModelProperty(
            value = "aruco 할당 해제 처리 결과",
            notes = "aruco 할당 해제 처리가 완료되었으면 true, 아니면 false 리턴",
            example = "true"
    )
    private final boolean result;
}
