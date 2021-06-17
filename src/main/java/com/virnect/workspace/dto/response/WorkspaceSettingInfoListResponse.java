package com.virnect.workspace.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@RequiredArgsConstructor
@Getter
public class WorkspaceSettingInfoListResponse {
    @ApiModelProperty(value = "워크스페이스 설정 정보 목록", required = true, example = "")
    private final List<WorkspaceSettingInfoResponse> workspaceSettingInfoList;
}
