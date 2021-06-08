package com.virnect.data.dto.request.room;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InviteRoomRequest {
    @ApiModelProperty(value = "Leader User Unique Identifier",  example = "410df50ca6e32db0b6acba09bcb457ff")
    @NotNull
    private String leaderId;

    /**
     * test17@test.com 4705cf50e6d02c59b0eef9591666e2a3
     * test19@test.com 473b12854daa6afeb9e505551d1b2743
     */
    @ApiModelProperty(
            value = "Remote Session Participant Identifier",
            position = 1,
            dataType = "List",
            example = "[\n" +
                      " \"4705cf50e6d02c59b0eef9591666e2a3\",\n" +
                      " \"473b12854daa6afeb9e505551d1b2743\",\n" +
                      "\n" +
                      "]"
    )
    @NotNull
    private List<String> participantIds;

    @Override
    public String toString() {
        return "InviteRoomRequest{" +
                "leaderId='" + leaderId + '\'' +
                ", participantIds='" + participantIds.toString() + '\'' +
                '}';
    }
}
