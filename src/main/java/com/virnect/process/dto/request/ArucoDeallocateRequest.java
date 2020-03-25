package com.virnect.process.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArucoDeallocateRequest {
    @ApiModelProperty(value = "aruco가 할당된 컨텐츠 식별자", notes = "aruco 컨텐츠 할당 해제를 위해 필요한 컨텐츠 식별자", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    private String contentUUID;
}
