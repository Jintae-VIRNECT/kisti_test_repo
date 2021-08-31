package com.virnect.content.dao.project;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.content.domain.EditPermission;
import com.virnect.content.domain.Mode;
import com.virnect.content.domain.SharePermission;
import com.virnect.content.domain.TargetType;
import com.virnect.content.domain.project.Project;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-20
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface ProjectCustomRepository {
	Long getWorkspaceStorageSize(String projectUUID);

	Page<Project> getFilteredProjectPage(
		String workspaceUUID, List<SharePermission> sharePermissionList, List<EditPermission> editPermissionList,
		List<Mode> modeList, List<TargetType> targetTypeList, String search,  Pageable pageable
	);

	Page<Project> getProjectPageByProjectList(List<Project> projectList, Pageable pageable);
}
