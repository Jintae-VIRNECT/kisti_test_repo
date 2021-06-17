package com.virnect.workspace.dto.response;

import com.virnect.workspace.domain.setting.SettingName;
import com.virnect.workspace.domain.setting.SettingValue;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-11
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class WorkspaceSettingUpdateInfoResponse {
    @ApiModelProperty(value = "워크스페이스 설정 id", required = true, example = "")
    private Long settingId;

    @ApiModelProperty(value = "워크스페이스 설정 이름", required = true, example = "")
    private SettingName settingName;

    @ApiModelProperty(value = "워크스페이스 설정 이름 설명", required = true, example = "")
    private String settingDescription;

    @ApiModelProperty(value = "워크스페이스 설정 값", required = true, example = "")
    private SettingValue settingValue;

    @ApiModelProperty(value = "워크스페이스 설정 추가 일자", required = true, example = "")
    private LocalDateTime createdDate;

    @ApiModelProperty(value = "워크스페이스 설정 수정 일자", required = true, example = "")
    private LocalDateTime updatedDate;
}
