package com.virnect.workspace.dao;

import com.virnect.workspace.domain.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-08
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    Workspace findByUuid(String workspaceUUID);
}
