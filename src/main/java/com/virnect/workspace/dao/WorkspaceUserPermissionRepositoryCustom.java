package com.virnect.workspace.dao;

import com.virnect.workspace.domain.WorkspaceRole;
import com.virnect.workspace.domain.WorkspaceUser;
import com.virnect.workspace.domain.WorkspaceUserPermission;
import com.virnect.workspace.dto.MemberInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    List<MemberInfoDTO> findUserInfoListFilterd(List<MemberInfoDTO> memberListResponse, String workspaceId, String filter);

    long deleteAllWorkspaceUserPermissionByWorkspaceUser(List<WorkspaceUser> workspaceUserList);

    //Page<MemberInfoDTO> getMemberInfoList(List<String> filter, Pageable pageable, List<UserInfoRestResponse> userInfoRestResponseList);
    Page<WorkspaceUserPermission> getRoleFilteredUserList(String roleFilter, Pageable pageable, String workspaceId);
    Page<WorkspaceUserPermission> getContainedUserIdList(List<String> userIdList, Pageable pageable, String workspaceId);
    Page<WorkspaceUserPermission> getWorkspaceUserList(Pageable pageable, String workspaceId);
}
