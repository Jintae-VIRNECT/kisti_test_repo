package com.virnect.serviceserver.gateway.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ApiModel
public class RoomProfileUpdateRequest {
    @ApiModelProperty(value = "변경할 프로필 이미지", notes = "null인 경우 기본 이미지로 설정합니다.")
    private MultipartFile profile;
}
