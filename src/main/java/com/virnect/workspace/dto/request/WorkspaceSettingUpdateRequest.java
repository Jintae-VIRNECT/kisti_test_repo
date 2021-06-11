package com.virnect.workspace.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class WorkspaceSettingUpdateRequest {
    private List<WorkspaceSettingUpdateInfoRequest> workspaceSettingUpdateInfoList;

    @Override
    public String toString() {
        return "WorkspaceSettingUpdateRequest{" +
                "workspaceSettingUpdateInfoList=" + workspaceSettingUpdateInfoList +
                '}';
    }
}
