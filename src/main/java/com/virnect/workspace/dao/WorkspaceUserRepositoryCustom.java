package com.virnect.workspace.dao;

import com.virnect.workspace.domain.Workspace;
import com.virnect.workspace.dto.UserDTO;

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
}
