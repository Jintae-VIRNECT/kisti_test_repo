package com.virnect.workspace.dao;

import com.virnect.workspace.domain.Workspace;
import com.virnect.workspace.domain.WorkspaceRole;
import com.virnect.workspace.domain.WorkspaceUser;
import com.virnect.workspace.domain.WorkspaceUserPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

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

    WorkspaceUserPermission findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(Workspace workspace, String userId);


    Page<WorkspaceUserPermission> findByWorkspaceUser_WorkspaceAndWorkspaceUserIsInAndWorkspaceRoleIsIn(Workspace workspace, List<WorkspaceUser> workspaceUserList, List<WorkspaceRole> workspaceRoleList, Pageable pageable);


    @Transactional
    void deleteAllByWorkspaceUser(WorkspaceUser workspaceUser);

    long countByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(Workspace workspace, String WorkspaceRole);

}
