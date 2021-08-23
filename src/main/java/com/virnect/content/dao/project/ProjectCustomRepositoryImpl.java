package com.virnect.content.dao.project;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.CollectionUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;

import com.virnect.content.domain.EditPermission;
import com.virnect.content.domain.Mode;
import com.virnect.content.domain.SharePermission;
import com.virnect.content.domain.TargetType;
import com.virnect.content.domain.project.Project;
import com.virnect.content.domain.project.QProject;
import com.virnect.content.domain.project.QProjectMode;
import com.virnect.content.domain.project.QProjectTarget;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-20
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class ProjectCustomRepositoryImpl extends QuerydslRepositorySupport implements ProjectCustomRepository {
	public ProjectCustomRepositoryImpl() {
		super(ProjectRepository.class);
	}

	@Override
	public Long getWorkspaceStorageSize(String workspaceUUID) {
		QProject qProject = QProject.project;
		Long sumSize = from(qProject)
			.select(qProject.size.sum())
			.where(qProject.workspaceUUID.eq(workspaceUUID))
			.fetchOne();

		return sumSize;
	}

	@Override
	public List<Project> getFilteredProjectList(
		String workspaceUUID,
		List<SharePermission> sharePermissionList, List<EditPermission> editPermissionList, List<Mode> modeList,
		List<TargetType> targetTypeList
	) {
		QProject qProject = QProject.project;
		QProjectMode qProjectMode = QProjectMode.projectMode;
		QProjectTarget qProjectTarget = QProjectTarget.projectTarget;
		JPQLQuery<Project> query = from(qProject)
			.select(qProject)
			.join(qProject.projectModeList, qProjectMode)
			.join(qProject.projectTarget, qProjectTarget)
			.where(
				qProject.workspaceUUID.eq(workspaceUUID), eqSharePermission(sharePermissionList),
				eqEditPermission(editPermissionList), eqMode(modeList), eqTargetType(targetTypeList)
			);
		return query.fetch();
	}

	@Override
	public Page<Project> getProjectPageByProjectList(
		List<Project> projectList1, Pageable pageable
	) {
		return null;
	}

	private BooleanExpression eqTargetType(List<TargetType> targetTypeList) {
		if (!CollectionUtils.isEmpty(targetTypeList)) {
			return QProjectTarget.projectTarget.type.in(targetTypeList);
		}
		return null;
	}

	private BooleanExpression eqMode(List<Mode> modeList) {
		if (!CollectionUtils.isEmpty(modeList)) {
			return QProjectMode.projectMode.mode.in(modeList);
		}
		return null;
	}

	private BooleanExpression eqEditPermission(List<EditPermission> editPermissionList) {
		if (!CollectionUtils.isEmpty(editPermissionList)) {
			return QProject.project.editPermission.in(editPermissionList);
		}
		return null;
	}

	private BooleanExpression eqSharePermission(List<SharePermission> sharePermissionList) {
		if (!CollectionUtils.isEmpty(sharePermissionList)) {
			return QProject.project.sharePermission.in(sharePermissionList);
		}
		return null;
	}
}
