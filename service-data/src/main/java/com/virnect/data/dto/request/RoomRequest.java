package com.virnect.data.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ApiModel
public class RoomRequest {
    @NotBlank
    @ApiModelProperty(value = "Remote Session Title", position = 1, example = "Remote")
    private String title;

    @ApiModelProperty(value = "Remote Session Description", position = 2, example = "This is Remote Collaborate, or.. Conference Room(Session)!!")
    private String description;

    @ApiModelProperty(value = "Enable Auto Server Recording", position = 3, example = "false")
    private boolean autoRecording;

    @ApiModelProperty(value = "Leader User Unique Identifier", position = 4, example = "410df50ca6e32db0b6acba09bcb457ff")
    @NotNull
    private String leaderId;

    @ApiModelProperty(value = "Workspace Identifier", position = 5, example = "40f9bbee9d85dca7a34a0dd205aae718")
    @NotNull
    private String workspaceId;

    /**
     * test17@test.com 4705cf50e6d02c59b0eef9591666e2a3
     * test19@test.com 473b12854daa6afeb9e505551d1b2743
     */
    @ApiModelProperty(
            value = "Participants Identifier",
            position = 7,
            dataType = "List",
            example = "[\n" +
                    "    \"4705cf50e6d02c59b0eef9591666e2a3\",\n" +
                    "    \"473b12854daa6afeb9e505551d1b2743\"\n" +
                    "\n" +
                    "]"
    )
    @NotNull
    private List<String> participantIds;

    @Override
    public String toString() {
        return "RoomRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", autoRecording='" + autoRecording + '\'' +
                ", leaderId='" + leaderId + '\'' +
                ", workspaceId='" + workspaceId + '\'' +
                ", participantIds='" + participantIds.toString() + '\'' +
                '}';
    }
}
