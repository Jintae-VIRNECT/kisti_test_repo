package com.virnect.workspace.application.workspace;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.message.MessageRestService;
import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.dao.history.HistoryRepository;
import com.virnect.workspace.dao.workspace.*;
import com.virnect.workspace.domain.workspace.Workspace;
import com.virnect.workspace.domain.workspace.WorkspaceSetting;
import com.virnect.workspace.domain.workspace.WorkspaceUserPermission;
import com.virnect.workspace.dto.onpremise.*;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.RedirectProperty;
import com.virnect.workspace.global.common.mapper.rest.RestMapStruct;
import com.virnect.workspace.global.common.mapper.workspace.WorkspaceMapStruct;
import com.virnect.workspace.global.error.ErrorCode;
import com.virnect.workspace.infra.file.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2021-02-02
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@Profile("onpremise")
public class OnPWorkspaceServiceImpl extends WorkspaceService {
    private static final String serviceID = "workspace-server";
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceUserPermissionRepository workspaceUserPermissionRepository;
    private final FileService fileUploadService;
    private final WorkspaceSettingRepository workspaceSettingRepository;

    public OnPWorkspaceServiceImpl(WorkspaceRepository workspaceRepository, WorkspaceUserRepository workspaceUserRepository, WorkspaceRoleRepository workspaceRoleRepository, WorkspacePermissionRepository workspacePermissionRepository, WorkspaceUserPermissionRepository workspaceUserPermissionRepository, UserRestService userRestService, MessageRestService messageRestService, FileService fileUploadService, SpringTemplateEngine springTemplateEngine, HistoryRepository historyRepository, MessageSource messageSource, LicenseRestService licenseRestService, RedirectProperty redirectProperty, WorkspaceMapStruct workspaceMapStruct, RestMapStruct restMapStruct, ApplicationEventPublisher applicationEventPublisher, WorkspaceSettingRepository workspaceSettingRepository) {
        super(workspaceRepository, workspaceUserRepository, workspaceRoleRepository, workspacePermissionRepository, workspaceUserPermissionRepository, userRestService, messageRestService, fileUploadService, springTemplateEngine, historyRepository, messageSource, licenseRestService, redirectProperty, workspaceMapStruct, restMapStruct, applicationEventPublisher);
        this.workspaceRepository = workspaceRepository;
        this.workspaceUserPermissionRepository = workspaceUserPermissionRepository;
        this.fileUploadService = fileUploadService;
        this.workspaceSettingRepository = workspaceSettingRepository;
    }


