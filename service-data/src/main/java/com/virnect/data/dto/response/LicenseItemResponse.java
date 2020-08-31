package com.virnect.data.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class LicenseItemResponse {
    @ApiModelProperty(value = "라이선스 타입", example = "BASIC PLAN")
    private String itemName;

    @ApiModelProperty(value = "원격협업 할당 가능 인원 수", position = 1, example = "3")
    private int userCapacity;

}
