package com.virnect.workspace.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class WorkspaceInviteRequest {
    @ApiModelProperty(value = "userId", example = "498b1839dc29ed7bb2ee90ad6985c608")
    @NotBlank(message = "초대하는 유저의 UUID는 필수 값입니다.")
    private String userId;

    @Valid
    private List<UserInfo> userInfoList;

    @Getter
    @Setter
    public static class UserInfo {

        @ApiModelProperty(value = "email", example = "ljk@virnect.com")
        @NotBlank(message = "초대할 유저의 이메일 주소는 필수값입니다.")
        private String email;
        @ApiModelProperty(value = "role", example = "MEMBER")
        @NotBlank(message = "초대할 유저의 워크스페이스 권한은 필수값입니다.")
        private String role;

        @ApiModelProperty(value = "makeType", example = "Basic")
        private String makeType;
        @ApiModelProperty(value = "viewType", example = "Basic")
        private String viewType;
        // private String remotePlan;
    }
}

