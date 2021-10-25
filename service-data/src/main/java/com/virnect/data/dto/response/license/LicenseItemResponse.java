package com.virnect.data.dto.response.license;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
@Builder
public class LicenseItemResponse {
    @ApiModelProperty(value = "Licence Type", example = "product")
    private String itemName;

    @ApiModelProperty(value = "Remote Session User Capacity", position = 1, example = "3")
    private int userCapacity;

}
