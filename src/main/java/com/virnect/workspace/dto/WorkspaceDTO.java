package com.virnect.workspace.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-08
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */

public class WorkspaceDTO {
    @Getter
    @Setter
    public static class WorkspaceCreateReq {
        @ApiModelProperty(example = "userId")
        @NotNull
        private String userId;
        @ApiModelProperty(example = "smic workspace", name = "?", value = "!")
        private String description;

        @Override
        public String toString() {
            return "WorkspaceInfo{" +
                    "userId='" + userId + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class WorkspaceInfoRes {
        private String uuid;
        private String pinNumber;
        private String description;
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;
        private String role;


        @Override
        public String toString() {
            return "UserWorkspaceInfo{" +
                    "uuid='" + uuid + '\'' +
                    ", pinNumber='" + pinNumber + '\'' +
                    ", description='" + description + '\'' +
                    ", createdDate=" + createdDate +
                    ", updatedDate=" + updatedDate +
                    ", role='" + role + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class WorkspaceInviteMemberReq {
        @NotNull
        @Email
        private String userEmail;
        //private String license;
        private List<Long> workspacePermission;
        private List<Map<String, String>> groups;
        private List<WorkspaceInviteMemberReq> userInfoList;
    }

    @Getter
    @Setter
    public static class WorkspaceInviteMailReq {
        private String requestUserName; //초대 한 사람 이름
        private String requestUserId; //초대 한 사람 uuid
        private String acceptUrl; //초대 수락 링크
        private String inviteCode; //초대 코드
        private List<Map<String, String>> inviteInfos; //초대 된 사람 이름, 이메일
    }
}
