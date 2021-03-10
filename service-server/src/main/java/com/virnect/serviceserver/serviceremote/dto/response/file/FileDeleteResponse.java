package com.virnect.serviceserver.serviceremote.dto.response.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class FileDeleteResponse {
    @ApiModelProperty(value = "Workspace Identifier")
    private String workspaceId;
    @ApiModelProperty(value = "Remote Session Identifier", position = 1)
    private String sessionId;
    @ApiModelProperty(value = "File name", position = 2, example = "example file name")
    private String fileName;
}
