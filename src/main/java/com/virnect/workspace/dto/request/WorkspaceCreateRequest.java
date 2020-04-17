package com.virnect.workspace.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

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
    @ApiModelProperty(value = "워크스페이스 유저 uuid", example = "userUUID")
    @NotNull(message = "워크스페이스에 초대하려는 사용자의 uuid는 필수 값입니다.")
    private String userId;

    @ApiModelProperty(value = "워크스페이스 이름(빈값 일 경우 닉네임's Workspace로 저장됩니다.)", example = "user's Workspace")
    private String name;

    @ApiModelProperty(value = "워크스페이스 프로필", dataType = "__file")
    private MultipartFile profile;

    @ApiModelProperty(value = "워크스페이스 설명", example = "워크스페이스 설명")
    private String description;

    @Override
    public String toString() {
        return "WorkspaceInfo{" +
                "userId='" + userId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
