package com.virnect.workspace.dao.workspace;

import com.virnect.workspace.domain.workspace.Workspace;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-22
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface WorkspaceUserRepositoryCustom {
    long countWorkspaceUser(String workspaceId);
    long deleteAllWorkspaceUserByWorkspace(Workspace workspace);
    List<String> getWorkspaceUserIdList(String workspaceId);
}
