package com.virnect.data.dto.response.session;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class SessionData {
    @ApiModelProperty(
            value = " Media streams will be routed through Remote Server. This Media Mode is mandatory for session recording",
            example = "ROUTED")
    private String mediaMode = "ROUTED";

    @ApiModelProperty(value = "Recording Mode", position = 1, example = "MANUAL")
    private String recordingMode = "MANUAL";

    @ApiModelProperty(
            value = "You can fix the sessionId that will be assigned to the session with this parameter.",
            position = 2)
    private String customSessionId;

    @ApiModelProperty(
            value = "",
            position = 3)
    private String defaultOutputMode = "COMPOSED";

    @ApiModelProperty(
            value = "",
            position = 4)
    private String defaultRecordingLayout = "BEST_FIT";

    @ApiModelProperty(
            value = "",
            position = 5)
    private String defaultCustomLayout;

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "mediaMode='" + mediaMode + '\'' +
                ", recordingMode='" + recordingMode + '\'' +
                ", customSessionId='" + customSessionId + '\'' +
                ", defaultOutputMode='" + defaultOutputMode + '\'' +
                ", defaultRecordingLayout='" + defaultRecordingLayout + '\'' +
                ", defaultCustomLayout=" + defaultCustomLayout + '\'' +
                '}';
    }
}
