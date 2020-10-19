package com.virnect.data.dto.request;

import com.virnect.data.dao.SessionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ApiModel
public class CompanyRequest {
    @ApiModelProperty(value = "Company Code", example = "1")
    @NotNull
    private int companyCode;

    @ApiModelProperty(value = "Workspace Identifier", example = "40f9bbee9d85dca7a34a0dd205aae718")
    @NotNull
    private String workspaceId;

    @ApiModelProperty(value = "User Unique Identifier", position = 1, example = "410df50ca6e32db0b6acba09bcb457ff")
    @NotNull
    private String userId;

    @ApiModelProperty(value = "Remote Session Type", position = 2)
    private String licenseName;

    @ApiModelProperty(value = "Remote Session Type", position = 3)
    @Enumerated(EnumType.STRING)
    private SessionType sessionType;

    @ApiModelProperty(value = "Enable Auto Server Recording", position = 4, example = "false")
    private boolean sttSync;

    @ApiModelProperty(value = "Enable translation", position = 5, example = "false")
    private boolean sttStreaming;

    @ApiModelProperty(value = "Enable Translation Language", position = 6, example = "false")
    private boolean transKoKr;

    @ApiModelProperty(value = "Enable Translation Language", position = 7, example = "false")
    private boolean transEnUs;

    @ApiModelProperty(value = "Enable Translation Language", position = 8, example = "false")
    private boolean transJaJp;

    @ApiModelProperty(value = "Enable Translation Language", position = 9, example = "false")
    private boolean transZh;

    @ApiModelProperty(value = "Enable Translation Language", position = 10, example = "false")
    private boolean transFrFr;

    @ApiModelProperty(value = "Enable Translation Language", position = 11, example = "false")
    private boolean transEsEs;

    @ApiModelProperty(value = "Enable Translation Language", position = 12, example = "false")
    private boolean transRuRu;

    @ApiModelProperty(value = "Enable Translation Language", position = 13, example = "false")
    private boolean transUkUa;

    @ApiModelProperty(value = "Enable Translation Language", position = 14, example = "false")
    private boolean transPlPl;

    /*@Override
    public String toString() {
        return "RoomRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", autoRecording='" + autoRecording + '\'' +
                ", leaderId='" + leaderId + '\'' +
                ", workspaceId='" + workspaceId + '\'' +
                ", participantIds='" + participantIds.toString() + '\'' +
                '}';
    }*/
}
