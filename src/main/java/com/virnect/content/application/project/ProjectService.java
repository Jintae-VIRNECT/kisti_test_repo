package com.virnect.content.application.project;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.application.license.LicenseRestService;
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
import com.virnect.content.dto.request.ProjectUploadRequest;
import com.virnect.content.dto.request.PropertyInfoRequest;
import com.virnect.content.dto.response.ProjectInfoListResponse;
import com.virnect.content.dto.response.ProjectInfoResponse;
import com.virnect.content.dto.rest.LicenseInfoResponse;
import com.virnect.content.dto.rest.MyLicenseInfoListResponse;
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

	@Transactional
	public ProjectInfoResponse uploadProject(ProjectUploadRequest projectUploadRequest) {
		//필수 값 체크 - project
		log.info("[PROJECT UPLOAD] REQ : {}", projectUploadRequest.toString());

		//업로드 사용자의 MAKE 라이선스 체크
		checkUserHaveMAKELicense(projectUploadRequest.getUserUUID(), projectUploadRequest.getWorkspaceUUID());
		checkWorkspaceMaxStorage(projectUploadRequest.getWorkspaceUUID(), projectUploadRequest.getProject().getSize());

		//프로젝트 저장
		String projectUUID = UUID.randomUUID().toString();
		String projectPath = fileUploadService.uploadByFileInputStream(projectUploadRequest.getProject(), projectUUID);
		Project project = Project.builder()
			.uuid(projectUUID)
			.name(projectUploadRequest.getName())
			.path(projectPath)
			.size(projectUploadRequest.getProject().getSize())
			.userUUID(projectUploadRequest.getUserUUID())
			.workspaceUUID(projectUploadRequest.getWorkspaceUUID())
			.properties(projectUploadRequest.getProperties())
			.editPermission(projectUploadRequest.getEditPermission())
			.sharePermission(projectUploadRequest.getSharePermission())
			.build();
		projectRepository.save(project);

		//모드 정보 저장
		List<Mode> modeList = new ArrayList<>();
		for (Mode mode : projectUploadRequest.getModeList()) {
			ProjectMode projectMode = ProjectMode.builder().mode(mode).project(project).build();
			modeList.add(mode);
			projectModeRepository.save(projectMode);
		}

		//공유 정보 저장
		if (projectUploadRequest.getSharePermission() == SharePermission.SPECIFIC_MEMBER) {
			for (String sharedUser : projectUploadRequest.getSharedUserList()) {
				ProjectShareUser projectShareUser = ProjectShareUser.builder()
					.userUUID(sharedUser)
					.project(project)
					.build();
				projectShareUerRepository.save(projectShareUser);
			}
		}
		//편집 정보 저장
		if (projectUploadRequest.getEditPermission() == EditPermission.SPECIFIC_MEMBER) {
			for (String editUser : projectUploadRequest.getEditUserList()) {
				ProjectEditUser projectEditUser = ProjectEditUser.builder().userUUID(editUser).project(project).build();
				projectEditUserRepository.save(projectEditUser);
			}
		}

		//타겟 저장
		String targetData = UUID.randomUUID().toString();
		String targetPath = "";
		if (projectUploadRequest.getTargetType() == TargetType.QR) {
			targetPath = getQRTargetFilePath(targetData);

		}
		if (projectUploadRequest.getTargetType() == TargetType.VTarget) {
			targetPath = fileDownloadService.getFilePath("workspace/report/", "virnect_target.png");
		}
		if (projectUploadRequest.getTargetType() == TargetType.IMAGE) {
			targetPath = fileUploadService.uploadByFileInputStream(
				projectUploadRequest.getTargetFile(),
				projectUploadRequest.getTargetFile().getName()
			);//todo: image target file name 정해야 함.
		}

		ProjectTarget projectTarget = ProjectTarget.builder()
			.type(projectUploadRequest.getTargetType())
			.data(targetData)
			.path(targetPath)
			.size(projectUploadRequest.getTargetSize())
			.project(project)
			.build();
		projectTargetRepository.save(projectTarget);
		ProjectInfoResponse projectInfoResponse = projectResponseMapper.projectToProjectInfoResponse(project);
		ObjectMapper mapper = new ObjectMapper();
		PropertyInfoRequest propertyInfoRequest = new PropertyInfoRequest();
		try {
			propertyInfoRequest = mapper.readValue(project.getProperties(), PropertyInfoRequest.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		projectInfoResponse.setModeList(modeList);
		//projectInfoResponse.setEditUserList();
		//projectInfoResponse.setSharedUserList();
		projectInfoResponse.setPropertySceneTotal(propertyInfoRequest.getSceneGroupList().size());
		int sceneGroupTotal = (int)propertyInfoRequest.getSceneGroupList()
			.stream()
			.map(sceneGroup -> sceneGroup.getSceneList().size())
			.count();
		int objectTotal = propertyInfoRequest.getSceneGroupList()
			.stream()
			.mapToInt(sceneGroup -> (int)sceneGroup.getSceneList()
				.stream()
				.map(scene -> scene.getObjectList().size())
				.count())
			.sum();
		projectInfoResponse.setPropertySceneGroupTotal(sceneGroupTotal);
		projectInfoResponse.setPropertyObjectTotal(objectTotal);
		projectInfoResponse.setTargetInfo(projectResponseMapper.projectTargetToTargetInfoResponse(projectTarget));
		return projectInfoResponse;
	}

	private String getQRTargetFilePath(String targetData) {/*
		String decodedTargetData = null;
		try {
			decodedTargetData = URLDecoder.decode(targetData, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
		}
		String decryptedTargetData = AES256EncryptUtils.decryptByBytes("virnect", decodedTargetData);*/
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			BufferedImage qrImage = QRcodeGenerator.generateQRCodeImage(targetData, 256, 256);
			ImageIO.write(qrImage, "png", os);

			String qrString = Base64.getEncoder().encodeToString(os.toByteArray());
			return fileUploadService.base64ImageUpload(qrString);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
		}
	}

	private void checkWorkspaceMaxStorage(String workspaceUUID, Long requestSize) {
		//업로드 가능 용량 체크
		LicenseInfoResponse licenseInfoResponse = getLicenseInfoResponse(workspaceUUID);

		// 업로드를 요청하는 워크스페이스를 기반으로 라이센스 서버의 최대 저장 용량을 가져온다. (MB 단위)
		Long maxCapacity = licenseInfoResponse.getMaxStorageSize();
		// 라이선스의 최대 용량이 0인 경우 업로드 프로세스를 수행하지 않는다.
		if (maxCapacity == null || maxCapacity == 0L) {
			log.error("[CHECK WORKSPACE MAX STORAGE] workspace max storage : {}", maxCapacity);
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD_LICENSE);
		}

		// 업로드를 요청하는 워크스페이스의 현재 총 용량을 가져온다. (byte 단위)
		Long workspaceContentsCapacity = contentRepository.getWorkspaceStorageSize(workspaceUUID);
		Long workspaceProjectsCapacity = projectRepository.getWorkspaceStorageSize(workspaceUUID);

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
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD_LICENSE);
		}
	}

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

	private void checkUserHaveMAKELicense(String userUUID, String workspaceUUID) {
		MyLicenseInfoListResponse myLicenseInfoListResponse = getMyLicenseInfoRequest(userUUID, workspaceUUID);
		boolean userHaveMAKELicense = myLicenseInfoListResponse.getLicenseInfoList()
			.stream()
			.anyMatch(myLicenseInfoResponse -> myLicenseInfoResponse.getProductName().equals("MAKE")
				&& myLicenseInfoResponse.getStatus().equals("ACTIVE"));
		if (!userHaveMAKELicense) {
			log.error(
				"[CHECK USER HAVE MAKE LICENSE] User haven't active Make license. userUUID : {}, workspaceUUID : {}",
				userUUID, workspaceUUID
			);
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD_LICENSE_PRODUCT_NOT_FOUND);
		}
	}

	private MyLicenseInfoListResponse getMyLicenseInfoRequest(String userUUID, String workspaceUUID) {
		ApiResponse<MyLicenseInfoListResponse> apiResponse = licenseRestService.getMyLicenseInfoRequestHandler(
			userUUID,
			workspaceUUID
		);
		if (apiResponse.getCode() != 200 || apiResponse.getData() == null || CollectionUtils.isEmpty(
			apiResponse.getData().getLicenseInfoList())) {
			log.error(
				"[REQ - LICENSE SERVER][GET MY LICENSE INFO] request user uuid : {}, request workspace uuid : {}, response code : {}, response message : {}, response data : {}",
				userUUID, workspaceUUID, apiResponse.getCode(), apiResponse.getMessage(),
				apiResponse.getData().getLicenseInfoList()
			);
			throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD_LICENSE_NOT_FOUND);
		}
		return apiResponse.getData();
	}

	public ProjectInfoListResponse getProjectList(
		String workspaceUUID, String userUUID, List<SharePermission> sharePermissionList,
		List<EditPermission> editPermissionList, List<Mode> modeList, List<TargetType> targetTypeList,
		Pageable pageable
	) {
		//조회 유저 권한 체크

		//필터링 된 프로젝트 목록
		List<Project> projectList = projectRepository.getFilteredProjectList(
			workspaceUUID, sharePermissionList, editPermissionList, modeList, targetTypeList);
		//MEMBER - 체크하지 않음.
		//SPECIFIC_MEMBER - 지정 멤버인지, 업로더인지, 마스터인지, 매니저인지 체크
		//UPLOADER - 업로더인지, 마스터인지, 매니저인지 체크
		//MANAGER - 마스터인지, 매니저인지 체크

		//1. 지정 멤버 프로젝트 걸러서 권한체크
		List<Project> projectList1 = new ArrayList<>();
		projectList.forEach(project -> {
			//1. 지정멤버 필터링
			if (!CollectionUtils.isEmpty(sharePermissionList) && sharePermissionList.contains(
				SharePermission.SPECIFIC_MEMBER)) {
				boolean match = project.getProjectShareUserList()
					.stream()
					.anyMatch(projectShareUser -> projectShareUser.getUserUUID().equals(userUUID));
				if (project.getUserUUID().equals(userUUID) || match) {
					projectList1.add(project);
				}
			}
			//2. 업로더 필터링
			if (!CollectionUtils.isEmpty(sharePermissionList) && sharePermissionList.contains(
				SharePermission.UPLOADER)) {
				if (project.getUserUUID().equals(userUUID)) {
					projectList1.add(project);
				}
			}
		});
		Page<Project> projectPage = projectRepository.getProjectPageByProjectList(projectList1, pageable);
		List<ProjectInfoResponse> projectInfoResponseList = projectPage.stream()
			.map(projectResponseMapper::projectToProjectInfoResponse)
			.collect(Collectors.toList());
		PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
			.currentPage(pageable.getPageNumber())
			.currentSize(pageable.getPageSize())
			.totalPage(projectPage.getTotalPages())
			.totalElements(projectPage.getTotalElements())
			.build();
		return new ProjectInfoListResponse(projectInfoResponseList, pageMetadataResponse);
	}
}
