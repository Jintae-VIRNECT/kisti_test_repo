package com.virnect.workspace.dao;

import com.querydsl.core.types.Expression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.virnect.workspace.domain.*;
import com.virnect.workspace.dto.UserDTO;
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
public class WorkspaceUserPermissionRepositoryImpl implements WorkspaceUserPermissionRepositoryCustom{
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
    public List<UserDTO.UserInfoDTO> findUserInfoListFilterd(List<UserDTO.UserInfoDTO> userInfoDTOList, String workspaceId) {

        QWorkspaceUserPermission qWorkspaceUserPermission = QWorkspaceUserPermission.workspaceUserPermission;
        List<UserDTO.UserInfoDTO> result = new ArrayList<>();

        for (UserDTO.UserInfoDTO userInfoDTO : userInfoDTOList) {
            String role = jpaQueryFactory.select(qWorkspaceUserPermission.workspaceRole.role)
                    .from(qWorkspaceUserPermission)
                    .where(qWorkspaceUserPermission.workspaceUser.workspace.uuid.eq(workspaceId)
                            .and(qWorkspaceUserPermission.workspaceUser.userId.eq(userInfoDTO.getUuid()))).fetchOne();
            if(role.equals("MASTER")) {
                result.add(userInfoDTO);
            }
        }
        return result;

    }
}
