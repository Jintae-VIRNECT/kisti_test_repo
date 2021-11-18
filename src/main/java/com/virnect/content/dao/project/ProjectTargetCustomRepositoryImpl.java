package com.virnect.content.dao.project;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.virnect.content.domain.project.Project;
import com.virnect.content.domain.project.ProjectTarget;
import com.virnect.content.domain.project.QProjectTarget;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-11-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class ProjectTargetCustomRepositoryImpl extends QuerydslRepositorySupport implements ProjectTargetCustomRepository{
	public ProjectTargetCustomRepositoryImpl() {
		super(ProjectTarget.class);
	}

	@Override
	public long deleteAllTargetByProjectList(List<Project> projectList) {
		QProjectTarget qProjectTarget = QProjectTarget.projectTarget;
		return delete(qProjectTarget).where(qProjectTarget.project.in(projectList)).execute();
	}
}
