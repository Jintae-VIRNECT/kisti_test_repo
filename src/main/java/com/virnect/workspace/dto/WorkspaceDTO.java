package com.virnect.workspace.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
    public static class WorkspaceInfo {
        private Long id;
        private String pinNumber;
        @NotNull
        private String userId;
        private String description;
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;

        @Override
        public String toString() {
            return "WorkspaceInfo{" +
                    "id=" + id +
                    ", pinNumber='" + pinNumber + '\'' +
                    ", userId='" + userId + '\'' +
                    ", description='" + description + '\'' +
                    ", createdDate=" + createdDate +
                    ", updatedDate=" + updatedDate +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class UserWorkspaceInfo {
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

}
