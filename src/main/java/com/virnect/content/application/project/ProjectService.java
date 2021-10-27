package com.virnect.content.application.project;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.application.license.LicenseRestService;
import com.virnect.content.application.user.UserRestService;
import com.virnect.content.application.workspace.WorkspaceRestService;
import com.virnect.content.dao.content.ContentRepository;
import com.virnect.content.dao.project.ProjectEditUserRepository;
import com.virnect.content.dao.project.ProjectModeRepository;
import com.virnect.content.dao.project.ProjectRepository;
import com.virnect.content.dao.project.ProjectShareUerRepository;
import com.virnect.content.dao.project.ProjectTargetRepository;
import com.virnect.content.domain.EditPermission;
import com.virnect.content.domain.Mode;
import com.virnect.content.domain.SharePermission;
import com.virnect.content.domain.TargetType;
import com.virnect.content.domain.project.Project;
import com.virnect.content.domain.project.ProjectEditUser;
import com.virnect.content.domain.project.ProjectMode;
import com.virnect.content.domain.project.ProjectShareUser;
import com.virnect.content.domain.project.ProjectTarget;
import com.virnect.content.dto.request.ProjectUpdateRequest;
import com.virnect.content.dto.request.ProjectUploadRequest;
import com.virnect.content.dto.request.PropertyInfoDTO;
import com.virnect.content.dto.response.ProjectDeleteResponse;
import com.virnect.content.dto.response.ProjectInfoListResponse;
import com.virnect.content.dto.response.ProjectInfoResponse;
import com.virnect.content.dto.response.ProjectTargetInfoResponse;
import com.virnect.content.dto.response.ProjectUpdateResponse;
import com.virnect.content.dto.rest.LicenseInfoResponse;
import com.virnect.content.dto.rest.MemberInfoDTO;
import com.virnect.content.dto.rest.MyLicenseInfoListResponse;
import com.virnect.content.dto.rest.UserInfoResponse;
import com.virnect.content.event.ProjectDownloadHitEvent;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.common.ApiResponse;
import com.virnect.content.global.common.PageMetadataResponse;
import com.virnect.content.global.common.ProjectResponseMapper;
import com.virnect.content.global.error.ErrorCode;
import com.virnect.content.global.util.QRcodeGenerator;
import com.virnect.content.infra.file.download.FileDownloadService;
import com.virnect.content.infra.file.upload.FileUploadService;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {
	private final FileUploadService fileUploadService;
	private final FileDownloadService fileDownloadService;
	private final ProjectRepository projectRepository;
	private final ProjectTargetRepository projectTargetRepository;
	private final ProjectModeRepository projectModeRepository;
	private final ProjectEditUserRepository projectEditUserRepository;
	private final ProjectShareUerRepository projectShareUerRepository;
	private final LicenseRestService licenseRestService;
	private final ContentRepository contentRepository;
	private final ProjectResponseMapper projectResponseMapper;
	private final WorkspaceRestService workspaceRestService;
	private final UserRestService userRestService;
	private final ApplicationEventPublisher applicationEventPublisher;

	private static final String PROJECT_DIRECTORY = "project";
	private static final String REPORT_DIRECTORY = "report";
	private static final String REPORT_FILE_EXTENSION = ".png";
	private static final String REPORT_DEFAULT_DIRECTORY = "workspace/report/";

	/**
	 * 프로젝트 업로드
	 * @param projectUploadRequest - 업로드 요청 정보
	 * @return - 업로드 된 프로젝트 정보
	 */
	@Transactional
	public ProjectInfoResponse uploadProject(ProjectUploadRequest projectUploadRequest) {
		//1-1. 프로젝트 파일 체크
		String fileName = projectUploadRequest.getProject()
			.substring(projectUploadRequest.getProject().lastIndexOf("/") + 1);
		long projectFileSize = fileDownloadService.getFileSize(projectUploadRequest.getProject());
		//1-2. 업로드 사용자의 MAKE 라이선스 체크
		if (!checkUserHaveMAKELicense(projectUploadRequest.getUserUUID(), projectUploadRequest.getWorkspaceUUID())) {
			throw new ContentServiceException(ErrorCode.ERR_PROJECT_UPLOAD_INVALID_LICENSE);
		}
		//1-3. 워크스페이스의 최대 업로드 사용량 체크
		if (!checkWorkspaceMaxStorage(projectUploadRequest.getWorkspaceUUID(), projectFileSize)) {
			throw new ContentServiceException(ErrorCode.ERR_PROJECT_UPLOAD_MAX_STORAGE);
		}

		//2. 프로젝트 저장
		String projectUUID = UUID.randomUUID().toString();
		String projectPath = fileUploadService.copyByFileObject(projectUploadRequest.getProject(), PROJECT_DIRECTORY,
			projectUploadRequest.getWorkspaceUUID(),
			projectUUID
		);
		fileUploadService.deleteByFileUrl(projectUploadRequest.getProject());//원본파일 삭제

		String properties = "";
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			properties = objectMapper.writeValueAsString(projectUploadRequest.getProperties());
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
		}

		Project project = Project.builder()
			.uuid(projectUUID)
			.name(projectUploadRequest.getName())
			.path(projectPath)
			.size(projectFileSize)
			.size(projectFileSize)
			.userUUID(projectUploadRequest.getUserUUID())
			.workspaceUUID(projectUploadRequest.getWorkspaceUUID())
			.properties(properties)
			.editPermission(projectUploadRequest.getEdit().getPermission())
			.sharePermission(projectUploadRequest.getShare().getPermission())
			.build();
		projectRepository.save(project);

		//2-1. 모드 정보 저장
		for (Mode mode : projectUploadRequest.getModeList()) {
			ProjectMode projectMode = ProjectMode.builder().mode(mode).project(project).build();
			projectModeRepository.save(projectMode);
		}

		//2-2. 공유 정보 저장
		if (projectUploadRequest.getShare().getPermission() == SharePermission.SPECIFIC_MEMBER) {
			for (String sharedUser : projectUploadRequest.getShare().getUserList()) {
				ProjectShareUser projectShareUser = ProjectShareUser.builder()
					.userUUID(sharedUser)
					.project(project)
					.build();
				projectShareUerRepository.save(projectShareUser);
			}
		}
		//2-3. 편집 정보 저장
		if (projectUploadRequest.getEdit().getPermission() == EditPermission.SPECIFIC_MEMBER) {
			for (String editUser : projectUploadRequest.getEdit().getUserList()) {
				ProjectEditUser projectEditUser = ProjectEditUser.builder().userUUID(editUser).project(project).build();
				projectEditUserRepository.save(projectEditUser);
			}
		}

		//2-4. 타겟 이미지 저장
		String targetPath = "";
		if (projectUploadRequest.getTarget().getType() == TargetType.QR) {
			String qrString = generateQRString(projectUploadRequest.getTarget().getData());
			String randomFileName = String.format(
				"%s_%s%s", LocalDate.now().toString(), RandomStringUtils.randomAlphanumeric(10).toLowerCase(),
				REPORT_FILE_EXTENSION
			);
			targetPath = fileUploadService.uploadByBase64Image(qrString, REPORT_DIRECTORY,
				projectUploadRequest.getWorkspaceUUID(), randomFileName
			);
		}
		if (projectUploadRequest.getTarget().getType() == TargetType.VTarget) {
			targetPath = fileDownloadService.getDefaultImagePath(REPORT_DEFAULT_DIRECTORY, "virnect_target.png");
		}
		if (projectUploadRequest.getTarget().getType() == TargetType.Image) {
			targetPath = projectUploadRequest.getTarget().getFile();
		}
		if (projectUploadRequest.getTarget().getType() == TargetType.VR) {
			targetPath = null;
		}
		//2-5. 타겟 저장
		ProjectTarget projectTarget = ProjectTarget.builder()
			.type(projectUploadRequest.getTarget().getType())
			.data(projectUploadRequest.getTarget().getData())
			.path(targetPath)
			.width(projectUploadRequest.getTarget().getWidth())
			.length(projectUploadRequest.getTarget().getLength())
			.project(project)
			.build();
		projectTargetRepository.save(projectTarget);

		//3. 응답
		ProjectInfoResponse projectInfoResponse = generateProjectResponse(project);
		projectInfoResponse.setModeList(projectUploadRequest.getModeList());
		ProjectTargetInfoResponse projectTargetInfoResponse = projectResponseMapper.projectTargetToTargetInfoResponse(
			projectTarget);
		projectInfoResponse.setTargetInfo(projectTargetInfoResponse);
		return projectInfoResponse;
	}

	/**
	 * 워크스페이스 최대 용량 체크
	 * @param workspaceUUID - 체크 대상 워크스페이스 식별자
	 * @param requestSize - 추가 대상 용량 정보
	 */
	private boolean checkWorkspaceMaxStorage(String workspaceUUID, long requestSize) {
		log.info(
			"[CHECK WORKSPACE MAX STORAGE] request workspace uuid : {}, add storage size: {}(byte)", workspaceUUID,
			requestSize
		);
		//업로드 가능 용량 체크
		LicenseInfoResponse licenseInfoResponse = getLicenseInfoResponse(workspaceUUID);

		// 업로드를 요청하는 워크스페이스를 기반으로 라이센스 서버의 최대 저장 용량을 가져온다. (MB 단위)
		Long maxCapacity = licenseInfoResponse.getMaxStorageSize();
		// 라이선스의 최대 용량이 0인 경우 업로드 프로세스를 수행하지 않는다.
		if (maxCapacity == null || maxCapacity == 0L) {
			log.error("[CHECK WORKSPACE MAX STORAGE] workspace max storage : {}", maxCapacity);
			return false;
		}

		// 업로드를 요청하는 워크스페이스의 현재 총 용량을 가져온다. (byte 단위)
		Long contentsSizeSum = contentRepository.getWorkspaceStorageSize(workspaceUUID);
		long workspaceContentsCapacity = contentsSizeSum == null ? 0L : contentsSizeSum;
		Long projectsSizeSum = projectRepository.getWorkspaceStorageSize(workspaceUUID);
		long workspaceProjectsCapacity = projectsSizeSum == null ? 0L : projectsSizeSum;

		// 워크스페이스 총 사용 용량에 업로드 파일 용량을 더한다. (byte 단위)
		long sumByteSize = workspaceContentsCapacity + workspaceProjectsCapacity + requestSize;

		// byte를 MegaByte로 변환
		long convertMB = sumByteSize / (1024L * 1024L);

		// 라이센스 서버의 최대 저장용량을 초과할 경우 업로드 프로세스를 수행하지 않는다.
		if (maxCapacity < convertMB) {
			log.error(
				"[CHECK WORKSPACE MAX STORAGE] license max capacity : {}(MB), current capacity : {}(MB)", maxCapacity,
				convertMB
			);
			return false;
		}
		return true;
	}

	/**
	 * REST SERVICE REQ - 워크스페이스의 라이선스 정보 조회
	 * @param workspaceUUID - 조회 대상 워크스페이스 식별자
	 * @return - 라이선스 정보
	 */
	private LicenseInfoResponse getLicenseInfoResponse(String workspaceUUID) {
		ApiResponse<LicenseInfoResponse> apiResponse = licenseRestService.getWorkspaceLicenseInfo(workspaceUUID);
		if (apiResponse.getCode() != 200 || apiResponse.getData() == null || apiResponse.getData()
			.getPlanStatus()
			.equals("INACTIVE")) {
			log.error(
				"[REQ - LICENSE SERVER][GET WORKSPACE LICENSE INFO] request workspace uuid : {}, response code : {}, response message : {}",
				workspaceUUID, apiResponse.getCode(), apiResponse.getMessage()
			);
		}
		return apiResponse.getData();
	}

	/**
	 * 유저의 MAKE 라이선스 보유 여부 조회
	 * @param userUUID - 조회 대상 유저 식별자
	 * @param workspaceUUID - 조회 대상 워크스페이스 식별자
	 */
	private boolean checkUserHaveMAKELicense(String userUUID, String workspaceUUID) {
		MyLicenseInfoListResponse myLicenseInfoListResponse = getMyLicenseInfoRequest(userUUID, workspaceUUID);
		boolean userHaveMAKELicense = myLicenseInfoListResponse.getLicenseInfoList()
			.stream()
			.anyMatch(myLicenseInfoResponse -> myLicenseInfoResponse.getProductName().equals("MAKE")
				&& myLicenseInfoResponse.getProductPlanStatus().equals("ACTIVE"));
		if (!userHaveMAKELicense) {
			log.error(
				"[CHECK USER HAVE MAKE LICENSE] User haven't active Make license. userUUID : {}, workspaceUUID : {}",
				userUUID, workspaceUUID
			);
			return false;
		}
		return true;
	}

	/**
	 * REST SERVICE REQ - 유저의 라이선스 정보 조회
	 * @param userUUID - 조회 대상 유저 식별자
	 * @param workspaceUUID - 조회 대상 워크스페이스 식별자
	 * @return - 유저의 라이선스 정보
	 */
	private MyLicenseInfoListResponse getMyLicenseInfoRequest(String userUUID, String workspaceUUID) {
		ApiResponse<MyLicenseInfoListResponse> apiResponse = licenseRestService.getMyLicenseInfoRequestHandler(
			userUUID,
			workspaceUUID
		);
		if (apiResponse.getCode() != 200 || apiResponse.getData() == null || CollectionUtils.isEmpty(
			apiResponse.getData().getLicenseInfoList())) {
			log.error(
				"[REQ - LICENSE SERVER][GET MY LICENSE INFO] request user uuid : {}, request workspace uuid : {}, response code : {}, response message : {}",
				userUUID, workspaceUUID, apiResponse.getCode(), apiResponse.getMessage()
			);
			throw new ContentServiceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
		}
		return apiResponse.getData();
	}

	/**
	 * 프로젝트 목록 조회
	 * @param workspaceUUID - 조회 대상 워크스페이스 식별자
	 * @param userUUID - 조회 요청 유저 식별자
	 * @param sharePermissionList - 공유 권한 필터 요청 정보
	 * @param editPermissionList - 편집 권한 필터 요청 정보
	 * @param modeList - 모드 필터 요청 정보
	 * @param targetTypeList - 타겟 타입 필터 요청 정보
	 * @param pageable - 페이징 정보
	 * @return - 프로젝트 정보 목록
	 */
	public ProjectInfoListResponse getProjectList(
		String workspaceUUID, String userUUID, List<SharePermission> sharePermissionList,
		List<EditPermission> editPermissionList, List<Mode> modeList, List<TargetType> targetTypeList, String search,
		Pageable pageable
	) {
		//1. 필터링 요청에 따른 프로젝트 목록 select
		Page<Project> filteredProjectPage = projectRepository.getFilteredProjectPage(
			workspaceUUID, sharePermissionList, editPermissionList, modeList, targetTypeList, search, pageable);

		//2. 프로젝트 조회 - 멤버 권한이 아닌경우 필터링이 요청이 없는 경우 필터링 된 모든 프로젝트를 볼 수 있음
		String userRole = getWorkspaceUserRole(workspaceUUID, userUUID);
		if (!userRole.equals("MEMBER")) {
			List<ProjectInfoResponse> projectInfoResponseList = filteredProjectPage.stream()
				.map(project -> generateProjectResponse(project))
				.collect(Collectors.toList());
			PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
				.currentPage(pageable.getPageNumber())
				.currentSize(pageable.getPageSize())
				.totalPage(filteredProjectPage.getTotalPages())
				.totalElements(filteredProjectPage.getTotalElements())
				.build();
			return new ProjectInfoListResponse(projectInfoResponseList, pageMetadataResponse);
		}
		//2-1. 프로젝트 조회 - 멤버 권한일 경우 필터링 된 프로젝트 중 볼 수 있는 프로젝트가 제한됨.
		List<Project> limitedProjectList = new ArrayList<>();
		filteredProjectPage.forEach(project -> {
			//1. 지정멤버 필터링
			if (project.getSharePermission() == SharePermission.SPECIFIC_MEMBER) {
				boolean match = project.getProjectShareUserList()
					.stream()
					.anyMatch(projectShareUser -> projectShareUser.getUserUUID().equals(userUUID));
				if (project.getUserUUID().equals(userUUID) || match) {
					limitedProjectList.add(project);
				}
			}
			//2. 업로더 필터링
			if (project.getSharePermission() == SharePermission.UPLOADER) {
				if (project.getUserUUID().equals(userUUID)) {
					limitedProjectList.add(project);
				}
			}
		});
		//2-1-1. 제한된 프로젝트를 바탕으로 응답
		Page<Project> projectPage = projectRepository.getProjectPageByProjectList(limitedProjectList, pageable);
		List<ProjectInfoResponse> projectInfoResponseList = projectPage.stream()
			.map(project -> generateProjectResponse(project))
			.collect(Collectors.toList());
		PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.totalPage(projectPage.getTotalPages())
			.totalElements(projectPage.getTotalElements())
			.build();

		return new ProjectInfoListResponse(projectInfoResponseList, pageMetadataResponse);
	}

	/**
	 * 프로젝트 상세 정보 응답 생성
	 * @param project - 프로젝트 entity
	 * @return - 프로젝트 상세 정보 dto
	 */
	private ProjectInfoResponse generateProjectResponse(Project project) {
		ProjectInfoResponse projectInfoResponse = projectResponseMapper.projectToProjectInfoResponse(project);
		//모드 정보
		projectInfoResponse.setModeList(
			project.getProjectModeList().stream().map(ProjectMode::getMode).collect(Collectors.toList()));

		//공유 권한 정보
		if (project.getSharePermission() == SharePermission.SPECIFIC_MEMBER) {
			projectInfoResponse.setSharedUserList(project.getProjectShareUserList()
				.stream()
				.map(ProjectShareUser::getUserUUID)
				.collect(Collectors.toList()));
		}
		//편집 권한 정보
		if (project.getEditPermission() == EditPermission.SPECIFIC_MEMBER) {
			projectInfoResponse.setEditUserList(project.getProjectEditUserList()
				.stream()
				.map(ProjectEditUser::getUserUUID)
				.collect(Collectors.toList()));
		}
		//타겟 정보
		ProjectTargetInfoResponse projectTargetInfoResponse = projectResponseMapper.projectTargetToTargetInfoResponse(
			project.getProjectTarget());
		projectInfoResponse.setTargetInfo(projectTargetInfoResponse);

		//프로퍼티
		ObjectMapper objectMapper = new ObjectMapper();
		PropertyInfoDTO propertyInfo = null;
		try {
			propertyInfo = objectMapper.readValue(project.getProperties(), PropertyInfoDTO.class);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
		}
		projectInfoResponse.setProperty(propertyInfo);
		projectInfoResponse.setPropertySceneGroupTotal(propertyInfo.getPropertyObjectList().size());
		projectInfoResponse.setPropertySceneTotal(
			propertyInfo.getSceneCount(propertyInfo.getPropertyObjectList(), 0));
		projectInfoResponse.setPropertyObjectTotal(
			propertyInfo.getObjectCount(propertyInfo.getPropertyObjectList(), 0));

		//업로더 네임, 프로필 정보
		UserInfoResponse userInfoResponse = getUserInfoResponse(project.getUserUUID());
		projectInfoResponse.setUploaderName(userInfoResponse.getName());
		projectInfoResponse.setUploaderProfile(userInfoResponse.getProfile());
		return projectInfoResponse;
	}

	/**
	 * 유저 정보 조회
	 * @param userUUID -  조회 대상 유저 식별자
	 * @return - 유저 정보
	 */
	private UserInfoResponse getUserInfoResponse(String userUUID) {
		ApiResponse<UserInfoResponse> apiResponse = userRestService.getUserInfoByUserUUID(userUUID);
		if (apiResponse.getCode() != 200 || apiResponse.getData() == null || !StringUtils.hasText(
			apiResponse.getData().getUuid())) {
			log.error(
				"[REQ - USER SERVER][GET USER INFO] request user uuid : {},  response code : {}, response message : {}",
				userUUID, apiResponse.getCode(), apiResponse.getMessage()
			);
			throw new ContentServiceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
		}
		return apiResponse.getData();
	}

	/**
	 * REST SERVICE REQ - 워크스페이스 유저 정보 요청
	 * @param workspaceUUID - 대상 워크스페이스 식별자
	 * @param userUUID - 대상 유저 식별자
	 * @return - 워크스페이스 유저 정보
	 */
	private String getWorkspaceUserRole(String workspaceUUID, String userUUID) {
		ApiResponse<MemberInfoDTO> apiResponse = workspaceRestService.getMemberInfo(workspaceUUID, userUUID);
		if (apiResponse.getCode() != 200 || apiResponse.getData() == null || !StringUtils.hasText(
			apiResponse.getData().getUuid())) {
			log.error(
				"[REQ - WORKSPACE SERVER][GET WORKSPACE USER INFO] request user uuid : {}, request workspace uuid : {}, response code : {}, response message : {}",
				userUUID, workspaceUUID, apiResponse.getCode(), apiResponse.getMessage()
			);
			throw new ContentServiceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
		}
		return apiResponse.getData().getRole();
	}

	/**
	 * 프로젝트 상세 정보 조회
	 * @param projectUUID - 조회 대상 프로젝트 식별자
	 * @param userUUID - 조회 요청 식별자
	 * @return - 프로젝트 상세 정보
	 */
	public ProjectInfoResponse getProjectInfo(String projectUUID, String userUUID) {
		//1. 프로젝트 조회
		Project project = projectRepository.findByUuid(projectUUID)
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_PROJECT_NOT_FOUND));

		//2. 프로젝트 공유 권한 체크
		String userRole = getWorkspaceUserRole(project.getWorkspaceUUID(), userUUID);
		if (!checkUserProjectSharePermission(project, userUUID, userRole)) {
			throw new ContentServiceException(ErrorCode.ERR_PROJECT_ACCESS_INVALID_SHARE_PERMISSION);
		}

		//3. 응답
		return generateProjectResponse(project);
	}

	@Transactional
	public ProjectDeleteResponse deleteProject(String projectUUID, String userUUID) {
		//1-1. 프로젝트 유효성 체크
		Project project = projectRepository.findByUuid(projectUUID)
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_PROJECT_NOT_FOUND));
		//1-2. 워크스페이스 체크 & 워크스페이스 내 유저 권한 조회
		String userRole = getWorkspaceUserRole(project.getWorkspaceUUID(), userUUID);
		//1-3. 공유 권한 체크
		if (!checkUserProjectSharePermission(project, userUUID, userRole)) {
			throw new ContentServiceException(ErrorCode.ERR_PROJECT_DELETE_INVALID_SHARE_PERMISSION);
		}
		//1-4. 편집 권한 체크
		if (!checkUserProjectEditPermission(project, userUUID, userRole)) {
			throw new ContentServiceException(ErrorCode.ERR_PROJECT_DELETE_INVALID_EDIT_PERMISSION);
		}

		//2. 삭제
		//project 리소스 삭제
		fileUploadService.deleteByFileUrl(project.getPath());

		//target 리소스 삭제
		if (project.getProjectTarget().getType() == TargetType.Image
			|| project.getProjectTarget().getType() == TargetType.QR) {
			fileUploadService.deleteByFileUrl(project.getProjectTarget().getPath());
		}

		//target 정보 삭제
		projectTargetRepository.delete(project.getProjectTarget());

		//mode 정보 삭제
		projectModeRepository.deleteAll(project.getProjectModeList());

		//공유 유저 정보 삭제
		projectShareUerRepository.deleteAll(project.getProjectShareUserList());

		//편집 유저 정보 삭제
		projectEditUserRepository.deleteAll(project.getProjectEditUserList());

		//project 삭제
		projectRepository.delete(project);

		return new ProjectDeleteResponse(true, projectUUID, LocalDateTime.now());
	}

	@Transactional
	public ProjectUpdateResponse updateProject(String projectUUID, ProjectUpdateRequest projectUpdateRequest) {
		//프로젝트 유효성 검증
		Project project = projectRepository.findByUuid(projectUUID)
			.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_PROJECT_NOT_FOUND));

		String userRole = getWorkspaceUserRole(
			project.getWorkspaceUUID(), projectUpdateRequest.getUserUUID());
		//업데이트 요청 사용자 공유 권한 체크
		if (!checkUserProjectSharePermission(
			project, projectUpdateRequest.getUserUUID(), userRole)) {
			throw new ContentServiceException(ErrorCode.ERR_PROJECT_UPDATE_INVALID_SHARE_PERMISSION);
		}
		//업데이트 요청 사용자 편집 권한 체크
		if (!checkUserProjectEditPermission(
			project, projectUpdateRequest.getUserUUID(), userRole)) {
			throw new ContentServiceException(ErrorCode.ERR_PROJECT_UPDATE_INVALID_EDIT_PERMISSION);
		}

		//프로젝트 이름 변경
		if (StringUtils.hasText(projectUpdateRequest.getName())) {
			project.setName(projectUpdateRequest.getName());
		}

		//프로젝트 파일 변경
		if (StringUtils.hasText(projectUpdateRequest.getProject())) {
			//업로드 사용자의 MAKE 라이선스 체크
			if (!checkUserHaveMAKELicense(projectUpdateRequest.getUserUUID(), project.getWorkspaceUUID())) {
				throw new ContentServiceException(ErrorCode.ERR_PROJECT_UPDATE_INVALID_LICENSE);
			}
			//최대 업로드 사용량 체크
			long projectFileSize = fileDownloadService.getFileSize(projectUpdateRequest.getProject());
			if (!checkWorkspaceMaxStorage(
				projectUpdateRequest.getUserUUID(), projectFileSize - project.getSize()
			)) {
				throw new ContentServiceException(ErrorCode.ERR_PROJECT_UPLOAD_MAX_STORAGE);
			}
			String newProjectPath = fileUploadService.copyByFileObject(
				projectUpdateRequest.getProject(), PROJECT_DIRECTORY, project.getWorkspaceUUID(), projectUUID);
			fileUploadService.deleteByFileUrl(projectUpdateRequest.getProject());//원본파일 삭제
			fileUploadService.deleteByFileUrl(project.getPath());
			project.setPath(newProjectPath);
		}

		//프로젝트 구성정보 변경
		if (projectUpdateRequest.getProperties() != null) {
			String properties = "";
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				properties = objectMapper.writeValueAsString(projectUpdateRequest.getProperties());
			} catch (JsonProcessingException e) {
				log.error(e.getMessage());
				throw new ContentServiceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
			}
			project.setProperties(properties);
		}

		//프로젝트 모드정보 변경
		if (!CollectionUtils.isEmpty(projectUpdateRequest.getModeList())) {
			List<ProjectMode> oldProjectModeList = project.getProjectModeList();
			projectModeRepository.deleteAll(oldProjectModeList);
			List<ProjectMode> newProjectModeList = projectUpdateRequest.getModeList()
				.stream()
				.map(mode -> ProjectMode.builder().mode(mode).project(project).build())
				.collect(Collectors.toList());
			projectModeRepository.saveAll(newProjectModeList);
		}

		//프로젝트 공유정보 변경
		if (projectUpdateRequest.getShare() != null) {
			projectShareUerRepository.deleteAll(project.getProjectShareUserList());
			project.setSharePermission(projectUpdateRequest.getShare().getPermission());
			if (!CollectionUtils.isEmpty(projectUpdateRequest.getShare().getUserList())) {
				List<ProjectShareUser> newProjectShareUserList = projectUpdateRequest.getShare().getUserList()
					.stream()
					.map(userUUID -> ProjectShareUser.builder()
						.userUUID(userUUID)
						.project(project)
						.build())
					.collect(Collectors.toList());
				projectShareUerRepository.saveAll(newProjectShareUserList);
			}
		}

		//프로젝트 편집정보 변경
		if (projectUpdateRequest.getEdit() != null) {
			projectEditUserRepository.deleteAll(project.getProjectEditUserList());
			project.setEditPermission(projectUpdateRequest.getEdit().getPermission());
			if (!CollectionUtils.isEmpty(projectUpdateRequest.getEdit().getUserList())) {
				List<ProjectEditUser> newProjectEditUserList = projectUpdateRequest.getEdit().getUserList()
					.stream()
					.map(userUUID -> ProjectEditUser.builder().userUUID(userUUID).project(project).build())
					.collect(Collectors.toList());
				projectEditUserRepository.saveAll(newProjectEditUserList);
			}
		}

		ProjectTarget projectTarget = project.getProjectTarget();

		//프로젝트 타겟 타입 정보 변경
		if (projectUpdateRequest.getTarget() != null) {
			//기존 타겟 삭제
			if (projectTarget.getType() == TargetType.Image || projectTarget.getType() == TargetType.QR) {
				fileUploadService.deleteByFileUrl(projectTarget.getPath());
			}
			//새로운 타겟 타입 등록
			projectTarget.setType(projectUpdateRequest.getTarget().getType());
			if (projectUpdateRequest.getTarget().getType() == TargetType.VR) {
				projectTarget.setPath(null);
			}
			if (projectUpdateRequest.getTarget().getType() == TargetType.VTarget) {
				String targetPath = fileDownloadService.getDefaultImagePath(
					REPORT_DEFAULT_DIRECTORY, "virnect_target.png");
				projectTarget.setPath(targetPath);
			}
			if (projectUpdateRequest.getTarget().getType() == TargetType.QR) {
				String qrString = generateQRString(projectUpdateRequest.getTarget().getData());
				String randomFileName = String.format(
					"%s_%s%s", LocalDate.now().toString(), RandomStringUtils.randomAlphanumeric(10).toLowerCase(),
					REPORT_FILE_EXTENSION
				);
				String targetPath = fileUploadService.uploadByBase64Image(qrString, REPORT_DIRECTORY,
					project.getWorkspaceUUID(), randomFileName
				);
				projectTarget.setPath(targetPath);
			}
			if (projectUpdateRequest.getTarget().getType() == TargetType.Image) {
				projectTarget.setPath(projectUpdateRequest.getTarget().getFile());
			}
		}

		//프로젝트 타겟 너비 변경
		if (projectUpdateRequest.isTargetWidthUpdate()) {
			projectTarget.setWidth(projectUpdateRequest.getTarget().getWidth());
		}

		//프로젝트 타겟 길이 변경
		if (projectUpdateRequest.isTargetLengthUpdate()) {
			projectTarget.setLength(projectUpdateRequest.getTarget().getLength());
		}
		projectTargetRepository.save(projectTarget);

		//프로젝트 업로드 사용자 변경
		project.setUserUUID(projectUpdateRequest.getUserUUID());
		projectRepository.save(project);

		return new ProjectUpdateResponse(true, projectUUID, projectUpdateRequest.getUserUUID(), LocalDateTime.now());
	}

	private String generateQRString(String targetData) {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			BufferedImage qrImage = QRcodeGenerator.generateQRCodeImage(targetData, 256, 256);
			ImageIO.write(qrImage, "png", os);
			return Base64.getEncoder().encodeToString(os.toByteArray());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_PROJECT_UPLOAD);
		}
	}

	/**
	 * 프로젝트 편집 권한 체크
	 * MEMBER - 체크하지 않음.
	 * SPECIFIC_MEMBER - 지정 멤버인지, 업로더인지, 마스터인지, 매니저인지 체크
	 * UPLOADER - 업로더인지, 마스터인지, 매니저인지 체크
	 * MANAGER - 마스터인지, 매니저인지 체크
	 * @param project - 체크 대상 프로젝트 식별자
	 * @param userUUID - 요청 유저 식별자
	 * @param userRole - 요청 유저 워크스페이스 내 권한
	 */
	private boolean checkUserProjectEditPermission(Project project, String userUUID, String userRole) {
		List<String> projectEditUserUUIDList = project.getProjectEditUserList()
			.stream()
			.map(ProjectEditUser::getUserUUID)
			.collect(Collectors.toList());
		log.info(
			"[CHECK USER EDIT PERMISSION] edit permission : [{}], project uploader : [{}], project specific edit users: [{}], request user role : [{}]",
			project.getEditPermission(), project.getUserUUID(),
			String.join(",", projectEditUserUUIDList), userRole
		);

		if (project.getEditPermission() == EditPermission.SPECIFIC_MEMBER) {
			if (!userRole.equals("MASTER") && !userRole.equals("MANAGER")
				&& !project.getUserUUID().equals(userUUID)
				&& !projectEditUserUUIDList.contains(userUUID)) {
				return false;
			}
		}
		if (project.getEditPermission() == EditPermission.UPLOADER) {
			if (!userRole.equals("MASTER") && !userRole.equals("MANAGER")
				&& !project.getUserUUID().equals(userUUID)) {
				return false;
			}

		}
		if (project.getEditPermission() == EditPermission.MANAGER) {
			if (!userRole.equals("MASTER") && !userRole.equals("MANAGER")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 프로젝트 공유 권한 체크
	 * MEMBER - 체크하지 않음.
	 * SPECIFIC_MEMBER - 지정 멤버인지, 업로더인지, 마스터인지, 매니저인지 체크
	 * UPLOADER - 업로더인지, 마스터인지, 매니저인지 체크
	 * MANAGER - 마스터인지, 매니저인지 체크
	 * @param project - 체크 대상 프로젝트 식별자
	 * @param userUUID - 요청 유저 식별자
	 * @param userRole - 요청 유저 워크스페이스 내 권한
	 */
	private boolean checkUserProjectSharePermission(Project project, String userUUID, String userRole) {
		List<String> projectShareUserUUIDList = project.getProjectShareUserList()
			.stream()
			.map(ProjectShareUser::getUserUUID)
			.collect(Collectors.toList());
		log.info(
			"[CHECK USER SHARE PERMISSION] share permission : [{}], project uploader : [{}], project specific share users : [{}], request user role : [{}]",
			project.getSharePermission(), project.getUserUUID(),
			String.join(",", projectShareUserUUIDList), userRole
		);
		if (project.getSharePermission() == SharePermission.SPECIFIC_MEMBER) {
			if (!userRole.equals("MASTER") && !userRole.equals("MANAGER")
				&& !project.getUserUUID().equals(userUUID)
				&& project.getProjectShareUserList()
				.stream()
				.noneMatch(projectShareUser -> projectShareUser.getUserUUID().equals(userUUID))) {
				return false;
			}
		}
		if (project.getSharePermission() == SharePermission.UPLOADER) {
			if (!userRole.equals("MASTER") && !userRole.equals("MANAGER")
				&& !project.getUserUUID().equals(userUUID)) {
				return false;
			}
		}
		if (project.getSharePermission() == SharePermission.MANAGER) {
			if (!userRole.equals("MASTER") && !userRole.equals("MANAGER")) {
				return false;
			}
		}
		return true;
	}

	public ResponseEntity<byte[]> downloadProjectByUUIDList(
		List<String> projectUUIDList, String userUUID, String workspaceUUID
	) {
		//1. 워크스페이스 체크
		String userRole = getWorkspaceUserRole(workspaceUUID, userUUID);
		List<ProjectDownloadHitEvent> projectDownloadHitEventList = new ArrayList<>();
		for (String projectUUID : projectUUIDList) {
			//1-2. 프로젝트 식별자 체크
			Project project = projectRepository.findByUuid(projectUUID)
				.orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_PROJECT_NOT_FOUND));
			//1-3. 공유 권한 체크
			if (!checkUserProjectSharePermission(project, userUUID, userRole)) {
				throw new ContentServiceException(ErrorCode.ERR_PROJECT_DOWNLOAD_INVALID_SHARE_PERMISSION);
			}
			//1-4. 프로젝트 다운로드 이벤트 생성
			projectDownloadHitEventList.add(new ProjectDownloadHitEvent(project, userUUID));
		}

		//2. 단일 파일 다운로드
		if (projectUUIDList.size() == 1) {
			ResponseEntity<byte[]> responseEntity = fileDownloadService.fileDownload(projectDownloadHitEventList.get(0).getProject().getPath(), null);
			applicationEventPublisher.publishEvent(projectDownloadHitEventList.get(0));
			return responseEntity;
		}

		//3. 여러 파일 다운로드
		List<String> projectFilePathList = projectDownloadHitEventList.stream().map(projectDownloadHitEvent -> projectDownloadHitEvent.getProject().getPath()).collect(Collectors.toList());
		byte[] bytes = fileDownloadService.multipleFileDownload(projectFilePathList);
		projectDownloadHitEventList.forEach(applicationEventPublisher::publishEvent);

		String zipFileName = String.format("%s_%s", LocalDate.now().toString(), "projects.zip");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.parseMediaType("application/octet-stream"));
		httpHeaders.setContentDispositionFormData("attachment", zipFileName);
		httpHeaders.setContentLength(bytes.length);
		return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
	}
}
