package com.virnect.workspace.dao;

import com.querydsl.core.types.Expression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.virnect.workspace.domain.QWorkspace;
import com.virnect.workspace.domain.QWorkspaceUser;
import com.virnect.workspace.domain.QWorkspaceUserPermission;
import com.virnect.workspace.dto.UserDTO;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-22
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@RequiredArgsConstructor

public class WorkspaceUserRepositoryImpl implements WorkspaceUserRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public long countWorkspaceUser(String workspaceId) {
        QWorkspaceUser qWorkspaceUser = QWorkspaceUser.workspaceUser;
        return jpaQueryFactory.select(qWorkspaceUser).from(qWorkspaceUser).where(qWorkspaceUser.workspace.uuid.eq(workspaceId)).fetchCount();
    }


}
