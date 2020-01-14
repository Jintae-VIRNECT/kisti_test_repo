package com.virnect.workspace.dto;

import com.virnect.workspace.domain.Group;
import com.virnect.workspace.domain.WorkspaceUser;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-08
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */

public class WorkspaceDTO {
    @Data
    public static class WorkspaceInfo {
        private Long id;
        private String pinNumber;
        @NotNull
        private String userId;
        private String description;
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;
    }
    @Data
    public static class UserWorkspaceInfo {
        private String uuid;
        private String pinNumber;
        private String description;
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;
        private String role;
    }

}
