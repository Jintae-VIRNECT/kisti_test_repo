package com.virnect.workspace.application.workspaceuser;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.dao.setting.WorkspaceCustomSettingRepository;
import com.virnect.workspace.dao.workspace.*;
import com.virnect.workspace.domain.workspace.Role;
import com.virnect.workspace.domain.workspace.Workspace;
import com.virnect.workspace.domain.workspace.WorkspaceRole;
import com.virnect.workspace.domain.workspace.WorkspaceUserPermission;
import com.virnect.workspace.dto.request.MemberUpdateRequest;
import com.virnect.workspace.dto.request.WorkspaceInviteRequest;
import com.virnect.workspace.dto.rest.MyLicenseInfoListResponse;
import com.virnect.workspace.dto.rest.MyLicenseInfoResponse;
import com.virnect.workspace.dto.rest.UserInfoModifyRequest;
import com.virnect.workspace.dto.rest.UserInfoRestResponse;
import com.virnect.workspace.event.history.HistoryAddEvent;
import com.virnect.workspace.event.mail.MailContextHandler;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.common.mapper.rest.RestMapStruct;
import com.virnect.workspace.global.error.ErrorCode;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
	private final MessageSource messageSource;
	private final ApplicationEventPublisher applicationEventPublisher;

	public OffWorkspaceUserServiceImpl(
		WorkspaceRepository workspaceRepository, WorkspaceUserRepository workspaceUserRepository,
		WorkspaceRoleRepository workspaceRoleRepository,
		WorkspaceUserPermissionRepository workspaceUserPermissionRepository, UserRestService userRestService,
		MessageSource messageSource, LicenseRestService licenseRestService, RestMapStruct restMapStruct,
		ApplicationEventPublisher applicationEventPublisher,
		WorkspaceCustomSettingRepository workspaceCustomSettingRepository, MailContextHandler mailContextHandler,
		WorkspacePermissionRepository workspacePermissionRepository
	) {
		super(
			workspaceRepository, workspaceUserRepository, workspaceRoleRepository, workspaceUserPermissionRepository,
			userRestService, messageSource, licenseRestService, restMapStruct, applicationEventPublisher,
			workspaceCustomSettingRepository, mailContextHandler, workspacePermissionRepository
		);
		this.workspaceRepository = workspaceRepository;
		this.workspaceUserRepository = workspaceUserRepository;
		this.workspaceRoleRepository = workspaceRoleRepository;
		this.workspacePermissionRepository = workspacePermissionRepository;
		this.workspaceUserPermissionRepository = workspaceUserPermissionRepository;
		this.userRestService = userRestService;
		this.licenseRestService = licenseRestService;
		this.restMapStruct = restMapStruct;
		this.workspaceCustomSettingRepository = workspaceCustomSettingRepository;
		this.messageSource = messageSource;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Override
	@Transactional
	public ApiResponse<Boolean> reviseMemberInfo(
		String workspaceId, MemberUpdateRequest memberUpdateRequest, Locale locale
	) {
		String requestUserId = MDC.get("userId") == null ? memberUpdateRequest.getRequestUserId() : MDC.get("userId");
		log.info(
			"[REVISE MEMBER INFO][{}] workspace uuid >> [{}], Update request info >> [{}].", requestUserId, workspaceId,
			memberUpdateRequest.toString()
		);
		//1-1. 요청 워크스페이스 조회
		Workspace workspace = workspaceRepository.findByUuid(workspaceId)
			.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));

		//1-2. 요청 유저 권한 조회
		WorkspaceUserPermission requestUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(
			workspace, requestUserId)
			.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));
		WorkspaceUserPermission updateUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(
			workspace, memberUpdateRequest.getUserId())
			.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));

		UserInfoRestResponse masterUser = getUserInfoRequest(workspace.getUserId());
		UserInfoRestResponse updateUser = getUserInfoRequest(memberUpdateRequest.getUserId());//수정 대상 유저
		UserInfoRestResponse requestUser = getUserInfoRequest(requestUserId);

		//2. 사용자 닉네임 변경
		if (StringUtils.hasText(memberUpdateRequest.getNickname()) && !updateUser.getNickname()
			.equals(memberUpdateRequest.getNickname())) {
			log.info(
				"[REVISE MEMBER INFO] Revise User Nickname Info. Current User Nickname >> [{}], Updated User Nickname >> [{}].",
				updateUser.getNickname(), memberUpdateRequest.getNickname()
			);
			//2-1. 유저 타입 확인
			if (updateUser.getUserType().equals("USER")) {
				throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_INFO_UPDATE_USER_TYPE);
			}
			//2-2. 권한 확인
			checkUserInfoUpdatePermission(requestUserPermission, updateUserPermission, workspaceId);
			//2-3. 변경 요청
			modifyUserInfoByUserId(
				memberUpdateRequest.getUserId(), new UserInfoModifyRequest(memberUpdateRequest.getNickname()));

		}
		//3. 사용자 권한 변경
		if (StringUtils.hasText(memberUpdateRequest.getRole()) && !updateUserPermission.getWorkspaceRole()
			.getRole()
			.name()
			.equals(memberUpdateRequest.getRole())) {
			log.info(
				"[REVISE MEMBER INFO] Revise User Role Info. Current User Role >> [{}], Updated User Role >> [{}].",
				updateUserPermission.getWorkspaceRole().getRole(), memberUpdateRequest.getRole()
			);
			//3-1. 유저 타입 확인
			if (updateUser.getUserType().equals("GUEST_USER")) {
				throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_INFO_UPDATE_USER_TYPE);
			}

			//3-2. 요청 유저 권한 체크
			checkUserRoleUpdatePermission(requestUserPermission, updateUserPermission, workspaceId);
			//3-3. 권한 정보 변경
			//1-3. 요청 권한 조회
			WorkspaceRole workspaceRole = workspaceRoleRepository.findByRole(
				Role.valueOf(memberUpdateRequest.getRole()))
				.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_ROLE_NOT_FOUND));
			updateUserPermission.setWorkspaceRole(workspaceRole);
			workspaceUserPermissionRepository.save(updateUserPermission);

			//3-4. 변경 성공 메일 발송 - 온프라미스는 하지 않음

			//3-5. 변경 성공 히스토리 저장
			String message;
			if (updateUserPermission.getWorkspaceRole().getRole() == Role.MANAGER) {
				message = messageSource.getMessage(
					"WORKSPACE_SET_MANAGER", new String[] {masterUser.getNickname(), updateUser.getNickname()}, locale);
			} else {
				message = messageSource.getMessage(
					"WORKSPACE_SET_MEMBER", new String[] {masterUser.getNickname(), updateUser.getNickname()}, locale);
			}
			applicationEventPublisher.publishEvent(
				new HistoryAddEvent(message, requestUserId, workspace));

			//3-6. 권한이 변경된 사용자 캐싱 데이터 삭제
			//applicationEventPublisher.publishEvent(new UserWorkspacesDeleteEvent(memberUpdateRequest.getUserId()));
		}

		//4. 사용자 제품 라이선스 유형 변경
		MyLicenseInfoListResponse myLicenseInfoListResponse = getMyLicenseInfoRequestHandler(
			workspaceId, memberUpdateRequest.getUserId());
		List<String> currentProductList = myLicenseInfoListResponse.getLicenseInfoList()
			.stream()
			.map(MyLicenseInfoResponse::getProductName)
			.collect(Collectors.toList());

		if (memberUpdateRequest.existLicenseUpdate(currentProductList)) {
			log.info(
				"[REVISE MEMBER INFO] Revise License Info. Current License Product Info >> [{}], ",
				org.apache.commons.lang.StringUtils.join(currentProductList, ",")
			);
			//4-1. 유저 타입 체크
			if (updateUser.getUserType().equals("GUEST_USER")
				|| updateUserPermission.getWorkspaceRole().getRole() == Role.GUEST) {
				throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_INFO_UPDATE_USER_TYPE);
			}
			//4-2. 요청 유저 권한 체크
			checkUserLicenseUpdatePermission(requestUserPermission, updateUserPermission, workspaceId);

			//4-3. 제품 라이선스 부여/해제 요청
			List<String> addedProductList = new ArrayList<>();
			List<String> removedProductList = new ArrayList<>();
			if (memberUpdateRequest.getLicenseRemote() != null && memberUpdateRequest.getLicenseRemote()
				&& !currentProductList.contains("REMOTE")) {
				grantWorkspaceLicenseToUser(workspace.getUuid(), updateUser.getUuid(), "REMOTE");
				addedProductList.add("REMOTE");
			}

			if (memberUpdateRequest.getLicenseRemote() != null && !memberUpdateRequest.getLicenseRemote()
				&& currentProductList.contains("REMOTE")) {
				revokeWorkspaceLicenseToUser(workspace.getUuid(), updateUser.getUuid(), "REMOTE");
				removedProductList.add("REMOTE");
			}

			if (memberUpdateRequest.getLicenseMake() != null && memberUpdateRequest.getLicenseMake()
				&& !currentProductList.contains("MAKE")) {
				grantWorkspaceLicenseToUser(workspace.getUuid(), updateUser.getUuid(), "MAKE");
				addedProductList.add("MAKE");
			}

			if (memberUpdateRequest.getLicenseMake() != null && !memberUpdateRequest.getLicenseMake()
				&& currentProductList.contains("MAKE")) {
				revokeWorkspaceLicenseToUser(workspace.getUuid(), updateUser.getUuid(), "MAKE");
				removedProductList.add("MAKE");
			}
			if (memberUpdateRequest.getLicenseView() != null && memberUpdateRequest.getLicenseView()
				&& !currentProductList.contains("VIEW")) {
				grantWorkspaceLicenseToUser(workspace.getUuid(), updateUser.getUuid(), "VIEW");
				addedProductList.add("VIEW");
			}

			if (memberUpdateRequest.getLicenseView() != null && !memberUpdateRequest.getLicenseView()
				&& currentProductList.contains("VIEW")) {
				revokeWorkspaceLicenseToUser(workspace.getUuid(), updateUser.getUuid(), "VIEW");
				removedProductList.add("VIEW");
			}

			//4-4. 라이선스 변경 히스토리 저장
			if (!addedProductList.isEmpty()) {
				String message = messageSource.getMessage(
					"WORKSPACE_GRANT_LICENSE", new String[] {requestUser.getNickname(), updateUser.getNickname(),
						org.apache.commons.lang.StringUtils.join(addedProductList, ",")}, locale);
				applicationEventPublisher.publishEvent(new HistoryAddEvent(message, updateUser.getUuid(), workspace));
			}

			if (!removedProductList.isEmpty()) {
				String message = messageSource.getMessage(
					"WORKSPACE_REVOKE_LICENSE", new String[] {requestUser.getNickname(), updateUser.getNickname(),
						org.apache.commons.lang.StringUtils.join(removedProductList, ",")}, locale);
				applicationEventPublisher.publishEvent(new HistoryAddEvent(message, updateUser.getUuid(), workspace));
			}
			log.info(
				"[REVISE MEMBER INFO] Revise License Result Info. Removed License Product Info >> [{}], Added License Product Info >> [{}].",
				org.apache.commons.lang.StringUtils.join(removedProductList, ","),
				org.apache.commons.lang.StringUtils.join(addedProductList, ",")
			);

			//4-5. 라이선스 변경 성공 메일 전송 - 온프라미스는 하지 않음.
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
}
