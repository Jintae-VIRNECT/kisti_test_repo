package com.virnect.workspace.application.workspace;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.dao.history.HistoryRepository;
import com.virnect.workspace.dao.setting.SettingRepository;
import com.virnect.workspace.dao.setting.WorkspaceCustomSettingRepository;
import com.virnect.workspace.dao.workspace.*;
import com.virnect.workspace.dao.workspacepermission.WorkspacePermissionRepository;
import com.virnect.workspace.dao.workspacerole.WorkspaceRoleRepository;
import com.virnect.workspace.dao.workspaceuser.WorkspaceUserRepository;
import com.virnect.workspace.dao.workspaceuserpermission.WorkspaceUserPermissionRepository;
import com.virnect.workspace.domain.workspace.*;
import com.virnect.workspace.dto.response.WorkspaceInfoDTO;
import com.virnect.workspace.dto.request.WorkspaceCreateRequest;
import com.virnect.workspace.application.user.dto.response.UserInfoRestResponse;
import com.virnect.workspace.dto.request.WorkspaceFaviconUpdateRequest;
import com.virnect.workspace.dto.request.WorkspaceLogoUpdateRequest;
import com.virnect.workspace.dto.request.WorkspaceTitleUpdateRequest;
import com.virnect.workspace.dto.response.WorkspaceCustomSettingResponse;
import com.virnect.workspace.dto.response.WorkspaceFaviconUpdateResponse;
import com.virnect.workspace.dto.response.WorkspaceLogoUpdateResponse;
import com.virnect.workspace.dto.response.WorkspaceTitleUpdateResponse;
import com.virnect.workspace.event.message.MailContextHandler;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.mapper.rest.RestMapStruct;
import com.virnect.workspace.global.common.mapper.workspace.WorkspaceMapStruct;
import com.virnect.workspace.global.constant.Permission;
import com.virnect.workspace.global.constant.UUIDType;
import com.virnect.workspace.global.error.ErrorCode;
import com.virnect.workspace.global.util.RandomStringTokenUtil;
import com.virnect.workspace.infra.file.DefaultImageFile;
import com.virnect.workspace.infra.file.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Profile("!onpremise")
public class OnWorkspaceServiceImpl extends WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final FileService fileUploadService;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final WorkspaceRoleRepository workspaceRoleRepository;
    private final WorkspaceUserPermissionRepository workspaceUserPermissionRepository;
    private final WorkspacePermissionRepository workspacePermissionRepository;
    private final WorkspaceMapStruct workspaceMapStruct;
    private final ApplicationEventPublisher applicationEventPublisher;

    private static final int MAX_HAVE_WORKSPACE_AMOUNT = 49; //최대 생성 가능한 워크스페이스 수

    public OnWorkspaceServiceImpl(WorkspaceRepository workspaceRepository, WorkspaceUserRepository workspaceUserRepository, WorkspaceUserPermissionRepository workspaceUserPermissionRepository, UserRestService userRestService, FileService fileUploadService, HistoryRepository historyRepository, LicenseRestService licenseRestService, WorkspaceMapStruct workspaceMapStruct, RestMapStruct restMapStruct, ApplicationEventPublisher applicationEventPublisher, SettingRepository settingRepository, WorkspaceCustomSettingRepository workspaceCustomSettingRepository, MailContextHandler mailContextHandler, WorkspaceRoleRepository workspaceRoleRepository, WorkspacePermissionRepository workspacePermissionRepository) {
        super(workspaceRepository, workspaceUserRepository, workspaceUserPermissionRepository, userRestService, fileUploadService, historyRepository, licenseRestService, workspaceMapStruct, restMapStruct, applicationEventPublisher, settingRepository, workspaceCustomSettingRepository, mailContextHandler);
        this.workspaceRepository = workspaceRepository;
        this.fileUploadService = fileUploadService;
        this.workspaceUserRepository = workspaceUserRepository;
        this.workspaceRoleRepository = workspaceRoleRepository;
        this.workspaceUserPermissionRepository = workspaceUserPermissionRepository;
        this.workspacePermissionRepository = workspacePermissionRepository;
        this.workspaceMapStruct = workspaceMapStruct;
        this.applicationEventPublisher = applicationEventPublisher;
    }


    /**
     * 워크스페이스 생성
     *
     * @param workspaceCreateRequest - 생성 할 워크스페이스 정보
     * @return - 생성 된 워크스페이스 정보
     */
    @Transactional
    @Override
    public WorkspaceInfoDTO createWorkspace(WorkspaceCreateRequest workspaceCreateRequest) {
        //1. 서브유저(유저가 만들어낸 유저)는 워크스페이스를 가질 수 없다.
        UserInfoRestResponse userInfoRestResponse = super.getUserInfo(workspaceCreateRequest.getUserId());
        if (userInfoRestResponse.isSubUser()) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        //2. 사용자가 생성 가능한 워크스페이스 수를 넘겼는지 체크
        long userHasWorkspaceAmount = workspaceRepository.countByUserId(workspaceCreateRequest.getUserId());
        if (userHasWorkspaceAmount + 1 > MAX_HAVE_WORKSPACE_AMOUNT) {
            log.error("[WORKSPACE CREATE] creatable maximum Workspace amount : [{}], current amount of workspace that user has : [{}].", MAX_HAVE_WORKSPACE_AMOUNT, userHasWorkspaceAmount);
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_CREATE_MAX_CREATE);
        }

        String workspaceId = RandomStringTokenUtil.generate(UUIDType.WORKSPACE_UUID, 0);

        String profile = null;
        if (workspaceCreateRequest.getProfile() == null) {
            profile = fileUploadService.getDefaultFileUrl(DefaultImageFile.WORKSPACE_PROFILE_IMG);
        } else {
            profile = fileUploadService.upload(workspaceCreateRequest.getProfile(), workspaceId);
        }

        // 워크스페이스 생성
        Workspace workspace = Workspace.builder()
                .uuid(workspaceId)
                .userId(workspaceCreateRequest.getUserId())
                .name(workspaceCreateRequest.getName())
                .description(workspaceCreateRequest.getDescription())
                .profile(profile)
                .pinNumber(RandomStringTokenUtil.generate(UUIDType.PIN_NUMBER, 0))
                .build();
        workspaceRepository.save(workspace);

        // 워크스페이스 소속 할당
        WorkspaceUser newWorkspaceUser = WorkspaceUser.builder()
                .userId(workspaceCreateRequest.getUserId())
                .workspace(workspace)
                .build();
        workspaceUserRepository.save(newWorkspaceUser);

        // 워크스페이스 권한 할당
        WorkspaceRole workspaceRole = workspaceRoleRepository.findByRole(Role.MASTER).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_ROLE_NOT_FOUND));
        WorkspacePermission workspacePermission = workspacePermissionRepository.findByPermission(Permission.ALL).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_PERMISSION_NOT_FOUND));
        WorkspaceUserPermission workspaceUserPermission = WorkspaceUserPermission.builder()
                .workspaceRole(workspaceRole)
                .workspacePermission(workspacePermission)
                .workspaceUser(newWorkspaceUser)
                .build();
        workspaceUserPermissionRepository.save(workspaceUserPermission);

        return workspaceMapStruct.workspaceToWorkspaceInfoDTO(workspace);
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


