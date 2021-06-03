package com.virnect.serviceserver.serviceremote.dto.response.room;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class InviteRoomResponse {
    @ApiModelProperty(value = "Workspace Identifier", example = "ses_NxKh1OiT2S")
    private String workspaceId = "";

    @ApiModelProperty(value = "Remote Session Identifier", position = 1, example = "ses_NxKh1OiT2S")
    private String sessionId = "";

    @ApiModelProperty(value = "Leader User Unique Identifier", position = 2, example = "410df50ca6e32db0b6acba09bcb457ff")
    private String leaderId = "";

    @ApiModelProperty(
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
    private List<String> participantIds = new ArrayList<>();

    @ApiModelProperty(value = "Remote Session Title", position = 4, example = "Remote")
    private String title = "";

    @Override
    public String toString() {
        return "InviteRoomResponse{" +
                "workspaceId='" + workspaceId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", leaderId='" + sessionId + '\'' +
                ", participantIds='" + participantIds.toString() + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
