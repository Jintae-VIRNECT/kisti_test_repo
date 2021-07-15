package com.virnect.workspace.dto.request;

import com.virnect.workspace.domain.setting.SettingName;
import com.virnect.workspace.domain.setting.SettingValue;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-11
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class WorkspaceSettingUpdateInfoRequest {
    @ApiModelProperty(value = "변경 할 설정 이름", required = true, example = "")
    @NotNull
    private SettingName settingName;

    @ApiModelProperty(value = "변경 할 설정 값", required = true, example = "")
    @NotNull
    private SettingValue settingValue;

    @Override
    public String toString() {
        return "WorkspaceSettingUpdateInfo{" +
                "settingName=" + settingName +
                ", settingValue=" + settingValue +
                '}';
    }
}
