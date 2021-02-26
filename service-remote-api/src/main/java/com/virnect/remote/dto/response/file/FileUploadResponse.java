package com.virnect.remote.dto.response.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@ApiModel
@NoArgsConstructor
public class FileUploadResponse {
    @ApiModelProperty(value = "Workspace Identifier")
    private String workspaceId = "";

    @ApiModelProperty(value = "Session Identifier", position = 1)
    private String sessionId = "";

    @ApiModelProperty(value = "User Identifier", position = 2)
    private String uuid = "";

    @ApiModelProperty(value = "file name", position = 3)
    private String name = "";

    @ApiModelProperty(value = "object name", position = 4, example = "2020-08-28_fjxqMrTwIcVmxFIjgfRC")
    private String objectName = "";

    @ApiModelProperty(value = "content type", position = 5, example = "image/png")
    private String contentType = "";

    @ApiModelProperty(value = "file size", position = 6)
    private Long size = 0L;

    @Override
    public String toString() {
        return "FileUploadResponse{" +
                "workspaceId='" + workspaceId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", objectName='" + objectName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}
