package com.virnect.workspace.dao;

import com.virnect.workspace.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-13
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface GroupRepository extends JpaRepository<Group,Long> {
    Group findByName(String groupName);
}

