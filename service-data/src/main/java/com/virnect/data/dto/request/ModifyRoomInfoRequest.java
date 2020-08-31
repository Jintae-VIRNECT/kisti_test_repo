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
    @ApiModelProperty(value = "사용자 식별자", notes = "원격협업 생성자만 이미지를 변경할 수 있습니다.")
    private String uuid;

    @NotBlank
    @ApiModelProperty(value = "변경할 협업 방 이름", position = 1, example = "Changed Title")
    private String title;

    @ApiModelProperty(value = "변경할 협업 방 설명", position = 2, example = "Changed Description")
    private String description;

}
