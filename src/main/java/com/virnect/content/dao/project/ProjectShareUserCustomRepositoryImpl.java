package com.virnect.content.dao.project;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.virnect.content.domain.project.Project;
import com.virnect.content.domain.project.ProjectShareUser;
import com.virnect.content.domain.project.QProjectShareUser;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-11-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class ProjectShareUserCustomRepositoryImpl extends QuerydslRepositorySupport
	implements ProjectShareUserCustomRepository {

	public ProjectShareUserCustomRepositoryImpl() {
		super(ProjectShareUser.class);
	}

	@Override
	public long deleteAllShareUserByProjectList(List<Project> projectList) {
		QProjectShareUser qProjectShareUser = QProjectShareUser.projectShareUser;
		return delete(qProjectShareUser).where(qProjectShareUser.project.in(projectList)).execute();
	}
}
