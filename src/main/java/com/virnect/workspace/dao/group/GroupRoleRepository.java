package com.virnect.workspace.dao.group;

import com.virnect.workspace.domain.GroupRole;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-30
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface GroupRoleRepository extends JpaRepository<GroupRole, Long>  {
    GroupRole findByRole(String role);
}
