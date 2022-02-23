package com.virnect.workspace.application.workspace;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.application.user.dto.response.UserInfoRestResponse;
import com.virnect.workspace.dao.history.HistoryRepository;
import com.virnect.workspace.dao.setting.WorkspaceSettingRepository;
import com.virnect.workspace.dao.workspace.WorkspaceRepository;
import com.virnect.workspace.dao.workspacepermission.WorkspacePermissionRepository;
import com.virnect.workspace.dao.workspacerole.WorkspaceRoleRepository;
import com.virnect.workspace.dao.workspaceuser.WorkspaceUserRepository;
import com.virnect.workspace.dao.workspaceuserpermission.WorkspaceUserPermissionRepository;
import com.virnect.workspace.domain.workspace.Role;
import com.virnect.workspace.domain.workspace.Workspace;
import com.virnect.workspace.domain.workspace.WorkspacePermission;
import com.virnect.workspace.domain.workspace.WorkspaceRole;
import com.virnect.workspace.domain.workspace.WorkspaceSetting;
import com.virnect.workspace.domain.workspace.WorkspaceUser;
import com.virnect.workspace.domain.workspace.WorkspaceUserPermission;
import com.virnect.workspace.dto.request.WorkspaceCreateRequest;
import com.virnect.workspace.dto.request.WorkspaceFaviconUpdateRequest;
import com.virnect.workspace.dto.request.WorkspaceLogoUpdateRequest;
import com.virnect.workspace.dto.request.WorkspaceTitleUpdateRequest;
import com.virnect.workspace.dto.response.WorkspaceCustomSettingResponse;
import com.virnect.workspace.dto.response.WorkspaceFaviconUpdateResponse;
import com.virnect.workspace.dto.response.WorkspaceInfoDTO;
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
import com.virnect.workspace.infra.file.DefaultImageName;
import com.virnect.workspace.infra.file.FileService;

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

	public OffWorkspaceServiceImpl(
		WorkspaceRepository workspaceRepository, WorkspaceUserRepository workspaceUserRepository,
		WorkspaceUserPermissionRepository workspaceUserPermissionRepository, UserRestService userRestService,
		FileService fileUploadService, HistoryRepository historyRepository, LicenseRestService licenseRestService,
		WorkspaceMapStruct workspaceMapStruct, RestMapStruct restMapStruct,
		ApplicationEventPublisher applicationEventPublisher, MailContextHandler mailContextHandler,
		WorkspaceSettingRepository workspaceSettingRepository, WorkspaceRoleRepository workspaceRoleRepository,
		WorkspacePermissionRepository workspacePermissionRepository
	) {
		super(
			workspaceRepository, workspaceUserRepository, workspaceUserPermissionRepository, userRestService,
			fileUploadService, historyRepository, licenseRestService, workspaceMapStruct, restMapStruct,
			applicationEventPublisher, mailContextHandler,
			workspaceSettingRepository
		);
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

	private static final int MAX_HAVE_WORKSPACE_AMOUNT = 1; //최대 생성 가능한 워크스페이스 수

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
			log.error(
				"[WORKSPACE CREATE] creatable maximum Workspace amount : [{}], current amount of workspace that user has : [{}].",
				MAX_HAVE_WORKSPACE_AMOUNT, userHasWorkspaceAmount
			);
			throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_CREATE_MAX_CREATE);
		}

		String workspaceId = RandomStringTokenUtil.generate(UUIDType.WORKSPACE_UUID, 0);

		String profile = null;
		if (workspaceCreateRequest.getProfile() == null) {
			profile = fileUploadService.getDefaultFileUrl(DefaultImageName.WORKSPACE_PROFILE);
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
		WorkspaceRole workspaceRole = workspaceRoleRepository.findByRole(Role.MASTER)
			.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_ROLE_NOT_FOUND));
		WorkspacePermission workspacePermission = workspacePermissionRepository.findByPermission(Permission.ALL)
			.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_PERMISSION_NOT_FOUND));
		WorkspaceUserPermission workspaceUserPermission = WorkspaceUserPermission.builder()
			.workspaceRole(workspaceRole)
			.workspacePermission(workspacePermission)
			.workspaceUser(newWorkspaceUser)
			.build();
		workspaceUserPermissionRepository.save(workspaceUserPermission);

		return workspaceMapStruct.workspaceToWorkspaceInfoDTO(workspace);
	}

	public WorkspaceFaviconUpdateResponse updateWorkspaceFavicon(
		String workspaceId, WorkspaceFaviconUpdateRequest workspaceFaviconUpdateRequest
	) {
		//1. 권한 체크
		Workspace workspace = checkWorkspaceAndUserRole(
			workspaceId, workspaceFaviconUpdateRequest.getUserId(), new Role[] {Role.MASTER});
		List<WorkspaceSetting> workspaceSettingList = workspaceSettingRepository.findAll();
		WorkspaceSetting workspaceSetting = workspaceSettingList.stream().findFirst()
			.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));

		//2. 파비콘 확장자, 사이즈 체크
		if (workspaceFaviconUpdateRequest.getFavicon() == null) {
			String favicon = fileUploadService.getDefaultFileUrl(DefaultImageName.WORKSPACE_DEFAULT_FAVICON);
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
		String favicon = fileUploadService.upload(workspaceFaviconUpdateRequest.getFavicon(), workspaceId);
		workspaceSetting.setFavicon(favicon);
		workspaceSettingRepository.save(workspaceSetting);

		WorkspaceFaviconUpdateResponse workspaceFaviconUpdateResponse = new WorkspaceFaviconUpdateResponse();
		workspaceFaviconUpdateResponse.setResult(true);
		workspaceFaviconUpdateResponse.setFavicon(favicon);
		return workspaceFaviconUpdateResponse;
	}

	private void checkFileSize(long requestSize) {
		if (requestSize < 0 || requestSize > (long)3145728) {
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
			workspaceId, workspaceLogoUpdateRequest.getUserId(), new Role[] {Role.MASTER});
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

			String logo = fileUploadService.upload(workspaceLogoUpdateRequest.getDefaultLogo(), workspaceId);
			workspaceSetting.setDefaultLogo(logo);
		} else {
			String logoDefault = fileUploadService.getDefaultFileUrl(DefaultImageName.WORKSPACE_DEFAULT_LOGO);
			workspaceSetting.setDefaultLogo(logoDefault);
			workspaceSettingRepository.save(workspaceSetting);

		}

		//4. grey logo 업로드
		if (workspaceLogoUpdateRequest.getGreyLogo() != null) {
			String greyExtension = FilenameUtils.getExtension(
				workspaceLogoUpdateRequest.getGreyLogo().getOriginalFilename());
			checkFileSize(workspaceLogoUpdateRequest.getGreyLogo().getSize());
			checkFileExtension(greyExtension, allowExtension);

			String logo = fileUploadService.upload(workspaceLogoUpdateRequest.getGreyLogo(), workspaceId);
			workspaceSetting.setGreyLogo(logo);
		}

		//4. white logo 업로드
		if (workspaceLogoUpdateRequest.getWhiteLogo() != null) {
			String whiteExtension = FilenameUtils.getExtension(
				workspaceLogoUpdateRequest.getWhiteLogo().getOriginalFilename());
			checkFileSize(workspaceLogoUpdateRequest.getWhiteLogo().getSize());
			checkFileExtension(whiteExtension, allowExtension);

			String logo = fileUploadService.upload(workspaceLogoUpdateRequest.getWhiteLogo(), workspaceId);
			workspaceSetting.setWhiteLogo(logo);
		} else {
			String logoWhite = fileUploadService.getDefaultFileUrl(DefaultImageName.WORKSPACE_WHITE_LOGO);
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
			workspaceId, workspaceTitleUpdateRequest.getUserId(), new Role[] {Role.MASTER});
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

	@Override
	public WorkspaceCustomSettingResponse getWorkspaceCustomSetting(String workspaceId) {
		List<WorkspaceSetting> workspaceSettingList = workspaceSettingRepository.findAll();

		WorkspaceSetting workspaceSetting = workspaceSettingList.stream().findFirst()
			.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));

		return workspaceMapStruct.workspaceSettingToWorkspaceCustomSettingResponse(workspaceSetting);
	}

	private Workspace checkWorkspaceAndUserRole(String workspaceId, String userId, Role[] roles) {
		Workspace workspace = workspaceRepository.findByUuid(workspaceId)
			.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
		WorkspaceUserPermission workspaceUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(
			workspace, userId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));

		log.info(
			"[CHECK WORKSPACE USER ROLE] Acceptable User Workspace Role : {}, Present User Role : [{}]",
			Arrays.toString(roles),
			workspaceUserPermission.getWorkspaceRole().getRole()
		);
		if (Arrays.stream(roles).noneMatch(role -> role == workspaceUserPermission.getWorkspaceRole().getRole())) {
			throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
		}
		return workspace;
	}
}
