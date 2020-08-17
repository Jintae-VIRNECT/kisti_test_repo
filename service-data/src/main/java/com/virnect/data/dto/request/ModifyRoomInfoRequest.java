package com.virnect.data.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel
public class ModifyRoomInfoRequest {

    @NotBlank
    @ApiModelProperty(value = "변경할 협업 방 이름", example = "Changed Title")
    private String title;

    @ApiModelProperty(value = "변경할 협업 방 설명", position = 1, example = "Changed Description")
    private String description;

}
