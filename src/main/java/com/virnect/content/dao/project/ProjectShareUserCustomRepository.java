package com.virnect.content.dao.project;

import java.util.List;

import com.virnect.content.domain.project.Project;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-11-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface ProjectShareUserCustomRepository {
	long deleteAllShareUserByProjectList(List<Project> projectList);
}
