package com.virnect.data.dto.file.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel
public class FileUploadRequest {

    @ApiModelProperty(value = "Workspace id", example = "40f9bbee9d85dca7a34a0dd205aae718")
    @NotBlank
    private String workspaceId;

    @ApiModelProperty(value = "Seesion id")
    @NotBlank
    private String sessionId;

    @ApiModelProperty(value = "user id", example = "410df50ca6e32db0b6acba09bcb457ff")
    @NotBlank
    private String uuid;

    @NotNull
    private MultipartFile file;

    @Override
    public String toString() {
        return "Participant{" +
                "workspaceId='" + workspaceId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", userId='" + uuid + '\'' +
                ", file='" + file.toString() + '\'' +
                '}';
    }
}
