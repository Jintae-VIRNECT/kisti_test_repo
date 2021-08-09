package com.virnect.workspace.application.workspace;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.dao.history.HistoryRepository;
import com.virnect.workspace.dao.setting.SettingRepository;
import com.virnect.workspace.dao.setting.WorkspaceCustomSettingRepository;
import com.virnect.workspace.dao.workspace.*;
import com.virnect.workspace.domain.workspace.*;
import com.virnect.workspace.dto.WorkspaceInfoDTO;
import com.virnect.workspace.dto.onpremise.*;
import com.virnect.workspace.dto.request.WorkspaceCreateRequest;
import com.virnect.workspace.dto.rest.UserInfoRestResponse;
import com.virnect.workspace.event.cache.UserWorkspacesDeleteEvent;
import com.virnect.workspace.event.mail.MailContextHandler;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.mapper.rest.RestMapStruct;
import com.virnect.workspace.global.common.mapper.workspace.WorkspaceMapStruct;
import com.virnect.workspace.global.constant.Permission;
import com.virnect.workspace.global.constant.UUIDType;
import com.virnect.workspace.global.error.ErrorCode;
import com.virnect.workspace.global.util.RandomStringTokenUtil;
import com.virnect.workspace.infra.file.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
public class OffWorkspaceServiceImpl extends WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceUserPermissionRepository workspaceUserPermissionRepository;
    private final FileService fileUploadService;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final WorkspaceSettingRepository workspaceSettingRepository;
    private final WorkspaceRoleRepository workspaceRoleRepository;
    private final WorkspaceMapStruct workspaceMapStruct;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final WorkspacePermissionRepository workspacePermissionRepository;

    @Value("${workspace.on-premise.max-have-workspace-amount}")
    private int maxHaveWorkspaceAmount;//최대 생성 가능한 워크스페이스 수

    public OffWorkspaceServiceImpl(WorkspaceRepository workspaceRepository, WorkspaceUserRepository workspaceUserRepository, WorkspaceUserPermissionRepository workspaceUserPermissionRepository, UserRestService userRestService, FileService fileUploadService, HistoryRepository historyRepository, LicenseRestService licenseRestService, WorkspaceMapStruct workspaceMapStruct, RestMapStruct restMapStruct, ApplicationEventPublisher applicationEventPublisher, SettingRepository settingRepository, WorkspaceCustomSettingRepository workspaceCustomSettingRepository, MailContextHandler mailContextHandler, WorkspaceSettingRepository workspaceSettingRepository, WorkspaceRoleRepository workspaceRoleRepository, WorkspacePermissionRepository workspacePermissionRepository) {
        super(workspaceRepository, workspaceUserRepository, workspaceUserPermissionRepository, userRestService, fileUploadService, historyRepository, licenseRestService, workspaceMapStruct, restMapStruct, applicationEventPublisher, settingRepository, workspaceCustomSettingRepository, mailContextHandler);
        this.workspaceRepository = workspaceRepository;
        this.workspaceUserPermissionRepository = workspaceUserPermissionRepository;
        this.fileUploadService = fileUploadService;
        this.workspaceUserRepository = workspaceUserRepository;
        this.workspaceSettingRepository = workspaceSettingRepository;
        this.workspaceRoleRepository = workspaceRoleRepository;
        this.workspaceMapStruct = workspaceMapStruct;
        this.applicationEventPublisher = applicationEventPublisher;
        this.workspacePermissionRepository = workspacePermissionRepository;
    }

    @Override
    public WorkspaceInfoDTO createWorkspace(WorkspaceCreateRequest workspaceCreateRequest) {
        //필수 값 체크
        if (!StringUtils.hasText(workspaceCreateRequest.getUserId()) || !StringUtils.hasText(
                workspaceCreateRequest.getName()) || !StringUtils.hasText(workspaceCreateRequest.getDescription())) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        //User Service 에서 유저 조회
        UserInfoRestResponse userInfoRestResponse = getUserInfo(workspaceCreateRequest.getUserId());
        //서브유저(유저가 만들어낸 유저)는 워크스페이스를 가질 수 없다.
        if (userInfoRestResponse == null || userInfoRestResponse.getUserType().equals("SUB_USER")) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        //사용자가 최대로 생성 가능한 워크스페이스 수를 넘겼는지 체크
        long userHasWorkspaceAmount = workspaceRepository.countByUserId(workspaceCreateRequest.getUserId());
        if (userHasWorkspaceAmount + 1 > maxHaveWorkspaceAmount) {
            log.error("[WORKSPACE CREATE] creatable maximum Workspace amount : [{}], current amount of workspace that user has : [{}].", maxHaveWorkspaceAmount, userHasWorkspaceAmount);
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_CREATE_MAX_CREATE);
        }
        //워크스페이스 생성
        String uuid = RandomStringTokenUtil.generate(UUIDType.UUID_WITH_SEQUENCE, 0);
        String pinNumber = RandomStringTokenUtil.generate(UUIDType.PIN_NUMBER, 0);
        String profile;
        if (workspaceCreateRequest.getProfile() != null) {
            try {
                profile = fileUploadService.upload(workspaceCreateRequest.getProfile());
            } catch (IOException e) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_CREATE_INVALID_PROFILE);
            }
        } else {
            profile = fileUploadService.getFileUrl("workspace-profile.png");
        }
        Workspace newWorkspace = Workspace.builder()
                .uuid(uuid)
                .userId(workspaceCreateRequest.getUserId())
                .name(workspaceCreateRequest.getName())
                .description(workspaceCreateRequest.getDescription())
                .profile(profile)
                .pinNumber(pinNumber)
                .build();
        workspaceRepository.save(newWorkspace);
        // 워크스페이스 소속 할당
        WorkspaceUser newWorkspaceUser = WorkspaceUser.builder()
                .userId(workspaceCreateRequest.getUserId())
                .workspace(newWorkspace)
                .build();
        workspaceUserRepository.save(newWorkspaceUser);
        // 워크스페이스 권한 할당
        WorkspaceRole workspaceRole = workspaceRoleRepository.findByRole(Role.MASTER).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_ROLE_NOT_FOUND));
        WorkspacePermission workspacePermission = workspacePermissionRepository.findById(Permission.ALL.getValue()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_PERMISSION_NOT_FOUND));
        WorkspaceUserPermission newWorkspaceUserPermission = WorkspaceUserPermission.builder()
                .workspaceRole(workspaceRole)
                .workspacePermission(workspacePermission)
                .workspaceUser(newWorkspaceUser)
                .build();
        workspaceUserPermissionRepository.save(newWorkspaceUserPermission);
        WorkspaceInfoDTO workspaceInfoDTO = workspaceMapStruct.workspaceToWorkspaceInfoDTO(newWorkspace);
        workspaceInfoDTO.setMasterUserId(newWorkspace.getUserId());
        applicationEventPublisher.publishEvent(new UserWorkspacesDeleteEvent(workspaceCreateRequest.getUserId()));// 캐싱 삭제
        return workspaceInfoDTO;
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
