package com.virnect.workspace.dto.response;

import com.virnect.workspace.domain.setting.SettingName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
    @ApiModelProperty(value = "변경 된 설정 이름", required = true, example = "")
    @NotNull
    private final SettingName settingName;

    @ApiModelProperty(value = "변경 된 일자", required = true, example = "")
    @NotNull
    private final LocalDateTime updatedDate;

}
