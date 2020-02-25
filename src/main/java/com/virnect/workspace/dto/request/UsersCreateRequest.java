package com.virnect.workspace.dto.request;

import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-24
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
public class UsersCreateRequest {
    @Valid
    private List<UserInfo> userInfoList;

    @Getter
    public class UserInfo {
        @NotBlank(message = "생성할 워크스페이스 멤버의 아이디는 필수 값입니다.")
        private String userId;
        @NotBlank(message = "생성할 워크스페이스 멤버의 이름은 필수 값입니다.")
        private String name;
        @NotBlank(message = "생성할 워크스페이스 멤버의 계정 비밀번호는 필수 값입니다.")
        private String password;

        private String description;
        private List<Long> workspacePermission;
        private List<GroupInfo> groups;
        private String profile;
    }

    @Getter
    public class GroupInfo {
        private String groupName;
        private String managerAssign;
    }

}
