package com.virnect.workspace.dao.workspace;

import com.virnect.workspace.domain.workspace.WorkspaceUser;
import com.virnect.workspace.domain.workspace.WorkspaceUserPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-14
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface WorkspaceUserPermissionRepositoryCustom {
    long deleteAllWorkspaceUserPermissionByWorkspaceUser(List<WorkspaceUser> workspaceUserList);

    Page<WorkspaceUserPermission> getRoleFilteredUserList(List<WorkspaceUserPermission> workspaceUserPermissionList, List<String> roleList, Pageable pageable, String workspaceId);

    Page<WorkspaceUserPermission> getContainedUserIdList(List<String> userIdList, Pageable pageable, String workspaceId);

    Page<WorkspaceUserPermission> getWorkspaceUserList(Pageable pageable, String workspaceId);

    List<WorkspaceUserPermission> findRecentWorkspaceUserList(int size, String workspaceId);

    Optional<WorkspaceUserPermission> findWorkspaceUser(String workspaceId, String userId);

    Page<WorkspaceUserPermission> getWorkspaceUserPermissionByInUserListAndEqRole(List<String> userIdList, String filter, Pageable newPageable, String workspaceId);
}
