package com.virnect.data.dto.response.room;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.data.dao.RestrictedMode;
import com.virnect.data.dto.response.CoturnResponse;

@Getter
@Setter
@ApiModel
@NoArgsConstructor
public class RoomResponse {
    @ApiModelProperty(value = "Remote Session token", example = "wss://localhost:5000?sessionId=ses_JIiIVBMNKW&token=tok_VNZEjukc3gDJpEej&role=PUBLISHER&version=0.1.0")
    private String token = "";

    @ApiModelProperty(value = "Remote Session Identifier", position = 1, example = "ses_NxKh1OiT2S")
    private String sessionId = "";

    @ApiModelProperty(value = "Coturn Server IP Address", position = 2)
    private List<CoturnResponse> coturn = new ArrayList<>();

    @ApiModelProperty(value = "Remote Server Websocket Address", position = 3, example = "wss://")
    private String wss = "";

    @ApiModelProperty(value = "Camera Operation Type", position = 4, example = "OFF")
    private RestrictedMode restrictedMode;

}
