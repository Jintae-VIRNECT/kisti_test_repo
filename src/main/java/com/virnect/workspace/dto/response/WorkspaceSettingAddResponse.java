package com.virnect.workspace.dto.response;

import com.virnect.workspace.domain.setting.SettingName;
import com.virnect.workspace.domain.setting.SettingValue;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
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
public class WorkspaceSettingAddResponse {
    private final List<AddedWorkspaceSettingInfo> addedWorkspaceSettingInfoList;

    @Getter
    @RequiredArgsConstructor
    public static class AddedWorkspaceSettingInfo {
        @ApiModelProperty(value = "추가 된 설정 이름", required = true, example = "")
        @NotNull
        private final SettingName settingName;

        @ApiModelProperty(value = "추가 할 설정 값", required = true, example = "")
        @NotNull
        private final SettingValue settingValue;
    }
}
