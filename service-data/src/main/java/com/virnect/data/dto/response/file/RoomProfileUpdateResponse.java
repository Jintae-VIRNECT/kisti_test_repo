package com.virnect.data.dto.response.file;

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
    @ApiModelProperty(value = "Remote Session Identifier", position = 1, example = "ses_NxKh1OiT2S")
    private String sessionId = "";
    @ApiModelProperty(value = "Remote Session Profile Image URL", position = 2, example = "http://localhost:8081/users/upload/2020-04-07_ilzUZjnHMZqhoRpkqMUn.jpg")
    private String profile  = "";
}
