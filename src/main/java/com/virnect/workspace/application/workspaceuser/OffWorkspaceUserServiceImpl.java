package com.virnect.workspace.application.workspaceuser;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.dao.setting.WorkspaceCustomSettingRepository;
import com.virnect.workspace.dao.workspace.*;
import com.virnect.workspace.dto.request.WorkspaceInviteRequest;
import com.virnect.workspace.event.mail.MailContextHandler;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.common.mapper.rest.RestMapStruct;
import com.virnect.workspace.global.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Locale;

/**
 * Project: PF-Workspace
 * DATE: 2021-05-13
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@Profile("onpremise")
public class OffWorkspaceUserServiceImpl extends WorkspaceUserService {
    private static final String serviceID = "workspace-server";
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final WorkspaceRoleRepository workspaceRoleRepository;
    private final WorkspacePermissionRepository workspacePermissionRepository;
    private final WorkspaceUserPermissionRepository workspaceUserPermissionRepository;
    private final UserRestService userRestService;
    private final LicenseRestService licenseRestService;
    private final RestMapStruct restMapStruct;
    private final WorkspaceCustomSettingRepository workspaceCustomSettingRepository;

    public OffWorkspaceUserServiceImpl(WorkspaceRepository workspaceRepository, WorkspaceUserRepository workspaceUserRepository, WorkspaceRoleRepository workspaceRoleRepository, WorkspaceUserPermissionRepository workspaceUserPermissionRepository, UserRestService userRestService, MessageSource messageSource, LicenseRestService licenseRestService, RestMapStruct restMapStruct, ApplicationEventPublisher applicationEventPublisher, WorkspaceCustomSettingRepository workspaceCustomSettingRepository, MailContextHandler mailContextHandler, WorkspacePermissionRepository workspacePermissionRepository) {
        super(workspaceRepository, workspaceUserRepository, workspaceRoleRepository, workspaceUserPermissionRepository, userRestService, messageSource, licenseRestService, restMapStruct, applicationEventPublisher, workspaceCustomSettingRepository, mailContextHandler, workspacePermissionRepository);
        this.workspaceRepository = workspaceRepository;
        this.workspaceUserRepository = workspaceUserRepository;
        this.workspaceRoleRepository = workspaceRoleRepository;
        this.workspacePermissionRepository = workspacePermissionRepository;
        this.workspaceUserPermissionRepository = workspaceUserPermissionRepository;
        this.userRestService = userRestService;
        this.licenseRestService = licenseRestService;
        this.restMapStruct = restMapStruct;
        this.workspaceCustomSettingRepository = workspaceCustomSettingRepository;
    }

    @Override
    public ApiResponse<Boolean> inviteWorkspace(String workspaceId, WorkspaceInviteRequest workspaceInviteRequest, Locale locale) {
        return null;
    }

    @Override
    public RedirectView inviteWorkspaceAccept(String sessionCode, String lang) throws IOException {
        return null;
    }

    @Override
    public RedirectView inviteWorkspaceReject(String sessionCode, String lang) {
        return null;
    }


    private void checkFileSize(long requestSize) {
        if (requestSize < 0 || requestSize > (long) 3145728) {
            log.error(
                    "[UPLOAD FILE SIZE CHECK] Acceptable File size : [{}], Present File size : [{}] ",
                    3145728, requestSize
            );
            throw new WorkspaceException(ErrorCode.ERR_NOT_ALLOW_FILE_SIZE);
        }
    }

    private void checkFileExtension(String requestExtension, String allowExtension) {
        if (!StringUtils.hasText(requestExtension) || !allowExtension.contains(requestExtension.toLowerCase())) {
            log.error(
                    "[UPLOAD FILE EXTENSION CHECK] Acceptable File extension : [{}], Present File extension : [{}] ",
                    allowExtension, requestExtension
            );
            throw new WorkspaceException(ErrorCode.ERR_NOT_ALLOW_FILE_EXTENSION);
        }
    }
}
