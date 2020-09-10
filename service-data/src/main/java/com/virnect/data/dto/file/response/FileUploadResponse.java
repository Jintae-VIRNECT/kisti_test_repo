package com.virnect.data.dto.file.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel
@NoArgsConstructor
public class FileUploadResponse {
    @ApiModelProperty(value = "Workspace id")
    private String workspaceId;

    @ApiModelProperty(value = "Seesion id")
    private String sessionId;

    @ApiModelProperty(value = "User id")
    private String userId;

    @ApiModelProperty(value = "file name")
    private String name;

    /*@ApiModelProperty(value = "file path")
    private String path;*/

    @ApiModelProperty(value = "file size")
    private Long size;

    @Override
    public String toString() {
        return "FileUploadResponse{" +
                "workspaceId='" + workspaceId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                //", path='" + path + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}
