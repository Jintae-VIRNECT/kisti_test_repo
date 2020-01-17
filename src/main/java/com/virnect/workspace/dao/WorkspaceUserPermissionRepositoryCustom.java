package com.virnect.workspace.dao;

import com.virnect.workspace.domain.WorkspaceRole;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-14
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface WorkspaceUserPermissionRepositoryCustom {
    WorkspaceRole findWorkspaceUserRole(String uuid, long workspaceId);
}
