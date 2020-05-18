package com.virnect.workspace.dto.response;

import com.virnect.workspace.dto.rest.PageMetadataRestResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-05-18
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class WorkspaceHistoryListResponse {
    private final List<WorkspaceHistory> workspaceHistoryList;
    private final PageMetadataRestResponse pageMeta;

    @Getter
    @Setter
    public static class WorkspaceHistory{
        private String message;
        private String createdDate;
        private String updatedDate;
    }
}
