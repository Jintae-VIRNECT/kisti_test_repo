package com.virnect.workspace.dto.response;

import com.virnect.workspace.dto.UserInfoDTO;
import com.virnect.workspace.dto.WorkspaceInfoDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@RequiredArgsConstructor
@Getter
public class WorkspaceInfoResponse {
    private final WorkspaceInfoDTO workspaceInfo;
    private final List<UserInfoDTO> workspaceUserInfo;
    private final long masterUserCount;
    private final long manageUserCount;
    private final long memberUserCount;
    private final int remotePlanCount;
    private final int makePlanCount;
    private final int viewPlanCount;
}
