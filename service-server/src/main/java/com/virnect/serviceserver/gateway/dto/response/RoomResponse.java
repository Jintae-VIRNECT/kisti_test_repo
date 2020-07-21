package com.virnect.serviceserver.gateway.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class RoomResponse {
    ///@ApiModelProperty(value = "원격협업 룸 ID", example = "1212")
    //private String roomId;

    @ApiModelProperty(value = "token", example = "wss://localhost:5000?sessionId=ses_JIiIVBMNKW&token=tok_VNZEjukc3gDJpEej&role=PUBLISHER&version=0.1.0")
    private String token;

    @ApiModelProperty(value = "원격협업 Session ID", position = 1, example = "ses_NxKh1OiT2S")
    private String sessionId;

}
