package com.virnect.serviceserver.gateway.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomResponse {
    @ApiModelProperty(value = "원격협업 룸 ID", example = "1212")
    private String roomId;

    @ApiModelProperty(value = "url?", position = 1, example = "1212")
    private String url;

    @ApiModelProperty(value = "원격협업 Session ID", position = 2, example = "ses_NxKh1OiT2S")
    private String sessionId;

}
