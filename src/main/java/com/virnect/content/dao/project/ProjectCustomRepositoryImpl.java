package com.virnect.content.dao.project;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;

import com.virnect.content.domain.EditPermission;
import com.virnect.content.domain.SharePermission;
import com.virnect.content.domain.TargetType;
import com.virnect.content.domain.project.Project;
import com.virnect.content.domain.project.QProject;
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
		super(Project.class);
	}

	@Override
	public Long getWorkspaceStorageSize(String projectUUID) {
		QProject qProject = QProject.project;

		return from(qProject)
			.select(qProject.size.sum())
			.where(qProject.workspaceUUID.eq(projectUUID))
			.fetchOne();
	}

	@Override
	public Page<Project> getFilteredProjectPage(
		String workspaceUUID,
		List<SharePermission> sharePermissionList, List<EditPermission> editPermissionList, List<String> modeList,
		List<TargetType> targetTypeList, String search, Pageable pageable
	) {
		QProject qProject = QProject.project;
		JPQLQuery<Project> query = from(qProject)
			.select(qProject)
			.where(
				qProject.workspaceUUID.eq(workspaceUUID), eqSharePermission(sharePermissionList),
				eqEditPermission(editPermissionList), eqSearch(search), eqMode(modeList)
			);

		if (!CollectionUtils.isEmpty(targetTypeList)) {
			QProjectTarget qProjectTarget = QProjectTarget.projectTarget;
			query = query.join(qProject.projectTarget, qProjectTarget).where(qProjectTarget.type.in(targetTypeList));
		}

		List<Project> resultProjectList = getQuerydsl().applyPagination(pageable, query).fetch();

		return new PageImpl<>(resultProjectList, pageable, query.fetchCount());
	}

	private BooleanExpression eqMode(List<String> modeList) {
		if (!CollectionUtils.isEmpty(modeList)) {
			if (modeList.contains("2D")) {
				return QProject.project.mode2D.eq(true).and(QProject.project.mode3D.eq(false));
			}
			if (modeList.contains("3D")) {
				return QProject.project.mode2D.eq(false).and(QProject.project.mode3D.eq(true));
			}
			if (modeList.contains("2D3D")) {
				return QProject.project.mode2D.eq(true).and(QProject.project.mode3D.eq(true));
			}
		}
		return null;
	}

	private BooleanExpression eqSearch(String search) {
		if (StringUtils.hasText(search)) {
			return QProject.project.name.contains(search);
		}
		return null;
	}

	@Override
	public Page<Project> getProjectPageByProjectList(
		List<Project> projectList, Pageable pageable
	) {
		QProject qProject = QProject.project;
		JPQLQuery<Project> query = from(qProject)
			.select(qProject)
			.where(qProject.in(projectList));

		List<Project> resultProjectList = getQuerydsl().applyPagination(pageable, query).fetch();

		return new PageImpl<>(resultProjectList, pageable, query.fetchCount());

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

	@Override
	public long calculateTotalStorageAmountByWorkspaceId(String workspaceId) {
		QProject qProject = QProject.project;
		Optional<Long> calculateTotalUsedStorageAmount = Optional.ofNullable(
			from(qProject).select(qProject.size.sum()).where(qProject.workspaceUUID.eq(workspaceId)).fetchOne()
		);
		if (calculateTotalUsedStorageAmount.isPresent()) {
			long totalStorageUsage = calculateTotalUsedStorageAmount.get();
			totalStorageUsage /= 1024 * 1024;
			return totalStorageUsage;
		}
		return 0L;
	}
}
