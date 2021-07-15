package com.virnect.workspace.dao.workspace;

import com.virnect.workspace.domain.workspace.WorkspaceRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-14
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface WorkspaceRoleRepository extends JpaRepository<WorkspaceRole, Long> {
    Optional<WorkspaceRole> findByRole(String roleName);
}
