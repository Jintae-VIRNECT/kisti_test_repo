package com.virnect.workspace.application.workspaceuser;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.message.MessageRestService;
import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.dao.cache.UserInviteRepository;
import com.virnect.workspace.dao.setting.WorkspaceCustomSettingRepository;
import com.virnect.workspace.dao.workspace.*;
import com.virnect.workspace.domain.redis.UserInvite;
import com.virnect.workspace.domain.setting.SettingName;
import com.virnect.workspace.domain.setting.SettingValue;
import com.virnect.workspace.domain.setting.WorkspaceCustomSetting;
import com.virnect.workspace.domain.workspace.*;
import com.virnect.workspace.dto.request.MemberUpdateRequest;
import com.virnect.workspace.dto.request.WorkspaceInviteRequest;
import com.virnect.workspace.dto.rest.*;
import com.virnect.workspace.event.history.HistoryAddEvent;
import com.virnect.workspace.event.invite.InviteSessionDeleteEvent;
import com.virnect.workspace.event.message.MailContextHandler;
import com.virnect.workspace.event.message.MailSendEvent;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.common.RedirectProperty;
import com.virnect.workspace.global.common.mapper.rest.RestMapStruct;
import com.virnect.workspace.global.constant.Mail;
import com.virnect.workspace.global.constant.Permission;
import com.virnect.workspace.global.constant.RedirectPath;
import com.virnect.workspace.global.constant.UUIDType;
import com.virnect.workspace.global.error.ErrorCode;
import com.virnect.workspace.global.util.RandomStringTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.context.Context;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Project: PF-Workspace
 * DATE: 2021-02-02
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@Profile("!onpremise")
public class OnWorkspaceUserServiceImpl extends WorkspaceUserService {
	private final WorkspaceRepository workspaceRepository;
	private final WorkspaceUserRepository workspaceUserRepository;
	private final WorkspaceRoleRepository workspaceRoleRepository;
	private final WorkspacePermissionRepository workspacePermissionRepository;
	private final WorkspaceUserPermissionRepository workspaceUserPermissionRepository;
	private final UserRestService userRestService;
	private final UserInviteRepository userInviteRepository;
	private final MessageSource messageSource;
	private final LicenseRestService licenseRestService;
	private final RedirectProperty redirectProperty;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final WorkspaceCustomSettingRepository workspaceCustomSettingRepository;
	private final MailContextHandler mailContextHandler;
	private final RestMapStruct restMapStruct;
	private static final int MAX_JOIN_WORKSPACE_AMOUNT = 49;//최대 참여 가능한 워크스페이스 수

	public OnWorkspaceUserServiceImpl(
		WorkspaceRepository workspaceRepository, WorkspaceUserRepository workspaceUserRepository,
		WorkspaceRoleRepository workspaceRoleRepository,
		WorkspaceUserPermissionRepository workspaceUserPermissionRepository, UserRestService userRestService,
		MessageSource messageSource, LicenseRestService licenseRestService, RestMapStruct restMapStruct,
		ApplicationEventPublisher applicationEventPublisher,
		WorkspaceCustomSettingRepository workspaceCustomSettingRepository, MailContextHandler mailContextHandler,
		WorkspacePermissionRepository workspacePermissionRepository, UserInviteRepository userInviteRepository,
		RedirectProperty redirectProperty,
		MessageRestService messageRestService
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
		this.userInviteRepository = userInviteRepository;
		this.messageSource = messageSource;
		this.licenseRestService = licenseRestService;
		this.redirectProperty = redirectProperty;
		this.applicationEventPublisher = applicationEventPublisher;
		this.workspaceCustomSettingRepository = workspaceCustomSettingRepository;
		this.mailContextHandler = mailContextHandler;
		this.restMapStruct = restMapStruct;
	}

