package com.virnect.serviceserver.dto.response;

import com.virnect.serviceserver.dto.CoturnResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ApiModel
@NoArgsConstructor
public class RoomResponse {
    ///@ApiModelProperty(value = "원격협업 룸 ID", example = "1212")
    //private String roomId;

    @ApiModelProperty(value = "token", example = "wss://localhost:5000?sessionId=ses_JIiIVBMNKW&token=tok_VNZEjukc3gDJpEej&role=PUBLISHER&version=0.1.0")
    private String token;

    @ApiModelProperty(value = "원격협업 Session ID", position = 1, example = "ses_NxKh1OiT2S")
    private String sessionId;

    @ApiModelProperty(value = "원격협업 Coturn", position = 2)
    private List<CoturnResponse> coturn = new ArrayList<>();

    @ApiModelProperty(value = "원격협업 웹 소켓 주소", position = 3, example = "wss://")
    private String wss;



}
