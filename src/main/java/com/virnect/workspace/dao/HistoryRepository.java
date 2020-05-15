package com.virnect.workspace.dao;

import com.virnect.workspace.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Project: PF-Workspace
 * DATE: 2020-05-12
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface HistoryRepository extends JpaRepository<History,Long> {
}