    public WorkspaceFaviconUpdateResponse updateWorkspaceFavicon(
            String workspaceId, WorkspaceFaviconUpdateRequest workspaceFaviconUpdateRequest
    ) {
        //1. 권한 체크
        Workspace workspace = checkWorkspaceAndUserRole(workspaceId, workspaceFaviconUpdateRequest.getUserId(), new String[]{"MASTER"});
        List<WorkspaceSetting> workspaceSettingList = workspaceSettingRepository.findAll();
        WorkspaceSetting workspaceSetting = workspaceSettingList.stream().findFirst()
                .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));

        //2. 파비콘 확장자, 사이즈 체크
        if (workspaceFaviconUpdateRequest.getFavicon() == null) {
            String favicon = fileUploadService.getFileUrl("virnect-default-favicon.ico");
            workspaceSetting.setFavicon(favicon);
            workspaceSettingRepository.save(workspaceSetting);
            WorkspaceFaviconUpdateResponse workspaceFaviconUpdateResponse = new WorkspaceFaviconUpdateResponse();
            workspaceFaviconUpdateResponse.setResult(true);
            workspaceFaviconUpdateResponse.setFavicon(favicon);
            return workspaceFaviconUpdateResponse;
        }
        String allowExtension = "jpg,jpeg,ico,png";
        String extension = FilenameUtils.getExtension(workspaceFaviconUpdateRequest.getFavicon().getOriginalFilename());
        checkFileSize(workspaceFaviconUpdateRequest.getFavicon().getSize());
        checkFileExtension(extension, allowExtension);

        //3. 파비콘 업로드
        try {
            String favicon = fileUploadService.upload(workspaceFaviconUpdateRequest.getFavicon());
            workspaceSetting.setFavicon(favicon);
            workspaceSettingRepository.save(workspaceSetting);

            WorkspaceFaviconUpdateResponse workspaceFaviconUpdateResponse = new WorkspaceFaviconUpdateResponse();
            workspaceFaviconUpdateResponse.setResult(true);
            workspaceFaviconUpdateResponse.setFavicon(favicon);
            return workspaceFaviconUpdateResponse;
        } catch (IOException e) {
            log.error("[UPDATE WORKSPACE FAVICON] Favicon Image upload fail. Error message >> [{}]", e.getMessage());
            WorkspaceFaviconUpdateResponse workspaceFaviconUpdateResponse = new WorkspaceFaviconUpdateResponse();
            workspaceFaviconUpdateResponse.setResult(false);
            return workspaceFaviconUpdateResponse;
        }
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

    public WorkspaceLogoUpdateResponse updateWorkspaceLogo(
            String workspaceId, WorkspaceLogoUpdateRequest workspaceLogoUpdateRequest
    ) {
        //1. 권한 체크
        Workspace workspace = checkWorkspaceAndUserRole(
                workspaceId, workspaceLogoUpdateRequest.getUserId(), new String[]{"MASTER"});
        List<WorkspaceSetting> workspaceSettingList = workspaceSettingRepository.findAll();
        WorkspaceSetting workspaceSetting = workspaceSettingList.stream().findFirst()
                .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));

        //2. 로고 확장자, 사이즈 체크
        String allowExtension = "jpg,jpeg,gif,png";

        //3. default logo 업로드
        if (workspaceLogoUpdateRequest.getDefaultLogo() != null) {
            String defaultExtension = FilenameUtils.getExtension(
                    workspaceLogoUpdateRequest.getDefaultLogo().getOriginalFilename());
            checkFileSize(workspaceLogoUpdateRequest.getDefaultLogo().getSize());
            checkFileExtension(defaultExtension, allowExtension);

            try {
                String logo = fileUploadService.upload(workspaceLogoUpdateRequest.getDefaultLogo());
                workspaceSetting.setDefaultLogo(logo);
            } catch (IOException e) {
                log.error("[UPDATE WORKSPACE LOGO] Logo Image upload fail. Error message >> [{}]", e.getMessage());
                WorkspaceLogoUpdateResponse workspaceLogoUpdateResponse = new WorkspaceLogoUpdateResponse();
                workspaceLogoUpdateResponse.setResult(false);
                return workspaceLogoUpdateResponse;
            }
        } else {
            String logoDefault = fileUploadService.getFileUrl("virnect-default-logo.png");
            workspaceSetting.setDefaultLogo(logoDefault);
            workspaceSettingRepository.save(workspaceSetting);

        }

        //4. grey logo 업로드
        if (workspaceLogoUpdateRequest.getGreyLogo() != null) {
            String greyExtension = FilenameUtils.getExtension(
                    workspaceLogoUpdateRequest.getGreyLogo().getOriginalFilename());
            checkFileSize(workspaceLogoUpdateRequest.getGreyLogo().getSize());
            checkFileExtension(greyExtension, allowExtension);

            try {
                String logo = fileUploadService.upload(workspaceLogoUpdateRequest.getGreyLogo());
                workspaceSetting.setGreyLogo(logo);
            } catch (IOException e) {
                log.error("[UPDATE WORKSPACE LOGO] Logo Image upload fail. Error message >> [{}]", e.getMessage());
                WorkspaceLogoUpdateResponse workspaceLogoUpdateResponse = new WorkspaceLogoUpdateResponse();
                workspaceLogoUpdateResponse.setResult(false);
                return workspaceLogoUpdateResponse;
            }
        }

        //4. white logo 업로드
        if (workspaceLogoUpdateRequest.getWhiteLogo() != null) {
            String whiteExtension = FilenameUtils.getExtension(workspaceLogoUpdateRequest.getWhiteLogo().getOriginalFilename());
            checkFileSize(workspaceLogoUpdateRequest.getWhiteLogo().getSize());
            checkFileExtension(whiteExtension, allowExtension);

            try {
                String logo = fileUploadService.upload(workspaceLogoUpdateRequest.getWhiteLogo());
                workspaceSetting.setWhiteLogo(logo);
            } catch (IOException e) {
                log.error("[UPDATE WORKSPACE LOGO] Logo Image upload fail. Error message >> [{}]", e.getMessage());
                WorkspaceLogoUpdateResponse workspaceLogoUpdateResponse = new WorkspaceLogoUpdateResponse();
                workspaceLogoUpdateResponse.setResult(false);
                return workspaceLogoUpdateResponse;
            }
        } else {
            String logoWhite = fileUploadService.getFileUrl("virnect-white-logo.png");
            workspaceSetting.setWhiteLogo(logoWhite);
        }

        workspaceSettingRepository.save(workspaceSetting);

        WorkspaceLogoUpdateResponse workspaceLogoUpdateResponse = new WorkspaceLogoUpdateResponse();
        workspaceLogoUpdateResponse.setResult(true);
        workspaceLogoUpdateResponse.setDefaultLogo(workspaceSetting.getDefaultLogo());
        workspaceLogoUpdateResponse.setGreyLogo(workspaceSetting.getGreyLogo());
        workspaceLogoUpdateResponse.setWhiteLogo(workspaceSetting.getWhiteLogo());
        return workspaceLogoUpdateResponse;
    }

    public WorkspaceTitleUpdateResponse updateWorkspaceTitle(
            String workspaceId, WorkspaceTitleUpdateRequest workspaceTitleUpdateRequest
    ) {
        //1. 권한 체크
        Workspace workspace = checkWorkspaceAndUserRole(
                workspaceId, workspaceTitleUpdateRequest.getUserId(), new String[]{"MASTER"});
        List<WorkspaceSetting> workspaceSettingList = workspaceSettingRepository.findAll();
        WorkspaceSetting workspaceSetting = workspaceSettingList.stream().findFirst()
                .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));

        //2. 고객사명 변경
        workspaceSetting.setTitle(workspaceTitleUpdateRequest.getTitle());
        workspaceSettingRepository.save(workspaceSetting);

        WorkspaceTitleUpdateResponse workspaceTitleUpdateResponse = new WorkspaceTitleUpdateResponse();
        workspaceTitleUpdateResponse.setResult(true);
        workspaceTitleUpdateResponse.setTitle(workspaceSetting.getTitle());
        return workspaceTitleUpdateResponse;
    }

    public WorkspaceCustomSettingResponse getWorkspaceCustomSetting() {
        List<WorkspaceSetting> workspaceSettingList = workspaceSettingRepository.findAll();

        WorkspaceSetting workspaceSetting = workspaceSettingList.stream().findFirst()
                .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));

        WorkspaceCustomSettingResponse workspaceCustomSettingResponse = new WorkspaceCustomSettingResponse();
        workspaceCustomSettingResponse.setWorkspaceTitle(workspaceSetting.getTitle());
        workspaceCustomSettingResponse.setDefaultLogo(workspaceSetting.getDefaultLogo());
        workspaceCustomSettingResponse.setGreyLogo(workspaceSetting.getGreyLogo());
        workspaceCustomSettingResponse.setWhiteLogo(workspaceSetting.getWhiteLogo());
        workspaceCustomSettingResponse.setFavicon(workspaceSetting.getFavicon());

        return workspaceCustomSettingResponse;
    }

    private Workspace checkWorkspaceAndUserRole(String workspaceId, String userId, String[] role) {
        Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        WorkspaceUserPermission workspaceUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, userId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));

        log.info(
                "[CHECK WORKSPACE USER ROLE] Acceptable User Workspace Role : {}, Present User Role : [{}]",
                Arrays.toString(role),
                workspaceUserPermission.getWorkspaceRole().getRole()
        );
        if (Arrays.stream(role).noneMatch(workspaceUserPermission.getWorkspaceRole().getRole()::equals)) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }
        return workspace;
    }
}
