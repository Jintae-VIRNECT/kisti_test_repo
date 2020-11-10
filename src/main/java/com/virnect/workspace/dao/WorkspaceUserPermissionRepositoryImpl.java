package com.virnect.workspace.dao;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.virnect.workspace.domain.*;
import com.virnect.workspace.dto.MemberInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-14
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class WorkspaceUserPermissionRepositoryImpl extends QuerydslRepositorySupport implements WorkspaceUserPermissionRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public WorkspaceUserPermissionRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        super(WorkspaceUserPermission.class);
        this.jpaQueryFactory=jpaQueryFactory;
    }

    @Override
    public WorkspaceRole findWorkspaceUserRole(String workspaceId, String userId) {
        QWorkspaceUserPermission qWorkspaceUserPermission = QWorkspaceUserPermission.workspaceUserPermission;

        return jpaQueryFactory.select(QWorkspaceUserPermission.workspaceUserPermission.workspaceRole)
                .from(qWorkspaceUserPermission)
                .where(qWorkspaceUserPermission.workspaceUser.workspace.uuid.eq(workspaceId)
                        .and(qWorkspaceUserPermission.workspaceUser.userId.eq(userId))).fetchOne();
    }

    @Override
    public List<MemberInfoDTO> findUserInfoListFilterd(List<MemberInfoDTO> memberInfoList, String workspaceId, String filter) {

        QWorkspaceUserPermission qWorkspaceUserPermission = QWorkspaceUserPermission.workspaceUserPermission;
        List<MemberInfoDTO> resultList = new ArrayList<>();

        for (MemberInfoDTO memberInfo : memberInfoList) {
            String role = jpaQueryFactory.select(qWorkspaceUserPermission.workspaceRole.role)
                    .from(qWorkspaceUserPermission)
                    .where(qWorkspaceUserPermission.workspaceUser.workspace.uuid.eq(workspaceId)
                            .and(qWorkspaceUserPermission.workspaceUser.userId.eq(memberInfo.getUuid()))).fetchOne();
            switch (role) {
                case "MASTER":
                    if (filter.contains("MASTER") || filter.contains("ALL")) resultList.add(memberInfo);
                    break;
                case "MANAGER":
                    if (filter.contains("MANAGER") || filter.contains("ALL")) resultList.add(memberInfo);
                    break;
                case "MEMBER":
                    if (filter.contains("MEMBER") || filter.contains("ALL")) resultList.add(memberInfo);
                    break;
                default:
            }

        }
        return resultList;
    }

    @Override
    public long deleteAllWorkspaceUserPermissionByWorkspaceUser(List<WorkspaceUser> workspaceUserList) {
        QWorkspaceUserPermission qWorkspaceUserPermission = QWorkspaceUserPermission.workspaceUserPermission;
        return jpaQueryFactory.delete(qWorkspaceUserPermission).where(qWorkspaceUserPermission.workspaceUser.in(workspaceUserList)).execute();
    }

    @Override
    public Page<WorkspaceUserPermission> getRoleFilteredUserList(String roleFilter, Pageable pageable, String workspaceId) {
        QWorkspaceUserPermission qWorkspaceUserPermission = QWorkspaceUserPermission.workspaceUserPermission;

        //1. 필터
        List<WorkspaceRole> roleList = new ArrayList<>();
        if (roleFilter.contains("MASTER")) {
            roleList.add(WorkspaceRole.builder().id(1L).build());
        }
        if (roleFilter.contains("MANAGER")) {
            roleList.add(WorkspaceRole.builder().id(2L).build());
        }
        if (roleFilter.contains("MEMBER")) {
            roleList.add(WorkspaceRole.builder().id(3L).build());
        }

        JPQLQuery<WorkspaceUserPermission> query = jpaQueryFactory
                .select(qWorkspaceUserPermission)
                .from(qWorkspaceUserPermission)
                .where(qWorkspaceUserPermission.workspaceUser.workspace.uuid.eq(workspaceId).and(qWorkspaceUserPermission.workspaceRole.in(roleList)))
                .fetchAll();

        List<WorkspaceUserPermission> workspaceUserPermissionList = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl<>(workspaceUserPermissionList, pageable, query.fetchCount());
    }

    @Override
    public Page<WorkspaceUserPermission> getContainedUserIdList(List<String> userIdList, Pageable pageable, String workspaceId) {
        QWorkspaceUserPermission qWorkspaceUserPermission = QWorkspaceUserPermission.workspaceUserPermission;
        JPQLQuery<WorkspaceUserPermission> query = jpaQueryFactory
                .select(qWorkspaceUserPermission)
                .from(qWorkspaceUserPermission)
                .where(qWorkspaceUserPermission.workspaceUser.workspace.uuid.eq(workspaceId).and(qWorkspaceUserPermission.workspaceUser.userId.in(userIdList)))
                .fetchAll();
        List<WorkspaceUserPermission> workspaceUserPermissionList = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl<>(workspaceUserPermissionList, pageable, query.fetchCount());
    }

    @Override
    public Page<WorkspaceUserPermission> getWorkspaceUserList(Pageable pageable, String workspaceId) {
        QWorkspaceUserPermission qWorkspaceUserPermission = QWorkspaceUserPermission.workspaceUserPermission;
        JPQLQuery<WorkspaceUserPermission> query = jpaQueryFactory
                .select(qWorkspaceUserPermission)
                .from(qWorkspaceUserPermission)
                .where(qWorkspaceUserPermission.workspaceUser.workspace.uuid.eq(workspaceId))
                .fetchAll();
        List<WorkspaceUserPermission> workspaceUserPermissionList = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl<>(workspaceUserPermissionList, pageable, query.fetchCount());
    }
}
