package com.virnect.workspace.dto.redis;

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
    private List<InviteUserInfo> inviteUserInfo;

    @Getter
    @Setter
    public static class InviteUserInfo {
        private String uuid;
        private String email;
        private String name;
        private String role;
        private String makeType;
        private String viewType;
    }
}
