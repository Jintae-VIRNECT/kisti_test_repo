package com.virnect.workspace.application;

import com.virnect.workspace.dao.setting.WorkspaceCustomSettingRepository;
import com.virnect.workspace.dao.workspace.WorkspaceUserPermissionRepository;
import com.virnect.workspace.dao.workspace.WorkspaceUserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@RequiredArgsConstructor
@Component
@Aspect
public class WorkspaceSettingAspect {
    private final ModelMapper modelMapper;
    private final WorkspaceCustomSettingRepository workspaceCustomSettingRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final WorkspaceUserPermissionRepository workspaceUserPermissionRepository;
/*
    @Before("execution(* com.virnect.workspace.application.workspace.WorkspaceService.addWorkspaceSetting(..))")
    public void checkSettingOnWorkspaceInviteRequest(JoinPoint jp) {
        String workspaceId = "";
        String settingRole = "";
        for (Object o : jp.getArgs()) {
            if (o instanceof String) {
                workspaceId = o.toString();
                Optional<WorkspaceCustomSetting> workspaceCustomSettingOptional = workspaceCustomSettingRepository.findByWorkspace_UuidAndStatusAndSetting_Name(workspaceId, Status.ACTIVE, SettingName.WORKSPACE_INVITE_SETTING);
                if (workspaceCustomSettingOptional.isPresent()) {
                    SettingValue settingValue = workspaceCustomSettingOptional.get().getValue();
                    settingRole = settingValue.toString();
                }
            }
        }
        for (Object o : jp.getArgs()) {
            if (o instanceof WorkspaceInviteRequest) {
                WorkspaceInviteRequest workspaceInviteRequest = modelMapper.map(o, WorkspaceInviteRequest.class);
                WorkspaceUserPermission workspaceUserPermission = workspaceUserPermissionRepository.findWorkspaceUser(workspaceId, workspaceInviteRequest.getUserId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
                if (!workspaceUserPermission.getWorkspaceRole().getRole().equals(settingRole)) {
                    //throw
                }
            }
        }

    }*/
}
