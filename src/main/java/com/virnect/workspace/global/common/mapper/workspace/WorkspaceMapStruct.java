package com.virnect.workspace.global.common.mapper.workspace;

import com.virnect.workspace.domain.setting.Setting;
import com.virnect.workspace.domain.setting.WorkspaceCustomSetting;
import com.virnect.workspace.domain.workspace.Workspace;
import com.virnect.workspace.dto.response.WorkspaceInfoDTO;
import com.virnect.workspace.dto.response.SettingInfoResponse;
import com.virnect.workspace.dto.response.WorkspaceInfoListResponse;
import com.virnect.workspace.dto.response.WorkspaceSettingInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Project: PF-Workspace
 * DATE: 2021-05-17
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Mapper(componentModel = "spring")
public interface WorkspaceMapStruct {

    @Mapping(target = "masterUserId", source = "userId")
    WorkspaceInfoDTO workspaceToWorkspaceInfoDTO(Workspace workspace);

    WorkspaceInfoListResponse.WorkspaceInfo workspaceToWorkspaceInfo(Workspace workspace);

    @Mapping(target = "settingId", expression = "java(workspaceCustomSetting.getSetting().getId())")
    @Mapping(target = "settingName", expression = "java(workspaceCustomSetting.getSetting().getName())")
    @Mapping(target = "settingDescription", expression = "java(workspaceCustomSetting.getSetting().getDescription())")
    @Mapping(target = "settingValue", source = "value")
    @Mapping(target = "paymentType", expression = "java(workspaceCustomSetting.getSetting().getPaymentType())")
    WorkspaceSettingInfoResponse workspaceCustomSettingToWorkspaceSettingInfoResponse(WorkspaceCustomSetting workspaceCustomSetting);


    @Mapping(target = "settingName", source = "name")
    @Mapping(target = "settingDescription", source = "description")
    SettingInfoResponse settingToSettingInfoResponse(Setting setting);
}
