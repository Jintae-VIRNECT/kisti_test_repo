package com.virnect.workspace.dao;

import com.virnect.workspace.domain.Workspace;
import com.virnect.workspace.domain.WorkspaceUser;
import com.virnect.workspace.domain.WorkspaceUserPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-14
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface WorkspaceUserPermissionRepository extends JpaRepository<WorkspaceUserPermission, Long>, WorkspaceUserPermissionRepositoryCustom {
    WorkspaceUserPermission findByWorkspaceUser(WorkspaceUser workspaceUser);

    Page<WorkspaceUserPermission> findByWorkspaceUser_WorkspaceAndWorkspaceUserIsInAndWorkspaceRole_Role(Workspace workspace, List<WorkspaceUser> workspaceUser, String role,Pageable pageable);

    //Page<WorkspaceUserPermission> findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(Workspace workspace, String userId, Pageable pageable);

}
