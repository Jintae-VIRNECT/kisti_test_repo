package com.virnect.content.dao.project;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.virnect.content.domain.project.Project;
import com.virnect.content.domain.project.ProjectEditUser;
import com.virnect.content.domain.project.QProjectEditUser;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-11-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class ProjectEditUserCustomRepositoryImpl extends QuerydslRepositorySupport
	implements ProjectEditUserCustomRepository {

	public ProjectEditUserCustomRepositoryImpl() {
		super(ProjectEditUser.class);
	}

	@Override
	public long deleteAllEditShareUserByProjectList(List<Project> projectList) {
		QProjectEditUser qProjectEditUser = QProjectEditUser.projectEditUser;
		return delete(qProjectEditUser).where(qProjectEditUser.project.in(projectList)).execute();
	}
}
