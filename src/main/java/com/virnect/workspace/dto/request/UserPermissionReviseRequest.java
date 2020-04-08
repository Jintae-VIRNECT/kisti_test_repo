package com.virnect.workspace.dto.request;

import com.virnect.workspace.dto.GroupInfoDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-03-02
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class UserPermissionReviseRequest {
    private String userId;
    private List<Long> workspacePermissions;
    private List<GroupInfoDTO> groups;
}
