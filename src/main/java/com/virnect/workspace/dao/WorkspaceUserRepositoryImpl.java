package com.virnect.workspace.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.virnect.workspace.domain.QWorkspaceUser;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-22
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkspaceUserRepositoryImpl implements WorkspaceUserRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public long countWorkspaceUser(String workspaceId) {
        QWorkspaceUser qWorkspaceUser = QWorkspaceUser.workspaceUser;
        return jpaQueryFactory.select(qWorkspaceUser).from(qWorkspaceUser).where(qWorkspaceUser.workspace.uuid.eq(workspaceId)).fetchCount();
    }


}
