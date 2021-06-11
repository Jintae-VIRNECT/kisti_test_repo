package com.virnect.workspace.dto.request;

import com.virnect.workspace.domain.setting.SettingName;
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
public class WorkspaceSettingAddRequest {
    private List<SettingName> settingNameList;
}
