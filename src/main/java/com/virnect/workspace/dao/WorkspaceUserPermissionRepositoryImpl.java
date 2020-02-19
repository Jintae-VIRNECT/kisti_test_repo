package com.virnect.workspace.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.virnect.workspace.domain.QWorkspaceUserPermission;
import com.virnect.workspace.domain.WorkspaceRole;
import com.virnect.workspace.dto.MemberInfoDTO;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-14
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@RequiredArgsConstructor
public class WorkspaceUserPermissionRepositoryImpl implements WorkspaceUserPermissionRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

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
                    if(filter.contains("MASTER")) resultList.add(memberInfo);
                    break;
                case "MANAGER":
                    if(filter.contains("MANAGER")) resultList.add(memberInfo);
                    break;
                case "MEMBER":
                    if(filter.contains("MEMBER")) resultList.add(memberInfo);
                    break;
                default:
            }
        }
        return resultList;
    }
}
