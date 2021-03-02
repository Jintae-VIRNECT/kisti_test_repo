package com.virnect.serviceserver.serviceremote.dto.response.room;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KickRoomResponse {
    private String workspaceId;
    private String sessionId;
    private String leaderId;
    private String participantId;
    private String connectionId;
    /*@ApiModelProperty(
            value = "Participants Identifier",
            position = 3,
            dataType = "List",
            example = "[\n" +
                    " \"4705cf50e6d02c59b0eef9591666e2a3\",\n" +
                    " \"473b12854daa6afeb9e505551d1b2743\",\n" +
                    "\n" +
                    "]"
    )
    @NotNull
    private List<String> participantIds = new ArrayList<>();*/


    @Override
    public String toString() {
        return "InviteRoomResponse{" +
                "workspaceId='" + workspaceId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", leaderId='" + leaderId + '\'' +
                ", participantId='" + participantId + '\'' +
                ", connectionId='" + connectionId + '\'' +
                '}';
    }
}
