package com.virnect.serviceserver.serviceremote.dto.request.company;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.data.domain.session.SessionType;
import com.virnect.serviceserver.serviceremote.dto.request.room.LanguageRequest;

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

    @ApiModelProperty(value = "Enable server recording", position = 4, example = "false")
    private boolean recording;

    @ApiModelProperty(value = "Enable storage service", position = 5, example = "false")
    private boolean storage;

    @ApiModelProperty(value = "Enable text translation", position = 6, example = "false")
    private boolean translation;

    @ApiModelProperty(value = "Enable Speech to text sync", position = 7, example = "false")
    private boolean sttSync;

    @ApiModelProperty(value = "Enable Speech to text streaming", position = 8, example = "false")
    private boolean sttStreaming;

    @ApiModelProperty(value = "Enable Text to speech", position = 8, example = "false")
    private boolean tts;

    @ApiModelProperty(value = "Support translation languages", position = 9)
    private LanguageRequest language;

   @ApiModelProperty(value = "Support Video Restricted Mode", position = 10)
    private boolean videoRestrictedMode;

    @ApiModelProperty(value = "Support Audio Restricted Mode", position = 10)
    private boolean audioRestrictedMode;

    @ApiModelProperty(value = "Local Recording", position = 11)
    private boolean localRecording;
}
