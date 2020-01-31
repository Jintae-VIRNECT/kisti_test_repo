package com.virnect.workspace.dao;

import com.virnect.workspace.domain.WorkspaceRole;
import com.virnect.workspace.domain.WorkspaceUser;
import com.virnect.workspace.dto.UserDTO;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-14
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface WorkspaceUserPermissionRepositoryCustom {
    WorkspaceRole findWorkspaceUserRole(String workspaceId, String userId);
    List<UserDTO.UserInfoDTO> findUserInfoListFilterd(List<UserDTO.UserInfoDTO> userInfoDTOList, String workspaceId);

}
