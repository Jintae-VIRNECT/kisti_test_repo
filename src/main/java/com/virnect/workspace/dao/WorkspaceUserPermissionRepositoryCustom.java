package com.virnect.workspace.dao;

import com.virnect.workspace.domain.WorkspaceRole;
import com.virnect.workspace.domain.WorkspaceUser;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-14
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface WorkspaceUserPermissionRepositoryCustom {
    WorkspaceRole findWorkspaceUserRole(WorkspaceUser workspaceUser);
}
