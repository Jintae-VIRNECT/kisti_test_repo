package com.virnect.workspace.dto.request;

import com.virnect.workspace.domain.setting.SettingName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
public class WorkspaceSettingAddRequest {
    @ApiModelProperty(value = "추가 할 설정 이름", required = true, example = "")
    @NotNull
    private SettingName settingName;

}
