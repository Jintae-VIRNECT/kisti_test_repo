package com.virnect.serviceserver.gateway.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class KickRoomRequest {
    @ApiModelProperty(value = "리더 유저 uuid", example = "410df50ca6e32db0b6acba09bcb457ff")
    @NotNull
    private String leaderId;

    @ApiModelProperty(value = "참여자 유저 uuid", position = 1, example = "473b12854daa6afeb9e505551d1b2743")
    @NotNull
    private String participantId;

    @ApiModelProperty(value = "참여자 유저 connectionId", position = 2, example = "connectionId")
    @NotNull
    private String connectionId;

    @Override
    public String toString() {
        return "KickRoomRequest{" +
                "leaderId='" + leaderId + '\'' +
                ", participantId='" + participantId + '\'' +
                ", connectionId='" + connectionId + '\'' +
                '}';
    }

}
