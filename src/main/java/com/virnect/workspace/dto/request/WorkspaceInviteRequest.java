package com.virnect.workspace.dto.request;

import lombok.Getter;

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
public class WorkspaceInviteRequest {
    @Valid
    private List<UserInfo> userInviteInfoList;

    @Getter
    public class UserInfo {
        @NotBlank(message = "초대할 유저의 이메일 주소는 필수값입니다.")
        private String userEmail;//초대할 유저

        private List<Long> workspacePermission;//소속 할당할 워크스페이스 정보

        private List<GroupInfo> groups; //소속 할당할 그룹 정보

    }

        @Getter
        public class GroupInfo {
            private String groupName;
            private String managerAssign;
    }
}

