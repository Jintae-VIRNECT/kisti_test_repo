package com.virnect.workspace.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class WorkspaceSettingUpdateResponse {
    @ApiModelProperty(value = "변경 성공 여부", required = true, example = "")
    private final boolean result;

    @ApiModelProperty(value = "변경 된 일자", required = true, example = "")
    private final LocalDateTime updatedDate;

    @ApiModelProperty(value = "변경 된 설정 정보", required = true, example = "")
    private final List<WorkspaceSettingInfoResponse> updatedWorkspaceSettingInfoList;

}