	@Override
	@Transactional
	public ApiResponse<Boolean> reviseMemberInfo(
		String workspaceId, MemberUpdateRequest memberUpdateRequest, Locale locale
	) {
		String requestUserId = MDC.get("userId") == null ? memberUpdateRequest.getRequestUserId() : MDC.get("userId");
		log.info(
			"[REVISE MEMBER INFO][{}][{}] Update request info >> [{}].", requestUserId, workspaceId,
			memberUpdateRequest.toString()
		);
		//1-1. 요청 워크스페이스 조회
		Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));

		//1-2. 요청 유저 권한 조회
		WorkspaceUserPermission requestUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, requestUserId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));
		WorkspaceUserPermission updateUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, memberUpdateRequest.getUserId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));

		UserInfoRestResponse masterUser = getUserInfoRequest(workspace.getUserId());
		UserInfoRestResponse updateUser = getUserInfoRequest(memberUpdateRequest.getUserId());
		UserInfoRestResponse requestUser = getUserInfoRequest(requestUserId);

		//2. 사용자 닉네임 변경
		if (StringUtils.hasText(memberUpdateRequest.getNickname())) {
			//2-1. 닉네임 변경이 가능한 유저인지 타입 체크
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
		if (StringUtils.hasText(memberUpdateRequest.getRole())) {
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

			//3-4. 변경 성공 메일 발송
			Context context = mailContextHandler.getWorkspaceUserPermissionUpdateContext(
				workspace.getName(), masterUser, updateUser, workspaceRole.getRole().toString());
			List<String> receiver = new ArrayList<>();
			receiver.add(updateUser.getEmail());
			applicationEventPublisher.publishEvent(
				new MailSendEvent(context, Mail.WORKSPACE_USER_PERMISSION_UPDATE, locale, receiver));

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

			//4-5. 라이선스 변경 성공 메일 전송
			Context context = mailContextHandler.getWorkspaceUserPlanUpdateContext(
				workspace.getName(), masterUser, updateUser, currentProductList);
			List<String> receiver = new ArrayList<>();
			receiver.add(updateUser.getEmail());
			applicationEventPublisher.publishEvent(
				new MailSendEvent(context, Mail.WORKSPACE_USER_PLAN_UPDATE, locale, receiver));
		}
		return new ApiResponse<>(true);
	}

	@Override
	public ApiResponse<Boolean> inviteWorkspace(
		String workspaceId, WorkspaceInviteRequest workspaceInviteRequest, Locale locale
	) {
		//1-1. 초대 받은 유저 역할 체크 : 마스터 권한을 부여하여 초대할 수 없다.
		if (workspaceInviteRequest.existMasterUserInvite()) {
			throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE);
		}

		//1-2. 초대 요청 유저 역할 체크
		checkWorkspaceInvitePermission(
			workspaceId, workspaceInviteRequest.getUserId(), workspaceInviteRequest.getUserInfoList());

		//1-3. 워크스페이스에 최대 참여 가능한 유저 수 체크
		WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse = getWorkspaceLicensesByWorkspaceId(
			workspaceId);
		checkWorkspaceMaxUserAmount(
			workspaceId, workspaceInviteRequest.getUserInfoList().size(), workspaceLicensePlanInfoResponse);

		//1-4. 제품 라이선스를 부여할 수 있는 지 체크
		checkWorkspaceInviteLicenseProduct(workspaceInviteRequest.getUserInfoList(), workspaceLicensePlanInfoResponse);

		//1-5. 탈퇴한 유저인지 체크
		List<String> invitedUserEmailList = workspaceInviteRequest.getUserInfoList()
			.stream()
			.map(WorkspaceInviteRequest.UserInfo::getEmail)
			.collect(Collectors.toList());
		Map<String, InviteUserInfoResponse> inviteUserInfoResponseMap = checkWorkspaceInvitedUserSecession(
			invitedUserEmailList);

		//1-6. 이미 초대된 유저인지 체크
		checkWorkspaceInvitedUserAlreadyJoin(workspaceId, inviteUserInfoResponseMap);

		//1-7. 이미 최대 참여 가능한 워크스페이스를 넘긴 유저인지 체크
		checkWorkspaceInvitedUserMaxJoin(inviteUserInfoResponseMap);

		// 마스터 유저 정보
		Workspace workspace = workspaceRepository.findByUuid(workspaceId)
			.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
		UserInfoRestResponse materUser = getUserInfoRequest(workspace.getUserId());

		//2. 초대 정보 저장
		workspaceInviteRequest.getUserInfoList().forEach(userInfo -> {
			InviteUserInfoResponse inviteUserResponse = inviteUserInfoResponseMap.get(userInfo.getEmail());
			String sessionCode = RandomStringTokenUtil.generate(UUIDType.INVITE_CODE, 20);
			Optional<UserInvite> userInviteOptional = userInviteRepository.findByWorkspaceIdAndInvitedUserEmail(
				workspaceId, userInfo.getEmail());
			if (userInviteOptional.isPresent()) {
				UserInvite userInvite = userInviteOptional.get();
				userInvite.setRole(userInfo.getRole());
				userInvite.setPlanRemote(userInfo.isPlanRemote());
				userInvite.setPlanMake(userInfo.isPlanMake());
				userInvite.setPlanView(userInfo.isPlanView());
				userInvite.setUpdatedDate(LocalDateTime.now());
				userInvite.setExpireTime(Duration.ofDays(7).getSeconds());
				userInviteRepository.save(userInvite);
				sessionCode = userInvite.getSessionCode();
				log.info("[WORKSPACE INVITE USER] Workspace Invite Info Redis Update >> {}", userInvite.toString());
			} else {
				UserInvite newUserInvite = UserInvite.builder()
					.sessionCode(sessionCode)
					.invitedUserEmail(userInfo.getEmail())
					.invitedUserId(inviteUserResponse.getInviteUserDetailInfo().getUserUUID())
					.requestUserId(workspaceInviteRequest.getUserId())
					.workspaceId(workspaceId)
					.role(userInfo.getRole())
					.planRemote(userInfo.isPlanRemote())
					.planMake(userInfo.isPlanMake())
					.planView(userInfo.isPlanView())
					.planRemoteType(null)
					.planMakeType(null)
					.planViewType(null)
					.invitedDate(LocalDateTime.now())
					.updatedDate(null)
					.expireTime(Duration.ofDays(7).getSeconds())
					.build();
				userInviteRepository.save(newUserInvite);
				log.info("[WORKSPACE INVITE USER] Workspace Invite Info Redis Set >> {}", newUserInvite.toString());
			}

			//3. 초대 메일 전송
			List<String> emailReceiverList = new ArrayList<>();
			emailReceiverList.add(userInfo.getEmail());
			if (inviteUserResponse.isMemberUser()) {
				Context context = mailContextHandler.getWorkspaceInviteContext(
					sessionCode, locale, workspace.getName(), userInfo, inviteUserResponse.getInviteUserDetailInfo(),
					materUser
				);
				applicationEventPublisher.publishEvent(
					new MailSendEvent(context, Mail.WORKSPACE_INVITE, locale, emailReceiverList));
			} else {
				Context context = mailContextHandler.getWorkspaceInviteNonUserContext(
					sessionCode, locale, workspace.getName(), userInfo, materUser);
				applicationEventPublisher.publishEvent(
					new MailSendEvent(context, Mail.WORKSPACE_INVITE_NON_USER, locale, emailReceiverList));
			}
		});
		return new ApiResponse<>(true);
	}

	/**
	 * 워크스페이스 초대 시 부여한 라이선스 검증
	 *
	 * @param userInfoList                     - 초대받은 유저 목록
	 * @param workspaceLicensePlanInfoResponse - 워크스페이스 라이선스 정보
	 */
	private void checkWorkspaceInviteLicenseProduct(
		List<WorkspaceInviteRequest.UserInfo> userInfoList,
		WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse
	) {
		long requestRemoteAmount = userInfoList.stream().filter(WorkspaceInviteRequest.UserInfo::isPlanRemote).count();
		long requestMakeAmount = userInfoList.stream().filter(WorkspaceInviteRequest.UserInfo::isPlanMake).count();
		long requestViewAmount = userInfoList.stream().filter(WorkspaceInviteRequest.UserInfo::isPlanView).count();
        log.info("[WORKSPACE INVITE USER] Request license amount. remote : {}, make : {}, view : {}", requestRemoteAmount, requestMakeAmount, requestViewAmount);
        if (requestRemoteAmount > 0) {
            Integer unUsedRemoteAmount = workspaceLicensePlanInfoResponse.getLicenseProductInfoList()
                    .stream()
                    .filter(licenseProductInfoResponse -> licenseProductInfoResponse.getProductName().equals("REMOTE"))
                    .map(WorkspaceLicensePlanInfoResponse.LicenseProductInfoResponse::getUnUseLicenseAmount)
                    .findFirst()
                    .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE_NON_LICENSE));
            if (requestRemoteAmount > unUsedRemoteAmount) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE_NON_LICENSE);
            }
        }
        if (requestMakeAmount > 0) {
            Integer unUsedMakeAmount = workspaceLicensePlanInfoResponse.getLicenseProductInfoList()
                    .stream()
                    .filter(licenseProductInfoResponse -> licenseProductInfoResponse.getProductName().equals("MAKE"))
                    .map(WorkspaceLicensePlanInfoResponse.LicenseProductInfoResponse::getUnUseLicenseAmount)
                    .findFirst()
                    .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE_NON_LICENSE));
            if (requestMakeAmount > unUsedMakeAmount) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE_NON_LICENSE);
            }
        }
        if (requestViewAmount > 0) {
            Integer unUsedViewAmount = workspaceLicensePlanInfoResponse.getLicenseProductInfoList()
                    .stream()
                    .filter(licenseProductInfoResponse -> licenseProductInfoResponse.getProductName().equals("VIEW"))
                    .map(WorkspaceLicensePlanInfoResponse.LicenseProductInfoResponse::getUnUseLicenseAmount)
                    .findFirst()
                    .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE_NON_LICENSE));
            if (requestViewAmount > unUsedViewAmount) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE_NON_LICENSE);
            }
        }
	}

	/**
	 * 워크스페이스 초대 시 초대받은 유저가 최대 소속 워크스페이스 수를 넘겼는지 체크
	 *
	 * @param inviteUserInfoResponseMap - 초대받은 유저 정보 목록
	 */
	private void checkWorkspaceInvitedUserMaxJoin(Map<String, InviteUserInfoResponse> inviteUserInfoResponseMap) {
		inviteUserInfoResponseMap.values().forEach(inviteUserInfoResponse -> {
			long userIncludedWorkspaceAmount = workspaceUserRepository.countByUserId(
				inviteUserInfoResponse.getInviteUserDetailInfo().getUserUUID());
			if (inviteUserInfoResponse.isMemberUser() && userIncludedWorkspaceAmount + 1 > MAX_JOIN_WORKSPACE_AMOUNT) {
				log.error(
					"[WORKSPACE INVITE USER] maximum join workspace amount : [{}], current user join workspace amount(include request) : [{}]",
					MAX_JOIN_WORKSPACE_AMOUNT, userIncludedWorkspaceAmount + 1
				);
				throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE_MAX_JOIN_USER);
			}
		});
	}

	/**
	 * 워크스페이스 초대 시 초대받은 유저가 이미 해당 워크스페이스의 소속되어 있는지 체크
	 * 단, 비회원이 아닌 경우에만 체크한다.
	 *
	 * @param workspaceId               - 초대한 워크스페이스 식별자
	 * @param inviteUserInfoResponseMap - 초대받은 유저 정보 목록
	 */
	private void checkWorkspaceInvitedUserAlreadyJoin(
		String workspaceId, Map<String, InviteUserInfoResponse> inviteUserInfoResponseMap
	) {
		inviteUserInfoResponseMap.values().forEach(inviteUserInfoResponse -> {
			Optional<WorkspaceUser> invitedWorkspaceUser = workspaceUserRepository.findByUserIdAndWorkspace_Uuid(
				inviteUserInfoResponse.getInviteUserDetailInfo().getUserUUID(), workspaceId);
			if (inviteUserInfoResponse.isMemberUser() && invitedWorkspaceUser.isPresent()) {
				log.error(
					"[WORKSPACE INVITE USER] Invite User is already Workspace user. Invite user is Member >>> [{}], Invite user is Workspace User >>> [{}]",
					inviteUserInfoResponse.isMemberUser(), true
				);
				throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_ALREADY_EXIST);
			}
		});

	}

	/**
	 * 워크스페이스 초대 시 탈퇴한 유저가 있는지 체크
	 *
	 * @param invitedUserEmailList - 워크스페이스 초대 유저 이메일 목록
	 * @return - 탈퇴한 유저가 없는 유저 정보 모록
	 */
	private Map<String, InviteUserInfoResponse> checkWorkspaceInvitedUserSecession(List<String> invitedUserEmailList) {
		Map<String, InviteUserInfoResponse> inviteUserInfoResponseMap = new HashMap<>();
		invitedUserEmailList.forEach(invitedUserEmail -> {
			ApiResponse<InviteUserInfoResponse> inviteUserInfoResponseApiResponse = getInviteUserInfoByEmail(
				invitedUserEmail);
			if (inviteUserInfoResponseApiResponse.getCode() != 200) {
				log.error("[WORKSPACE INVITE USER] Invalid Invited User Info.");
				if (inviteUserInfoResponseApiResponse.getCode() == 5002) {
					throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE_SECESSION_USER);
				}
				throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE);
			}
			InviteUserInfoResponse inviteUserResponse = inviteUserInfoResponseApiResponse.getData();
			inviteUserInfoResponseMap.put(invitedUserEmail, inviteUserResponse);
		});
		return inviteUserInfoResponseMap;
	}

	/**
	 * 워크스페이스 초대 요청 유저의 권한 유효성 체크
	 *
	 * @param workspaceId   - 워크스페이스 식별자
	 * @param requestUserId - 초대 요청 유저 식별자
	 * @param userInfoList  - 초대 대상 유저 역할 리스트
	 */
	private void checkWorkspaceInvitePermission(
		String workspaceId, String requestUserId, List<WorkspaceInviteRequest.UserInfo> userInfoList
	) {
		List<Role> invitedUserRoleList = userInfoList.stream()
			.map(userInfo -> Role.valueOf(userInfo.getRole()))
			.collect(Collectors.toList());
		WorkspaceUserPermission requestUserPermission = workspaceUserPermissionRepository.findWorkspaceUser(
			workspaceId, requestUserId)
			.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION));

		//초대 권한이 설정 정보에 따라 변경됨.
		Optional<WorkspaceCustomSetting> workspaceCustomSettingOptional = workspaceCustomSettingRepository.findByWorkspace_UuidAndSetting_Name(
			workspaceId, SettingName.PUBLIC_USER_MANAGEMENT_ROLE_SETTING);
		if (!workspaceCustomSettingOptional.isPresent()
			|| workspaceCustomSettingOptional.get().getValue() == SettingValue.UNUSED
			|| workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER) {
			// 초대한 사람이 마스터 또는 매니저여야 한다.
			if (requestUserPermission.getWorkspaceRole().getRole() != Role.MASTER
				&& requestUserPermission.getWorkspaceRole().getRole() != Role.MANAGER) {
				throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
			}
			// 매니저 유저는 매니저 유저를 초대할 수 없다.
			if (requestUserPermission.getWorkspaceRole().getRole() == Role.MANAGER && invitedUserRoleList.stream()
				.anyMatch(s -> s.equals(Role.MANAGER.name()))) {
				throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
			}
		}
		if (workspaceCustomSettingOptional.isPresent()) {
			WorkspaceCustomSetting workspaceCustomSetting = workspaceCustomSettingOptional.get();
			// 마스터 유저만 초대 할 수 있다.
			if (workspaceCustomSetting.getValue() == SettingValue.MASTER
				&& requestUserPermission.getWorkspaceRole().getRole() != Role.MASTER) {
				throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
			}
			//멤버유저도 초대할 수 있다. 단 상위 유저는 초대할 수 없다.
			//TODO workspace setting 추가 될때 상위 유저 확인해서 block
			if (workspaceCustomSetting.getValue() == SettingValue.MASTER_OR_MANAGER_OR_MEMBER) {
				if (requestUserPermission.getWorkspaceRole().getRole() != Role.MASTER
					&& requestUserPermission.getWorkspaceRole().getRole() != Role.MANAGER
					&& requestUserPermission.getWorkspaceRole().getRole() != Role.MEMBER) {
					throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
				}
			}
		}
	}

	private ApiResponse<InviteUserInfoResponse> getInviteUserInfoByEmail(String email) {
		ApiResponse<InviteUserInfoResponse> inviteUserInfoResponseApiResponse = userRestService.getInviteUserInfoByEmail(
			email);
		if (inviteUserInfoResponseApiResponse.getCode() != 200) {
			log.error(
				"[GET INVITE USER INFO BY EMAIL] response code : {}, response message : {}",
				inviteUserInfoResponseApiResponse.getCode(), inviteUserInfoResponseApiResponse.getMessage()
			);
		}
		return inviteUserInfoResponseApiResponse;
	}

	/**
	 * 워크스페이스 초대 수락
	 *
	 * @param sessionCode - 초대 세션 식별자
	 * @param lang        - 사용자 언어
	 * @return - 리다이렉트 url
	 */
	public RedirectView inviteWorkspaceAccept(String sessionCode, String lang) {
		Locale locale = Locale.KOREAN;
		if (StringUtils.hasText(lang)) {
			locale = new Locale(lang, "");
		}

		//1-1. 초대 세션 유효성 체크
		Optional<UserInvite> optionalUserInvite = userInviteRepository.findById(sessionCode);
		if (!optionalUserInvite.isPresent()) {
			log.error(
				"[WORKSPACE INVITE ACCEPT] Workspace invite session Info Not found. session code >> [{}]", sessionCode);
			return redirectView(redirectProperty.getConsoleWeb() + RedirectPath.WORKSPACE_INVITE_FAIL.getValue());
		}
		UserInvite userInvite = optionalUserInvite.get();
		log.info("[WORKSPACE INVITE ACCEPT] Workspace invite session Info >> [{}]", userInvite.toString());

		//1-2. 초대받은 유저가 유효한지 체크
		ApiResponse<InviteUserInfoResponse> inviteUserInfoResponseApiResponse = getInviteUserInfoByEmail(
			userInvite.getInvitedUserEmail());
		if (inviteUserInfoResponseApiResponse.getCode() != 200) {
			log.error("[WORKSPACE INVITE ACCEPT] Invalid Invited User Info.");
			//탈퇴한 유저의 캐싱은 삭제, 이외의 유저는 보류.
			if (inviteUserInfoResponseApiResponse.getCode() == 5002) {
				applicationEventPublisher.publishEvent(new InviteSessionDeleteEvent(userInvite.getSessionCode()));
			}
			return redirectView(redirectProperty.getConsoleWeb() + RedirectPath.WORKSPACE_INVITE_FAIL.getValue());
		}

		InviteUserInfoResponse inviteUserResponse = inviteUserInfoResponseApiResponse.getData();
		//1-3. 초대받은 유저가 비회원인지 체크
		if (!inviteUserResponse.isMemberUser()) {
			log.info("[WORKSPACE INVITE ACCEPT] Invited User is Member >> [{}]", inviteUserResponse.isMemberUser());
			return redirectView(
				redirectProperty.getTermsWeb() + "?inviteSession=" + sessionCode + "&lang=" + lang + "&email="
					+ userInvite.getInvitedUserEmail());
		}

		//1-4. 비회원인경우 초대 session정보에 uuid가 null값이므로 회원가입 후에 수락하게 하고 user서버에서 회원정보를 가져온다.(회원가입후에 수락으로 리다이렉트한 경우 이 과정이 필요하다.)
		InviteUserDetailInfoResponse inviteUserInfo = inviteUserResponse.getInviteUserDetailInfo();
		userInvite.setInvitedUserEmail(inviteUserInfo.getEmail());
		userInvite.setInvitedUserId(inviteUserInfo.getUserUUID());
		userInviteRepository.save(userInvite);

		Workspace workspace = workspaceRepository.findByUuid(userInvite.getWorkspaceId())
			.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
		UserInfoRestResponse masterUserInfo = getUserInfoRequest(workspace.getUserId());

		//1-5. 유저가 최대 참여 가능한 워크스페이스 수 체크
		long maxJoinWorkspaceAmount = workspaceUserRepository.countByWorkspace_Uuid(workspace.getUuid());
		if (maxJoinWorkspaceAmount + 1 > MAX_JOIN_WORKSPACE_AMOUNT) {
			log.error(
				"[WORKSPACE INVITE ACCEPT] Over Max Workspace join amount. max join workspace Amount >> [{}], current workspace joined amount(include master user) >> [{}]"
				, MAX_JOIN_WORKSPACE_AMOUNT, maxJoinWorkspaceAmount);

			WorkspaceInviteProcess workspaceInviteProcess = WorkspaceInviteProcess.builder()
				.applicationEventPublisher(applicationEventPublisher)
				.inviteSessionDeleteEvent(new InviteSessionDeleteEvent(userInvite.getSessionCode()))
				.mailSendEvent(new MailSendEvent(
					mailContextHandler.getWorkspaceOverJoinContext(
						workspace.getName(), masterUserInfo, inviteUserInfo, userInvite.isPlanRemote(),
						userInvite.isPlanMake(), userInvite.isPlanView()
					),
					Mail.WORKSPACE_OVER_JOIN_FAIL,
					locale,
					getMasterAndManagerEmail(workspace, masterUserInfo)
				))
				.redirectUrl(redirectProperty.getWorkstationWeb() + RedirectPath.WORKSPACE_OVER_JOIN_FAIL.getValue())
				.build();
			return workspaceInviteProcess.process();
		}

		//1-6. 워크스페이스에서 최대 참여 가능한 멤버 수 체크
		WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse = getWorkspaceLicensesByWorkspaceId(
			workspace.getUuid());
		try {
			checkWorkspaceMaxUserAmount(workspace.getUuid(), 1, workspaceLicensePlanInfoResponse);
		} catch (WorkspaceException e) {
			WorkspaceInviteProcess workspaceInviteProcess = WorkspaceInviteProcess.builder()
				.applicationEventPublisher(applicationEventPublisher)
				.inviteSessionDeleteEvent(new InviteSessionDeleteEvent(userInvite.getSessionCode()))
				.mailSendEvent(new MailSendEvent(
					mailContextHandler.getWorkspaceOverMaxUserContext(
						workspace.getName(), masterUserInfo, inviteUserInfo, userInvite.isPlanRemote(),
						userInvite.isPlanMake(), userInvite.isPlanView()
					),
					Mail.WORKSPACE_OVER_MAX_USER_FAIL,
					locale,
					getMasterAndManagerEmail(workspace, masterUserInfo)
				))
				.redirectUrl(
					redirectProperty.getWorkstationWeb() + RedirectPath.WORKSPACE_OVER_MAX_USER_FAIL.getValue())
				.build();
			return workspaceInviteProcess.process();
		}
		//2. 초대 수락 수행 - 플랜 할당. 할당에 실패하면 초대 로직 원복.
		List<String> requestPlanList = getRequestWorkspaceLicense(
			userInvite.isPlanRemote(), userInvite.isPlanMake(), userInvite.isPlanView());
		if (!requestPlanList.isEmpty()) {
			List<String> failPlanList = executeGrantWorkspaceLicenseToUser(
				workspace.getUuid(), inviteUserInfo.getUserUUID(), requestPlanList);
			if (!failPlanList.isEmpty()) {
				requestPlanList.removeAll(failPlanList);
				requestPlanList.forEach(
					productName -> revokeWorkspaceLicenseToUser(workspace.getUuid(), inviteUserInfo.getUserUUID(),
						productName
					));
				WorkspaceInviteProcess workspaceInviteAcceptProcess = WorkspaceInviteProcess.builder()
					.applicationEventPublisher(applicationEventPublisher)
					.inviteSessionDeleteEvent(new InviteSessionDeleteEvent(userInvite.getSessionCode()))
					.mailSendEvent(new MailSendEvent(
						mailContextHandler.getWorkspaceOverPlanContext(
							workspace.getName(), masterUserInfo, inviteUserInfo, requestPlanList, failPlanList),
						Mail.WORKSPACE_OVER_PLAN_FAIL,
						locale,
						new ArrayList<>()
					))
					.redirectUrl(
						redirectProperty.getWorkstationWeb() + RedirectPath.WORKSPACE_OVER_PLAN_FAIL.getValue())
					.build();
				return workspaceInviteAcceptProcess.process();
			}
		}
		//워크스페이스 소속 넣기 (workspace_user)
		WorkspaceUser workspaceUser = WorkspaceUser.builder()
			.workspace(workspace)
			.userId(userInvite.getInvitedUserId())
			.build();
		workspaceUserRepository.save(workspaceUser);

		//워크스페이스 권한 부여하기 (workspace_user_permission)
		WorkspaceRole workspaceRole = workspaceRoleRepository.findByRole(Role.valueOf(userInvite.getRole()))
			.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		WorkspacePermission workspacePermission = workspacePermissionRepository.findById(Permission.ALL.getValue())
			.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		WorkspaceUserPermission newWorkspaceUserPermission = WorkspaceUserPermission.builder()
			.workspaceUser(workspaceUser)
			.workspaceRole(workspaceRole)
			.workspacePermission(workspacePermission)
			.build();
		workspaceUserPermissionRepository.save(newWorkspaceUserPermission);

		String message;
		if (Role.valueOf(userInvite.getRole()) == Role.MANAGER) {
			message = messageSource.getMessage(
				"WORKSPACE_INVITE_MANAGER",
				new String[] {inviteUserInfo.getNickname(), String.join(",", requestPlanList)}, locale
			);
		} else {
			message = messageSource.getMessage(
				"WORKSPACE_INVITE_MEMBER",
				new String[] {inviteUserInfo.getNickname(), String.join(",", requestPlanList)}, locale
			);
		}
		// 수락 성공 프로세스
		WorkspaceInviteProcess workspaceInviteAcceptProcess = WorkspaceInviteProcess.builder()
			.applicationEventPublisher(applicationEventPublisher)
			.historyAddEvent(new HistoryAddEvent(message, userInvite.getInvitedUserId(), workspace))
			.mailSendEvent(new MailSendEvent(
				mailContextHandler.getWorkspaceInviteAcceptContext(
					workspace.getName(), masterUserInfo, inviteUserInfo, userInvite.getRole(),
					userInvite.isPlanRemote(), userInvite.isPlanMake(), userInvite.isPlanView()
				),
				Mail.WORKSPACE_INVITE_ACCEPT,
				locale,
				getMasterAndManagerEmail(workspace, masterUserInfo)
			))
			.redirectUrl(redirectProperty.getWorkstationWeb())
			.inviteSessionDeleteEvent(new InviteSessionDeleteEvent(userInvite.getSessionCode()))
			.build();
		return workspaceInviteAcceptProcess.process();
	}

	private RedirectView redirectView(String url) {
		RedirectView redirectView = new RedirectView();
		redirectView.setUrl(url);
		redirectView.setContentType("application/json");
		return redirectView;
	}

	private List<String> executeGrantWorkspaceLicenseToUser(
		String workspaceId, String
		inviteUserId, List<String> requestPlanList
	) {
		List<String> failPlanList = new ArrayList<>();
		requestPlanList.forEach(requestPlan -> {
			try {
				grantWorkspaceLicenseToUser(workspaceId, inviteUserId, requestPlan);
			} catch (WorkspaceException e) {
				failPlanList.add(requestPlan);
			}
		});
		return failPlanList;
	}

	private List<String> getRequestWorkspaceLicense(boolean planRemote, boolean planMake, boolean planView) {
		List<String> result = new ArrayList<>();
		if (planRemote) {
			result.add("REMOTE");
		}
		if (planMake) {
			result.add("MAKE");
		}
		if (planView) {
			result.add("VIEW");
		}
		return result;
	}

	private List<String> getMasterAndManagerEmail(Workspace workspace, UserInfoRestResponse masterUserInfo) {
		List<String> emailReceiverList = new ArrayList<>();
		List<WorkspaceUserPermission> managerUserPermissionList = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(
			workspace, Role.MANAGER);
		if (managerUserPermissionList != null && !managerUserPermissionList.isEmpty()) {
			managerUserPermissionList.forEach(workspaceUserPermission -> {
				UserInfoRestResponse managerUserInfo = getUserInfoRequest(
					workspaceUserPermission.getWorkspaceUser().getUserId());
				emailReceiverList.add(managerUserInfo.getEmail());
			});
		}
		emailReceiverList.add(masterUserInfo.getEmail());
		return emailReceiverList;
	}

	public RedirectView inviteWorkspaceReject(String sessionCode, String lang) {
		Locale locale = Locale.KOREAN;
		if (StringUtils.hasText(lang)) {
			locale = new Locale(lang, "");
		}

		Optional<UserInvite> optionalUserInvite = userInviteRepository.findById(sessionCode);
		if (!optionalUserInvite.isPresent()) {
			log.info(
				"[WORKSPACE INVITE REJECT] Workspace invite session Info Not found. session code >> [{}]", sessionCode);
			return redirectView(redirectProperty.getWorkstationWeb());
		}
		UserInvite userInvite = optionalUserInvite.get();
		log.info("[WORKSPACE INVITE REJECT] Workspace Invite Session Info >> [{}] ", userInvite);

		//비회원 거절은 메일 전송 안함.
		ApiResponse<InviteUserInfoResponse> inviteUserInfoResponseApiResponse = getInviteUserInfoByEmail(
			userInvite.getInvitedUserEmail());
		if (inviteUserInfoResponseApiResponse.getCode() != 200) {
			log.error("[WORKSPACE INVITE REJECT] Invalid Invited User Info.");
			//탈퇴한 유저의 캐싱은 삭제, 이외의 유저는 보류.
			if (inviteUserInfoResponseApiResponse.getCode() == 5002) {
				applicationEventPublisher.publishEvent(new InviteSessionDeleteEvent(userInvite.getSessionCode()));
			}
			return redirectView(redirectProperty.getWorkstationWeb());
		}
		InviteUserInfoResponse inviteUserResponse = inviteUserInfoResponseApiResponse.getData();

		//비회원
		if (!inviteUserResponse.isMemberUser()) {
			log.info("[WORKSPACE INVITE REJECT] Invited User is Member >> [{}]", inviteUserResponse.isMemberUser());
			applicationEventPublisher.publishEvent(new InviteSessionDeleteEvent(userInvite.getSessionCode()));
			return redirectView(redirectProperty.getWorkstationWeb());
		}
		//비회원일경우 초대 session정보에 uuid가 안들어가므로 user서버에서 조회해서 가져온다.
		InviteUserDetailInfoResponse inviteUserInfo = inviteUserResponse.getInviteUserDetailInfo();
		userInvite.setInvitedUserEmail(inviteUserInfo.getEmail());
		userInvite.setInvitedUserId(inviteUserInfo.getUserUUID());
		userInviteRepository.save(userInvite);

		//MAIL 발송
		Workspace workspace = workspaceRepository.findByUuid(userInvite.getWorkspaceId())
			.orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
		UserInfoRestResponse masterUserInfo = getUserInfoRequest(workspace.getUserId());

		List<String> emailReceiverList = getMasterAndManagerEmail(workspace, masterUserInfo);

		WorkspaceInviteProcess workspaceInviteProcess = WorkspaceInviteProcess.builder()
			.applicationEventPublisher(applicationEventPublisher)
			.mailSendEvent(new MailSendEvent(
				mailContextHandler.getWorkspaceInviteRejectContext(workspace.getName(), inviteUserInfo),
				Mail.WORKSPACE_INVITE_REJECT, locale, emailReceiverList
			))
			.redirectUrl(redirectProperty.getWorkstationWeb())
			.inviteSessionDeleteEvent(new InviteSessionDeleteEvent(userInvite.getSessionCode()))
			.build();
		return workspaceInviteProcess.process();
	}
}
