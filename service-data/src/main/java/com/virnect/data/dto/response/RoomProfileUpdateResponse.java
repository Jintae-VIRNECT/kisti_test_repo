package com.virnect.data.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@ApiModel
@NoArgsConstructor
public class RoomProfileUpdateResponse {
    @ApiModelProperty(value = "원격협업 Session ID", position = 1, example = "ses_NxKh1OiT2S")
    private String sessionId;
    @ApiModelProperty(value = "프로필 이미지 url", position = 2, example = "http://localhost:8081/users/upload/2020-04-07_ilzUZjnHMZqhoRpkqMUn.jpg")
    private String profile;
}
