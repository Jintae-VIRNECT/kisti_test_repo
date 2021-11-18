package com.virnect.content.dao.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virnect.content.domain.project.Project;
import com.virnect.content.domain.project.ProjectActivityLog;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-11-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Repository
public interface ProjectActivityLogRepository
	extends JpaRepository<ProjectActivityLog, Long>, ProjectActivityLogCustomRepository {
	Page<ProjectActivityLog> findByProject(Project project, Pageable pageable);

}
