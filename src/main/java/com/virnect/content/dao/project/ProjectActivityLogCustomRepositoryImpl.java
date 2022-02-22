package com.virnect.content.dao.project;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.virnect.content.domain.project.Project;
import com.virnect.content.domain.project.ProjectActivityLog;
import com.virnect.content.domain.project.QProjectActivityLog;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-11-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class ProjectActivityLogCustomRepositoryImpl extends QuerydslRepositorySupport
	implements ProjectActivityLogCustomRepository {

	public ProjectActivityLogCustomRepositoryImpl() {
		super(ProjectActivityLog.class);
	}

	@Override
	public long deleteAllProjectActivityByProjectList(
		List<Project> projectList
	) {
		QProjectActivityLog qProjectActivityLog = QProjectActivityLog.projectActivityLog;
		return delete(qProjectActivityLog).where(qProjectActivityLog.project.in(projectList)).execute();
	}
}
