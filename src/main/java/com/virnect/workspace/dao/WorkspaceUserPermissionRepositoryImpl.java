package com.virnect.workspace.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.virnect.workspace.domain.*;
import lombok.RequiredArgsConstructor;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-14
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@RequiredArgsConstructor
public class WorkspaceUserPermissionRepositoryImpl implements WorkspaceUserPermissionRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public WorkspaceRole findWorkspaceUserRole(String uuid, long workspaceId) {
        QWorkspaceUser qWorkspaceUser = QWorkspaceUser.workspaceUser;
        QWorkspaceUserPermission qWorkspaceUserPermission = QWorkspaceUserPermission.workspaceUserPermission;


        return jpaQueryFactory.select(QWorkspaceUserPermission.workspaceUserPermission.workspaceRole)
                .from(qWorkspaceUserPermission)
                .where(qWorkspaceUserPermission.workspaceUser
                        .eq(jpaQueryFactory.select(qWorkspaceUser).from(qWorkspaceUser)
                                .where(qWorkspaceUser.workspace.id.eq(workspaceId).and(qWorkspaceUser.userId.eq(uuid))).fetchOne()))
                .fetchOne();

    }
}
