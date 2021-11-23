package com.virnect.data.dto.response.room;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.data.domain.session.SessionType;

@Getter
@Setter
@ApiModel
@NoArgsConstructor
public class RoomResponse {
    @ApiModelProperty(value = "Remote Session token")
    private String token = "";

    @ApiModelProperty(value = "Remote Session Identifier", position = 1, example = "ses_NxKh1OiT2S")
    private String sessionId = "";

    @ApiModelProperty(value = "Coturn Server IP Address", position = 2)
    private List<CoturnResponse> coturn = new ArrayList<>();

    @ApiModelProperty(value = "Remote Server Websocket Address", position = 3, example = "wss://")
    private String wss = "";

    @ApiModelProperty(value = "Video Restricted Mode", position = 4, example = "OFF")
    private boolean videoRestrictedMode;

    @ApiModelProperty(value = "Audio Restricted Mode", position = 5, example = "OFF")
    private boolean audioRestrictedMode;

    @ApiModelProperty(hidden = true)
    @Enumerated(EnumType.STRING)
    private SessionType sessionType;

    @Builder
    public RoomResponse(
        String token, String sessionId,
        List<CoturnResponse> coturn,
        String wss,
        boolean videoRestrictedMode,
        boolean audioRestrictedMode,
        SessionType sessionType
    ) {
        this.token = token;
        this.sessionId = sessionId;
        this.coturn = coturn;
        this.wss = wss;
        this.videoRestrictedMode = videoRestrictedMode;
        this.audioRestrictedMode = audioRestrictedMode;
        this.sessionType = sessionType;
    }
}
