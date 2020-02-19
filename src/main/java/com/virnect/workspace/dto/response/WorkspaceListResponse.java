package com.virnect.workspace.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-18
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@RequiredArgsConstructor
public class WorkspaceListResponse {
    private final List<WorkspaceInfo> workspaceInfo;

    @Getter
    @Setter
    public static class WorkspaceInfo {
        private String uuid;
        private String pinNumber;
        private String description;
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;
        private String role;
    }

}
