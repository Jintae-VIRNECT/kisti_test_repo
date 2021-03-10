package com.virnect.serviceserver.serviceremote.dto.request.room;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KickRoomRequest {
    @ApiModelProperty(value = "Leader User Unique Identifier", example = "410df50ca6e32db0b6acba09bcb457ff")
    @NotNull
    private String leaderId;

    @ApiModelProperty(value = "Remote Session Participant Identifier", position = 1, example = "473b12854daa6afeb9e505551d1b2743")
    @NotNull
    private String participantId;

    @Override
    public String toString() {
        return "KickRoomRequest{" +
                "leaderId='" + leaderId + '\'' +
                ", participantId='" + participantId + '\'' +
                '}';
    }

}
