package com.virnect.data.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel
public class RoomProfileUpdateRequest {
    @NotBlank
    @ApiModelProperty(value = "사용자 식별자", notes = "원격협업 생성자만 이미지를 변경할 수 있습니다.")
    private String uuid;

    @ApiModelProperty(value = "변경할 프로필 이미지",  dataType = "__file", notes = "null인 경우 기본 이미지로 설정합니다.", hidden = true)
    private MultipartFile profile;
}
