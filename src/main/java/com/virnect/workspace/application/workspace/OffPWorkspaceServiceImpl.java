package com.virnect.workspace.application.workspace;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.message.MessageRestService;
import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.dao.history.HistoryRepository;
import com.virnect.workspace.dao.workspace.*;
import com.virnect.workspace.dto.onpremise.*;
import com.virnect.workspace.global.common.RedirectProperty;
import com.virnect.workspace.global.common.mapper.rest.RestMapStruct;
import com.virnect.workspace.global.common.mapper.workspace.WorkspaceMapStruct;
import com.virnect.workspace.infra.file.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Slf4j
@Service
@Profile("!onpremise")
public class OffPWorkspaceServiceImpl extends WorkspaceService {


    public OffPWorkspaceServiceImpl(WorkspaceRepository workspaceRepository, WorkspaceUserRepository workspaceUserRepository, WorkspaceRoleRepository workspaceRoleRepository, WorkspacePermissionRepository workspacePermissionRepository, WorkspaceUserPermissionRepository workspaceUserPermissionRepository, UserRestService userRestService, MessageRestService messageRestService, FileService fileUploadService, SpringTemplateEngine springTemplateEngine, HistoryRepository historyRepository, MessageSource messageSource, LicenseRestService licenseRestService, RedirectProperty redirectProperty, RedisTemplate redisTemplate, WorkspaceMapStruct workspaceMapStruct, RestMapStruct restMapStruct) {
        super(workspaceRepository, workspaceUserRepository, workspaceRoleRepository, workspacePermissionRepository, workspaceUserPermissionRepository, userRestService, messageRestService, fileUploadService, springTemplateEngine, historyRepository, messageSource, licenseRestService, redirectProperty, redisTemplate, workspaceMapStruct, restMapStruct);
    }

    @Override
    public WorkspaceTitleUpdateResponse updateWorkspaceTitle(String workspaceId, WorkspaceTitleUpdateRequest workspaceTitleUpdateRequest) {
        return null;
    }

    @Override
    public WorkspaceLogoUpdateResponse updateWorkspaceLogo(String workspaceId, WorkspaceLogoUpdateRequest workspaceLogoUpdateRequest) {
        return null;
    }

    @Override
    public WorkspaceFaviconUpdateResponse updateWorkspaceFavicon(String workspaceId, WorkspaceFaviconUpdateRequest workspaceFaviconUpdateRequest) {
        return null;
    }

    @Override
    public WorkspaceCustomSettingResponse getWorkspaceCustomSetting() {
        return null;
    }

}


