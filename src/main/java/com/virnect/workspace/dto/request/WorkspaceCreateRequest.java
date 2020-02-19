package com.virnect.workspace.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-18
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class WorkspaceCreateRequest {
    @ApiModelProperty(example = "userId")
    @NotNull(message = "워크스페이스에 초대하려는 사용자의 uuid는 필수 값입니다.")
    private String userId;

    @ApiModelProperty(example = "smic workspace")
    private String description;

    @Override
    public String toString() {
        return "WorkspaceInfo{" +
                "userId='" + userId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
