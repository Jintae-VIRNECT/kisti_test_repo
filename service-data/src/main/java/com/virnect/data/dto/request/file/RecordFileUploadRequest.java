package com.virnect.data.dto.request.file;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class RecordFileUploadRequest {
    @ApiModelProperty(value = "Workspace Identifier", example = "40f9bbee9d85dca7a34a0dd205aae718")
    @NotNull
    private String workspaceId;

    @ApiModelProperty(value = "Session Identifier")
    @NotNull
    private String sessionId;

    @ApiModelProperty(value = "User Identifier", example = "410df50ca6e32db0b6acba09bcb457ff")
    @NotNull
    private String userId;

    @ApiModelProperty(value = "Local Record File Duration time", example = "3600")
    @NotNull
    private Long durationSec;

    @NotNull
    private MultipartFile file;

    @Override
    public String toString() {
        return "RecordFileUploadRequest{" +
                "workspaceId='" + workspaceId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", durationSec='" + durationSec + '\'' +
                ", file='" + file.toString() + '\'' +
                '}';
    }
}
