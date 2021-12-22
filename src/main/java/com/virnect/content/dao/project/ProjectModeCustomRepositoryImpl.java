package com.virnect.content.dao.project;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.virnect.content.dao.project.ProjectModeCustomRepository;
import com.virnect.content.domain.project.Project;
import com.virnect.content.domain.project.ProjectMode;
import com.virnect.content.domain.project.QProjectMode;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-11-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class ProjectModeCustomRepositoryImpl extends QuerydslRepositorySupport implements ProjectModeCustomRepository {
	public ProjectModeCustomRepositoryImpl() {
		super(ProjectMode.class);
	}

	@Override
	public long deleteAllModeByProjectList(List<Project> projectList) {
		QProjectMode qProjectMode = QProjectMode.projectMode;
		return delete(qProjectMode).where(qProjectMode.project.in(projectList)).execute();
	}
}
