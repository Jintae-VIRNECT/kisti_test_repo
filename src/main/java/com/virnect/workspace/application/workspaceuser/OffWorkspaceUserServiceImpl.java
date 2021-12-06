package com.virnect.workspace.application.workspaceuser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.RedirectView;

import lombok.extern.slf4j.Slf4j;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.remote.RemoteRestService;
import com.virnect.workspace.application.user.UserRestServiceHandler;
import com.virnect.workspace.dao.workspace.WorkspaceRepository;
import com.virnect.workspace.dao.workspacepermission.WorkspacePermissionRepository;
import com.virnect.workspace.dao.workspacerole.WorkspaceRoleRepository;
import com.virnect.workspace.dao.workspaceuser.WorkspaceUserRepository;
import com.virnect.workspace.dao.workspaceuserpermission.WorkspaceUserPermissionRepository;
import com.virnect.workspace.domain.workspace.Role;
import com.virnect.workspace.domain.workspace.UserType;
import com.virnect.workspace.domain.workspace.Workspace;
import com.virnect.workspace.domain.workspace.WorkspaceRole;
import com.virnect.workspace.domain.workspace.WorkspaceUserPermission;
import com.virnect.workspace.dto.request.MemberUpdateRequest;
import com.virnect.workspace.dto.request.WorkspaceInviteRequest;
import com.virnect.workspace.application.license.dto.MyLicenseInfoListResponse;
import com.virnect.workspace.application.license.dto.MyLicenseInfoResponse;
import com.virnect.workspace.application.user.dto.request.UserInfoModifyRequest;
import com.virnect.workspace.application.user.dto.response.UserInfoRestResponse;
import com.virnect.workspace.event.message.MailContextHandler;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.common.mapper.rest.RestMapStruct;
import com.virnect.workspace.global.error.ErrorCode;

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
	private final WorkspaceRepository workspaceRepository;
	private final WorkspaceRoleRepository workspaceRoleRepository;
	private final WorkspaceUserPermissionRepository workspaceUserPermissionRepository;
	private final UserRestServiceHandler userRestServiceHandler;

	public OffWorkspaceUserServiceImpl(
		WorkspaceRepository workspaceRepository,
		WorkspaceUserRepository workspaceUserRepository,
		WorkspaceRoleRepository workspaceRoleRepository,
		WorkspaceUserPermissionRepository workspaceUserPermissionRepository,
		MessageSource messageSource,
		LicenseRestService licenseRestService,
		RestMapStruct restMapStruct,
		ApplicationEventPublisher applicationEventPublisher,
		MailContextHandler mailContextHandler,
		WorkspacePermissionRepository workspacePermissionRepository,
		RemoteRestService remoteRestService,
		UserRestServiceHandler userRestServiceHandler
	) {
		super(workspaceRepository, workspaceUserRepository, workspaceRoleRepository, workspaceUserPermissionRepository,
			messageSource, licenseRestService, restMapStruct, applicationEventPublisher, mailContextHandler,
			workspacePermissionRepository, remoteRestService, userRestServiceHandler
		);
		this.workspaceRepository = workspaceRepository;
		this.workspaceRoleRepository = workspaceRoleRepository;
		this.workspaceUserPermissionRepository = workspaceUserPermissionRepository;
		this.userRestServiceHandler = userRestServiceHandler;
	}

	@Override
	public ApiResponse<Boolean> reviseMemberInfo(
		String workspaceId, MemberUpdateRequest memberUpdateRequest, Locale locale
	) {
		// 변경 대상 유저 정보 조회
		UserInfoRestResponse updateUserInfo = userRestServiceHandler.getUserRequest(memberUpdateRequest.getUserId());
		if (updateUserInfo.isEmtpy()) {
			throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND);
		}

		// 변경 요청 유저 permission 조회
		WorkspaceUserPermission requestUserPermission = workspaceUserPermissionRepository.findWorkspaceUserPermission(
			workspaceId, memberUpdateRequest.getRequestUserId())
			.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));
		// 변경 대상 유저 permission 조회
		WorkspaceUserPermission updateUserPermission = workspaceUserPermissionRepository.findWorkspaceUserPermission(
			workspaceId, memberUpdateRequest.getUserId())
			.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));

		//1. 사용자 닉네임 변경
		if (StringUtils.hasText(memberUpdateRequest.getNickname()) && !updateUserInfo.getNickname()
			.equals(memberUpdateRequest.getNickname())) {
			log.info("[REVISE MEMBER INFO] User nickname update. nickname : {}", memberUpdateRequest.getNickname());
			//1-1. 유저 타입 확인
			if (updateUserInfo.getUserType() == UserType.USER) {
				throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_INFO_UPDATE_USER_TYPE);
			}
			//1-2. 요청 유저 권한 확인
			if (!memberUpdateRequest.isUserSelfUpdateRequest()
				&& requestUserPermission.getWorkspaceRole().getRole() != Role.MASTER) {
				throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
			}
			//1-3. 변경 요청
			UserInfoRestResponse userInfoRestResponse = userRestServiceHandler.modifyUserRequest(
				memberUpdateRequest.getUserId(), new UserInfoModifyRequest(memberUpdateRequest.getNickname()));
			if (userInfoRestResponse.isEmtpy()) {
				throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_INFO_UPDATE);
			}
		}

		//2. 사용자 권한 변경
		if (StringUtils.hasText(memberUpdateRequest.getRole()) && !updateUserPermission.getWorkspaceRole()
			.getRole()
			.name()
			.equals(memberUpdateRequest.getRole())) {
			log.info(
				"[REVISE MEMBER INFO] User role update. current role : {}, update role : {} ",
				updateUserPermission.getWorkspaceRole().getRole(),
				memberUpdateRequest.getRole()
			);
			//2-1. 유저 타입 확인
			if (updateUserInfo.getUserType() == UserType.GUEST_USER) {
				throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_INFO_UPDATE_USER_TYPE);
			}
			//2-2. 요청 유저 권한 확인
			if (!memberUpdateRequest.isUserSelfUpdateRequest()
				&& requestUserPermission.getWorkspaceRole().getRole() != Role.MASTER) {
				throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
			}
			//2-3. 권한 변경
			WorkspaceRole updateWorkspaceRole = workspaceRoleRepository.findByRole(
				Role.valueOf(memberUpdateRequest.getRole()))
				.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_ROLE_NOT_FOUND));
			updateUserPermission.setWorkspaceRole(updateWorkspaceRole);
			workspaceUserPermissionRepository.save(updateUserPermission);

			//2-4. 메일 발송
			UserInfoRestResponse masterUserInfo = userRestServiceHandler.getUserRequest(
				updateUserPermission.getWorkspaceUser().getWorkspace().getUserId());
			if (masterUserInfo.isEmtpy()) {
				throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND);
			}

			//2-5. 히스토리 저장
			Workspace workspace = workspaceRepository.findByUuid(workspaceId)
				.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
			saveUserRoleUpdateHistory(updateWorkspaceRole, masterUserInfo, updateUserInfo,
				memberUpdateRequest.getRequestUserId(), locale, workspace
			);
		}

		//3. 사용자 제품 라이선스 변경
		MyLicenseInfoListResponse myLicenseInfoListResponse = getMyLicenseInfoRequestHandler(
			workspaceId, memberUpdateRequest.getUserId());
		List<String> currentProductList = myLicenseInfoListResponse.getLicenseInfoList()
			.stream()
			.map(MyLicenseInfoResponse::getProductName)
			.collect(Collectors.toList());
		if (memberUpdateRequest.existLicenseUpdate(currentProductList)) {
			log.info(
				"[REVISE MEMBER INFO] User License update. current license : [{}]",
				org.apache.commons.lang.StringUtils.join(currentProductList, ",")
			);
			//3-1. 유저 타입 확인
			if (updateUserInfo.getUserType() == UserType.GUEST_USER) {
				throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_INFO_UPDATE_USER_TYPE);
			}
			//3-2. 요청 유저 권한 확인
			if (!memberUpdateRequest.isUserSelfUpdateRequest()) {
				if (updateUserPermission.getWorkspaceRole().getRole() == Role.MASTER) {
					throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_INFO_UPDATE_MASTER_PLAN);
				}
				if (!isMasterOrManagerRole(requestUserPermission.getWorkspaceRole())) {
					throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
				}
				if (isBothManagerRole(
					requestUserPermission.getWorkspaceRole(), updateUserPermission.getWorkspaceRole())) {
					throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
				}
			}

			//3-3. 제품 라이선스 부여/해제 요청
			List<String> addedProductList = new ArrayList<>(), removedProductList = new ArrayList<>();
			if (memberUpdateRequest.getLicenseRemote() != null && memberUpdateRequest.getLicenseRemote()
				&& !currentProductList.contains("REMOTE")) {
				grantWorkspaceLicenseToUser(workspaceId, updateUserInfo.getUuid(), "REMOTE");
				addedProductList.add("REMOTE");
			}
			if (memberUpdateRequest.getLicenseRemote() != null && !memberUpdateRequest.getLicenseRemote()
				&& currentProductList.contains("REMOTE")) {
				revokeWorkspaceLicenseToUser(workspaceId, updateUserInfo.getUuid(), "REMOTE");
				removedProductList.add("REMOTE");
			}
			if (memberUpdateRequest.getLicenseMake() != null && memberUpdateRequest.getLicenseMake()
				&& !currentProductList.contains("MAKE")) {
				grantWorkspaceLicenseToUser(workspaceId, updateUserInfo.getUuid(), "MAKE");
				addedProductList.add("MAKE");
			}

			if (memberUpdateRequest.getLicenseMake() != null && !memberUpdateRequest.getLicenseMake()
				&& currentProductList.contains("MAKE")) {
				revokeWorkspaceLicenseToUser(workspaceId, updateUserInfo.getUuid(), "MAKE");
				removedProductList.add("MAKE");
			}
			if (memberUpdateRequest.getLicenseView() != null && memberUpdateRequest.getLicenseView()
				&& !currentProductList.contains("VIEW")) {
				grantWorkspaceLicenseToUser(workspaceId, updateUserInfo.getUuid(), "VIEW");
				addedProductList.add("VIEW");
			}

			if (memberUpdateRequest.getLicenseView() != null && !memberUpdateRequest.getLicenseView()
				&& currentProductList.contains("VIEW")) {
				revokeWorkspaceLicenseToUser(workspaceId, updateUserInfo.getUuid(), "VIEW");
				removedProductList.add("VIEW");
			}

			//3-4. 라이선스 변경 히스토리 저장
			UserInfoRestResponse requestUserInfo = userRestServiceHandler.getUserRequest(
				memberUpdateRequest.getRequestUserId());
			if (requestUserInfo.isEmtpy()) {
				throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND);
			}
			Workspace workspace = workspaceRepository.findByUuid(workspaceId)
				.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
			saveUserLicenseUpdateHistory(
				addedProductList, removedProductList, requestUserInfo, updateUserInfo, locale, workspace);

			log.info(
				"[REVISE MEMBER INFO] Revise License Result Info. Removed License Product Info >> [{}], Added License Product Info >> [{}].",
				org.apache.commons.lang.StringUtils.join(removedProductList, ","),
				org.apache.commons.lang.StringUtils.join(addedProductList, ",")
			);

			//3-5. 라이선스 변경 성공 메일 전송
		}
		return new ApiResponse<>(true);
	}

	@Override
	public ApiResponse<Boolean> inviteWorkspace(
		String workspaceId, WorkspaceInviteRequest workspaceInviteRequest, Locale locale
	) {
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

}
