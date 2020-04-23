package com.virnect.workspace.dao;

import com.virnect.workspace.domain.Workspace;
import com.virnect.workspace.domain.WorkspaceUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-09
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface WorkspaceUserRepository extends JpaRepository<WorkspaceUser,Long>, WorkspaceUserRepositoryCustom{
    List<WorkspaceUser> findByWorkspace_Uuid(String workspaceId);
    Page<WorkspaceUser> findByUserId(String userId, Pageable pageable);
    WorkspaceUser findByUserIdAndWorkspace(String userId, Workspace workspace);
    List<WorkspaceUser> findTop4ByWorkspace_UuidOrderByCreatedDateDesc(String workspaceId);
}
