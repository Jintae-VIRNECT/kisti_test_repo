package com.virnect.workspace.dto.redis;

import com.virnect.workspace.dto.GroupInfoDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-25
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Setter
@Getter
public class WorkspaceInviteRedisRequest {
    private String userId;
    private String workspaceId;
    private String inviteCode;
    private List<UserInfo> userInfoList;

    @Getter
    @Setter
    public static class UserInfo{
        private String email;
        private String name;
        private List<Long> permission;
        private List<GroupInfoDTO> groups;
        private Boolean existUser;
    }

}
