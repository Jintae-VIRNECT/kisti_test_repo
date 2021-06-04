package com.virnect.workspace.dao.setting;

import com.virnect.workspace.domain.setting.SettingName;
import com.virnect.workspace.domain.setting.Status;
import com.virnect.workspace.domain.setting.WorkspaceCustomSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface WorkspaceCustomSettingRepository extends JpaRepository<WorkspaceCustomSetting, Long> {
    List<WorkspaceCustomSetting> findByWorkspace_UuidAndStatus(String workspaceId, Status status);

    Optional<WorkspaceCustomSetting> findByWorkspace_UuidAndSetting_Name(String workspaceId, SettingName settingName);

    List<WorkspaceCustomSetting> findBySetting_Name(SettingName settingName);

    Optional<WorkspaceCustomSetting> findByWorkspace_UuidAndStatusAndSetting_Name(String workspaceId, Status status, SettingName settingName);
}
