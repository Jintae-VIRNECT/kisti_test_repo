package com.virnect.workspace.application.workspaceuser;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.dao.cache.UserInviteRepository;
import com.virnect.workspace.dao.setting.WorkspaceCustomSettingRepository;
import com.virnect.workspace.dao.workspace.*;
import com.virnect.workspace.domain.redis.UserInvite;
import com.virnect.workspace.domain.setting.SettingName;
import com.virnect.workspace.domain.setting.SettingValue;
import com.virnect.workspace.domain.setting.WorkspaceCustomSetting;
import com.virnect.workspace.domain.workspace.*;
import com.virnect.workspace.dto.request.*;
import com.virnect.workspace.dto.response.WorkspaceMemberInfoListResponse;
import com.virnect.workspace.dto.response.WorkspaceMemberPasswordChangeResponse;
import com.virnect.workspace.dto.response.WorkspaceUserInfoResponse;
import com.virnect.workspace.dto.rest.*;
import com.virnect.workspace.event.history.HistoryAddEvent;
import com.virnect.workspace.event.invite.InviteSessionDeleteEvent;
import com.virnect.workspace.event.mail.MailContextHandler;
import com.virnect.workspace.event.mail.MailSendEvent;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
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

    public OnWorkspaceUserServiceImpl(WorkspaceRepository workspaceRepository, WorkspaceUserRepository workspaceUserRepository, WorkspaceRoleRepository workspaceRoleRepository, WorkspaceUserPermissionRepository workspaceUserPermissionRepository, UserRestService userRestService, MessageSource messageSource, LicenseRestService licenseRestService, RestMapStruct restMapStruct, ApplicationEventPublisher applicationEventPublisher, WorkspaceCustomSettingRepository workspaceCustomSettingRepository, MailContextHandler mailContextHandler, WorkspacePermissionRepository workspacePermissionRepository, UserInviteRepository userInviteRepository, RedirectProperty redirectProperty) {
        super(workspaceRepository, workspaceUserRepository, workspaceRoleRepository, workspaceUserPermissionRepository, userRestService, messageSource, licenseRestService, restMapStruct, applicationEventPublisher, workspaceCustomSettingRepository, mailContextHandler, workspacePermissionRepository);
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
    public ApiResponse<Boolean> inviteWorkspace(
            String workspaceId, WorkspaceInviteRequest workspaceInviteRequest, Locale locale
    ) {
        //1-1. 초대 받은 유저 역할 체크 : 마스터 권한을 부여하여 초대할 수 없다.
        if (workspaceInviteRequest.existMasterUserInvite()) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE);
        }

        //1-2. 초대 요청 유저 역할 체크
        checkWorkspaceInvitePermission(workspaceId, workspaceInviteRequest.getUserId(), workspaceInviteRequest.getUserInfoList());

        //1-3. 워크스페이스에 최대 참여 가능한 유저 수 체크
        WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse = getWorkspaceLicensesByWorkspaceId(workspaceId);
        checkWorkspaceMaxUserAmount(workspaceId, workspaceInviteRequest.getUserInfoList().size(), workspaceLicensePlanInfoResponse);

        //1-4. 제품 라이선스를 부여할 수 있는 지 체크
        checkWorkspaceInviteLicenseProduct(workspaceInviteRequest.getUserInfoList(), workspaceLicensePlanInfoResponse);

        //1-5. 탈퇴한 유저인지 체크
        List<String> invitedUserEmailList = workspaceInviteRequest.getUserInfoList().stream().map(WorkspaceInviteRequest.UserInfo::getEmail).collect(Collectors.toList());
        Map<String, InviteUserInfoResponse> inviteUserInfoResponseMap = checkWorkspaceInvitedUserSecession(invitedUserEmailList);

        //1-6. 이미 초대된 유저인지 체크
        checkWorkspaceInvitedUserAlreadyJoin(workspaceId, inviteUserInfoResponseMap);

        //1-7. 이미 최대 참여 가능한 워크스페이스를 넘긴 유저인지 체크
        checkWorkspaceInvitedUserMaxJoin(inviteUserInfoResponseMap);

        // 마스터 유저 정보
        Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        UserInfoRestResponse materUser = getUserInfoByUserId(workspace.getUserId());

        //2. 초대 정보 저장
        workspaceInviteRequest.getUserInfoList().forEach(userInfo -> {
            InviteUserInfoResponse inviteUserResponse = inviteUserInfoResponseMap.get(userInfo.getEmail());
            String sessionCode = RandomStringTokenUtil.generate(UUIDType.INVITE_CODE, 20);
            Optional<UserInvite> userInviteOptional = userInviteRepository.findByWorkspaceIdAndInvitedUserEmail(workspaceId, userInfo.getEmail());
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
                Context context = mailContextHandler.getWorkspaceInviteContext(sessionCode, locale, workspace.getName(), userInfo, inviteUserResponse.getInviteUserDetailInfo(), materUser);
                applicationEventPublisher.publishEvent(new MailSendEvent(context, Mail.WORKSPACE_INVITE, locale, emailReceiverList));
            } else {
                Context context = mailContextHandler.getWorkspaceInviteNonUserContext(sessionCode, locale, workspace.getName(), userInfo, materUser);
                applicationEventPublisher.publishEvent(new MailSendEvent(context, Mail.WORKSPACE_INVITE_NON_USER, locale, emailReceiverList));
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
    private void checkWorkspaceInviteLicenseProduct(List<WorkspaceInviteRequest.UserInfo> userInfoList, WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse) {
        long requestRemoteAmount = userInfoList.stream().filter(WorkspaceInviteRequest.UserInfo::isPlanRemote).count();
        long requestMakeAmount = userInfoList.stream().filter(WorkspaceInviteRequest.UserInfo::isPlanMake).count();
        long requestViewAmount = userInfoList.stream().filter(WorkspaceInviteRequest.UserInfo::isPlanView).count();

        if (requestRemoteAmount > 0 || requestMakeAmount > 0 || requestViewAmount > 0) {
            Integer unUsedRemoteAmount = workspaceLicensePlanInfoResponse.getLicenseProductInfoList().stream().filter(licenseProductInfoResponse -> licenseProductInfoResponse.getProductName().equals("REMOTE")).map(WorkspaceLicensePlanInfoResponse.LicenseProductInfoResponse::getUnUseLicenseAmount).findFirst()
                    .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE_NON_LICENSE));
            if (requestRemoteAmount > unUsedRemoteAmount) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE_NON_LICENSE);
            }
            Integer unUsedMakeAmount = workspaceLicensePlanInfoResponse.getLicenseProductInfoList().stream().filter(licenseProductInfoResponse -> licenseProductInfoResponse.getProductName().equals("MAKE")).map(WorkspaceLicensePlanInfoResponse.LicenseProductInfoResponse::getUnUseLicenseAmount).findFirst().orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE_NON_LICENSE));
            if (requestMakeAmount > unUsedMakeAmount) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE_NON_LICENSE);
            }
            Integer unUsedViewAmount = workspaceLicensePlanInfoResponse.getLicenseProductInfoList().stream().filter(licenseProductInfoResponse -> licenseProductInfoResponse.getProductName().equals("VIEW")).map(WorkspaceLicensePlanInfoResponse.LicenseProductInfoResponse::getUnUseLicenseAmount).findFirst().orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE_NON_LICENSE));
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
            long userIncludedWorkspaceAmount = workspaceUserRepository.countByUserId(inviteUserInfoResponse.getInviteUserDetailInfo().getUserUUID());
            if (inviteUserInfoResponse.isMemberUser() && userIncludedWorkspaceAmount + 1 > MAX_JOIN_WORKSPACE_AMOUNT) {
                log.error("[WORKSPACE INVITE USER] maximum join workspace amount : [{}], current user join workspace amount(include request) : [{}]", MAX_JOIN_WORKSPACE_AMOUNT, userIncludedWorkspaceAmount + 1);
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
    private void checkWorkspaceInvitedUserAlreadyJoin(String workspaceId, Map<String, InviteUserInfoResponse> inviteUserInfoResponseMap) {
        inviteUserInfoResponseMap.values().forEach(inviteUserInfoResponse -> {
            Optional<WorkspaceUser> invitedWorkspaceUser = workspaceUserRepository.findByUserIdAndWorkspace_Uuid(inviteUserInfoResponse.getInviteUserDetailInfo().getUserUUID(), workspaceId);
            if (inviteUserInfoResponse.isMemberUser() && invitedWorkspaceUser.isPresent()) {
                log.error("[WORKSPACE INVITE USER] Invite User is already Workspace user. Invite user is Member >>> [{}], Invite user is Workspace User >>> [{}]", inviteUserInfoResponse.isMemberUser(), true);
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
            ApiResponse<InviteUserInfoResponse> inviteUserInfoResponseApiResponse = getInviteUserInfoByEmail(invitedUserEmail);
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
    private void checkWorkspaceInvitePermission(String workspaceId, String requestUserId, List<WorkspaceInviteRequest.UserInfo> userInfoList) {
        List<Role> invitedUserRoleList = userInfoList.stream().map(WorkspaceInviteRequest.UserInfo::getRole).collect(Collectors.toList());
        WorkspaceUserPermission requestUserPermission = workspaceUserPermissionRepository.findWorkspaceUser(workspaceId, requestUserId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION));

        //초대 권한이 설정 정보에 따라 변경됨.
        Optional<WorkspaceCustomSetting> workspaceCustomSettingOptional = workspaceCustomSettingRepository.findByWorkspace_UuidAndSetting_Name(workspaceId, SettingName.PUBLIC_USER_MANAGEMENT_ROLE_SETTING);
        if (!workspaceCustomSettingOptional.isPresent() || workspaceCustomSettingOptional.get().getValue() == SettingValue.UNUSED || workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER) {
            // 초대한 사람이 마스터 또는 매니저여야 한다.
            if (requestUserPermission.getWorkspaceRole().getRole() != Role.MASTER && requestUserPermission.getWorkspaceRole().getRole() != Role.MANAGER) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
            // 매니저 유저는 매니저 유저를 초대할 수 없다.
            if (requestUserPermission.getWorkspaceRole().getRole() == Role.MANAGER && invitedUserRoleList.stream().anyMatch(s -> s.equals(Role.MANAGER.name()))) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
        }
        if (workspaceCustomSettingOptional.isPresent()) {
            WorkspaceCustomSetting workspaceCustomSetting = workspaceCustomSettingOptional.get();
            // 마스터 유저만 초대 할 수 있다.
            if (workspaceCustomSetting.getValue() == SettingValue.MASTER && requestUserPermission.getWorkspaceRole().getRole() != Role.MASTER) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
            //멤버유저도 초대할 수 있다. 단 상위 유저는 초대할 수 없다.
            //TODO workspace setting 추가 될때 상위 유저 확인해서 block
            if (workspaceCustomSetting.getValue() == SettingValue.MASTER_OR_MANAGER_OR_MEMBER) {
                if (requestUserPermission.getWorkspaceRole().getRole() != Role.MASTER && requestUserPermission.getWorkspaceRole().getRole() != Role.MANAGER && requestUserPermission.getWorkspaceRole().getRole() != Role.MEMBER) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
            }
        }
    }

    private ApiResponse<InviteUserInfoResponse> getInviteUserInfoByEmail(String email) {
        ApiResponse<InviteUserInfoResponse> inviteUserInfoResponseApiResponse = userRestService.getInviteUserInfoByEmail(email);
        if (inviteUserInfoResponseApiResponse.getCode() != 200) {
            log.error("[GET INVITE USER INFO BY EMAIL] response code : {}, response message : {}", inviteUserInfoResponseApiResponse.getCode(), inviteUserInfoResponseApiResponse.getMessage());
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
            log.error("[WORKSPACE INVITE ACCEPT] Workspace invite session Info Not found. session code >> [{}]", sessionCode);
            return redirectView(redirectProperty.getConsoleWeb() + RedirectPath.WORKSPACE_INVITE_FAIL.getValue());
        }
        UserInvite userInvite = optionalUserInvite.get();
        log.info("[WORKSPACE INVITE ACCEPT] Workspace invite session Info >> [{}]", userInvite.toString());

        //1-2. 초대받은 유저가 유효한지 체크
        ApiResponse<InviteUserInfoResponse> inviteUserInfoResponseApiResponse = getInviteUserInfoByEmail(userInvite.getInvitedUserEmail());
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
            return redirectView(redirectProperty.getTermsWeb() + "?inviteSession=" + sessionCode + "&lang=" + lang + "&email=" + userInvite.getInvitedUserEmail());
        }

        //1-4. 비회원인경우 초대 session정보에 uuid가 null값이므로 회원가입 후에 수락하게 하고 user서버에서 회원정보를 가져온다.(회원가입후에 수락으로 리다이렉트한 경우 이 과정이 필요하다.)
        InviteUserDetailInfoResponse inviteUserInfo = inviteUserResponse.getInviteUserDetailInfo();
        userInvite.setInvitedUserEmail(inviteUserInfo.getEmail());
        userInvite.setInvitedUserId(inviteUserInfo.getUserUUID());
        userInviteRepository.save(userInvite);

        Workspace workspace = workspaceRepository.findByUuid(userInvite.getWorkspaceId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        UserInfoRestResponse masterUserInfo = getUserInfoByUserId(workspace.getUserId());

        //1-5. 유저가 최대 참여 가능한 워크스페이스 수 체크
        long maxJoinWorkspaceAmount = workspaceUserRepository.countByWorkspace_Uuid(workspace.getUuid());
        if (maxJoinWorkspaceAmount + 1 > MAX_JOIN_WORKSPACE_AMOUNT) {
            log.error("[WORKSPACE INVITE ACCEPT] Over Max Workspace join amount. max join workspace Amount >> [{}], current workspace joined amount(include master user) >> [{}]"
                    , MAX_JOIN_WORKSPACE_AMOUNT, maxJoinWorkspaceAmount);

            WorkspaceInviteProcess workspaceInviteProcess = WorkspaceInviteProcess.builder().applicationEventPublisher(applicationEventPublisher)
                    .inviteSessionDeleteEvent(new InviteSessionDeleteEvent(userInvite.getSessionCode()))
                    .mailSendEvent(new MailSendEvent(
                            mailContextHandler.getWorkspaceOverJoinContext(workspace.getName(), masterUserInfo, inviteUserInfo, userInvite.isPlanRemote(), userInvite.isPlanMake(), userInvite.isPlanView()),
                            Mail.WORKSPACE_OVER_JOIN_FAIL,
                            locale,
                            getMasterAndManagerEmail(workspace, masterUserInfo)
                    ))
                    .redirectUrl(redirectProperty.getWorkstationWeb() + RedirectPath.WORKSPACE_OVER_JOIN_FAIL.getValue())
                    .build();
            return workspaceInviteProcess.process();
        }

        //1-6. 워크스페이스에서 최대 참여 가능한 멤버 수 체크
        WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse = getWorkspaceLicensesByWorkspaceId(workspace.getUuid());
        try {
            checkWorkspaceMaxUserAmount(workspace.getUuid(), 1, workspaceLicensePlanInfoResponse);
        } catch (WorkspaceException e) {
            WorkspaceInviteProcess workspaceInviteProcess = WorkspaceInviteProcess.builder().applicationEventPublisher(applicationEventPublisher)
                    .inviteSessionDeleteEvent(new InviteSessionDeleteEvent(userInvite.getSessionCode()))
                    .mailSendEvent(new MailSendEvent(
                            mailContextHandler.getWorkspaceOverMaxUserContext(workspace.getName(), masterUserInfo, inviteUserInfo, userInvite.isPlanRemote(), userInvite.isPlanMake(), userInvite.isPlanView()),
                            Mail.WORKSPACE_OVER_MAX_USER_FAIL,
                            locale,
                            getMasterAndManagerEmail(workspace, masterUserInfo)
                    ))
                    .redirectUrl(redirectProperty.getWorkstationWeb() + RedirectPath.WORKSPACE_OVER_MAX_USER_FAIL.getValue())
                    .build();
            return workspaceInviteProcess.process();
        }
        //2. 초대 수락 수행 - 플랜 할당. 할당에 실패하면 초대 로직 원복.
        List<String> requestPlanList = getRequestWorkspaceLicense(userInvite.isPlanRemote(), userInvite.isPlanMake(), userInvite.isPlanView());
        if (!requestPlanList.isEmpty()) {
            List<String> failPlanList = executeGrantWorkspaceLicenseToUser(workspace.getUuid(), inviteUserInfo.getUserUUID(), requestPlanList);
            if (!failPlanList.isEmpty()) {
                requestPlanList.removeAll(failPlanList);
                requestPlanList.forEach(productName -> revokeWorkspaceLicenseToUser(workspace.getUuid(), inviteUserInfo.getUserUUID(), productName));
                WorkspaceInviteProcess workspaceInviteAcceptProcess = WorkspaceInviteProcess.builder().applicationEventPublisher(applicationEventPublisher)
                        .inviteSessionDeleteEvent(new InviteSessionDeleteEvent(userInvite.getSessionCode()))
                        .mailSendEvent(new MailSendEvent(
                                mailContextHandler.getWorkspaceOverPlanContext(workspace.getName(), masterUserInfo, inviteUserInfo, requestPlanList, failPlanList),
                                Mail.WORKSPACE_OVER_PLAN_FAIL,
                                locale,
                                new ArrayList<>()
                        ))
                        .redirectUrl(redirectProperty.getWorkstationWeb() + RedirectPath.WORKSPACE_OVER_PLAN_FAIL.getValue())
                        .build();
                return workspaceInviteAcceptProcess.process();
            }
        }
        //워크스페이스 소속 넣기 (workspace_user)
        WorkspaceUser workspaceUser = WorkspaceUser.builder().workspace(workspace).userId(userInvite.getInvitedUserId()).build();
        workspaceUserRepository.save(workspaceUser);

        //워크스페이스 권한 부여하기 (workspace_user_permission)
        WorkspaceRole workspaceRole = workspaceRoleRepository.findByRole(userInvite.getRole()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
        WorkspacePermission workspacePermission = workspacePermissionRepository.findById(Permission.ALL.getValue()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
        WorkspaceUserPermission newWorkspaceUserPermission = WorkspaceUserPermission.builder()
                .workspaceUser(workspaceUser)
                .workspaceRole(workspaceRole)
                .workspacePermission(workspacePermission)
                .build();
        workspaceUserPermissionRepository.save(newWorkspaceUserPermission);

        String message;
        if (userInvite.getRole() == Role.MANAGER) {
            message = messageSource.getMessage("WORKSPACE_INVITE_MANAGER", new String[]{inviteUserInfo.getNickname(), String.join(",", requestPlanList)}, locale);
        } else {
            message = messageSource.getMessage("WORKSPACE_INVITE_MEMBER", new String[]{inviteUserInfo.getNickname(), String.join(",", requestPlanList)}, locale);
        }
        // 수락 성공 프로세스
        WorkspaceInviteProcess workspaceInviteAcceptProcess = WorkspaceInviteProcess.builder()
                .applicationEventPublisher(applicationEventPublisher)
                .historyAddEvent(new HistoryAddEvent(message, userInvite.getInvitedUserId(), workspace))
                .mailSendEvent(new MailSendEvent(mailContextHandler.getWorkspaceInviteAcceptContext(workspace.getName(), masterUserInfo, inviteUserInfo, userInvite.getRole().toString(), userInvite.isPlanRemote(), userInvite.isPlanMake(), userInvite.isPlanView()),
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

    private List<String> executeGrantWorkspaceLicenseToUser(String workspaceId, String
            inviteUserId, List<String> requestPlanList) {
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
        List<WorkspaceUserPermission> managerUserPermissionList = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(workspace, Role.MANAGER);
        if (managerUserPermissionList != null && !managerUserPermissionList.isEmpty()) {
            managerUserPermissionList.forEach(workspaceUserPermission -> {
                UserInfoRestResponse managerUserInfo = getUserInfoByUserId(workspaceUserPermission.getWorkspaceUser().getUserId());
                emailReceiverList.add(managerUserInfo.getEmail());
            });
        }
        emailReceiverList.add(masterUserInfo.getEmail());
        return emailReceiverList;
    }

    private void revokeWorkspaceLicenseToUser(String workspaceId, String invitedUserId, String productName) {
        ApiResponse<Boolean> revokeWorkspaceLicenseApiResponse = licenseRestService.revokeWorkspaceLicenseToUser(workspaceId, invitedUserId, productName);
        log.error("[WORKSPACE INVITE ACCEPT] [{}] License Grant Fail. Revoke user License Result >> [{}], Code >> [{}]", productName, revokeWorkspaceLicenseApiResponse.getData(), revokeWorkspaceLicenseApiResponse.getCode());
    }

    private void grantWorkspaceLicenseToUser(String workspaceId, String invitedUserId, String productName) {
        ApiResponse<MyLicenseInfoResponse> myLicenseInfoResponseApiResponse = licenseRestService.grantWorkspaceLicenseToUser(workspaceId, invitedUserId, productName);
        if (myLicenseInfoResponseApiResponse.getCode() != 200) {
            log.error("[WORKSPACE INVITE ACCEPT] [{}] License Grant Fail. request workspace id >> [{}], request user id >> [{}], response code >> [{}]", productName, workspaceId, invitedUserId, myLicenseInfoResponseApiResponse.getCode());
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE_NON_LICENSE);
        } else {
            log.info("[WORKSPACE INVITE ACCEPT] [{}] License Grant SUCCESS! request workspace id >> [{}], request user id >> [{}]", productName, workspaceId, invitedUserId);
        }
    }


    @Override
    public WorkspaceMemberInfoListResponse createWorkspaceMemberAccount(String workspaceId, MemberAccountCreateRequest memberAccountCreateRequest) {
        //계정 유형 체크
        if (memberAccountCreateRequest.existMasterRoleUser() || memberAccountCreateRequest.existSeatRoleUser()) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_ACCOUNT_CREATE_FAIL);
        }
        //1. 요청한 사람의 권한 체크
        Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        WorkspaceUserPermission requestUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, memberAccountCreateRequest.getUserId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));
        Optional<WorkspaceCustomSetting> workspaceCustomSettingOptional = workspaceCustomSettingRepository.findByWorkspace_UuidAndSetting_Name(workspaceId, SettingName.PRIVATE_USER_MANAGEMENT_ROLE_SETTING);
        memberAccountCreateRequest.getMemberAccountCreateRequest().forEach(memberAccountCreateInfo -> {
            WorkspaceRole requestUserRole = requestUserPermission.getWorkspaceRole();
            WorkspaceRole createUserRole = workspaceRoleRepository.findByRole(Role.valueOf(memberAccountCreateInfo.getRole())).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_ROLE_NOT_FOUND));

            if (!workspaceCustomSettingOptional.isPresent() || workspaceCustomSettingOptional.get().getValue() == SettingValue.UNUSED || workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER) {
                //상위 유저에 대해서는 계정을 생성 할 수 없음.
                if (requestUserRole.getId() > createUserRole.getId()) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }

                //마스터 또는 매니저 유저만 계정을 생성할 수 있음.
                if (requestUserRole.getRole() != Role.MASTER && requestUserRole.getRole() != Role.MANAGER) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
            }
            if (workspaceCustomSettingOptional.isPresent()) {
                //마스터 유저만 계정을 생성할 수 있음.
                if (workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER) {
                    if (requestUserRole.getRole() != Role.MASTER) {
                        throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                    }
                }
                //마스터 또는 매니저 또는 멤버 유저만 계정을 생성할 수 있음.
                if (workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER_OR_MEMBER) {
                    if (requestUserRole.getRole() != Role.MASTER && requestUserRole.getRole() != Role.MANAGER && requestUserRole.getRole() != Role.MEMBER) {
                        throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                    }
                }
            }
        });


        List<String> responseLicense = new ArrayList<>();
        List<WorkspaceUserInfoResponse> workspaceUserInfoResponseList = new ArrayList<>();

        for (MemberAccountCreateInfo memberAccountCreateInfo : memberAccountCreateRequest.getMemberAccountCreateRequest()) {
            //1. user-server 멤버 정보 등록 api 요청
            RegisterMemberRequest registerMemberRequest = new RegisterMemberRequest();
            registerMemberRequest.setEmail(memberAccountCreateInfo.getId());
            registerMemberRequest.setPassword(memberAccountCreateInfo.getPassword());
            UserInfoRestResponse userInfoRestResponse = userRestService.registerMemberRequest(registerMemberRequest, "workspace-server").getData();

            if (userInfoRestResponse == null || !StringUtils.hasText(userInfoRestResponse.getUuid())) {
                log.error("[CREATE WORKSPACE MEMBER ACCOUNT] USER SERVER Member Register fail.");
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_ACCOUNT_CREATE_FAIL);
            }

            //3. license-server grant api 요청 -> 실패시 user-server 롤백 api 요청
            if (memberAccountCreateInfo.isPlanRemote()) {
                MyLicenseInfoResponse myLicenseInfoResponse = licenseRestService.grantWorkspaceLicenseToUser(workspaceId, userInfoRestResponse.getUuid(), "REMOTE").getData();
                if (myLicenseInfoResponse == null || !StringUtils.hasText(myLicenseInfoResponse.getProductName())) {
                    log.error("[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant fail. Request User UUID : [{}], Product License : [{}]", userInfoRestResponse.getUuid(), "REMOTE");
                    UserDeleteRestResponse userDeleteRestResponse = userRestService.userDeleteRequest(userInfoRestResponse.getUuid(), "workspace-server").getData();
                    log.error("[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant fail >>>> USER SERVER account delete process. Request User UUID : [{}], Delete Date : [{}]", userDeleteRestResponse.getUserUUID(), userDeleteRestResponse.getDeletedDate());
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_LICENSE_GRANT_FAIL);
                }
                responseLicense.add("REMOTE");
            }
            if (memberAccountCreateInfo.isPlanMake()) {
                MyLicenseInfoResponse myLicenseInfoResponse = licenseRestService.grantWorkspaceLicenseToUser(
                        workspaceId, userInfoRestResponse.getUuid(), "MAKE").getData();
                if (myLicenseInfoResponse == null || !StringUtils.hasText(myLicenseInfoResponse.getProductName())) {
                    log.error("[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant fail. Request User UUID : [{}], Product License : [{}]", userInfoRestResponse.getUuid(), "REMOTE");
                    UserDeleteRestResponse userDeleteRestResponse = userRestService.userDeleteRequest(userInfoRestResponse.getUuid(), "workspace-server").getData();
                    log.error("[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant fail >>>> USER SERVER account delete process. Request User UUID : [{}], Delete Date : [{}]", userDeleteRestResponse.getUserUUID(), userDeleteRestResponse.getDeletedDate());
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_LICENSE_GRANT_FAIL);
                }
                responseLicense.add("MAKE");
            }
            if (memberAccountCreateInfo.isPlanView()) {
                MyLicenseInfoResponse myLicenseInfoResponse = licenseRestService.grantWorkspaceLicenseToUser(workspaceId, userInfoRestResponse.getUuid(), "VIEW").getData();
                if (myLicenseInfoResponse == null || !StringUtils.hasText(myLicenseInfoResponse.getProductName())) {
                    log.error("[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant fail. Request User UUID : [{}], Product License : [{}]", userInfoRestResponse.getUuid(), "REMOTE");
                    UserDeleteRestResponse userDeleteRestResponse = userRestService.userDeleteRequest(userInfoRestResponse.getUuid(), "workspace-server").getData();
                    log.error("[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant fail >>>> USER SERVER account delete process. Request User UUID : [{}], Delete Date : [{}]", userDeleteRestResponse.getUserUUID(), userDeleteRestResponse.getDeletedDate());
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_LICENSE_GRANT_FAIL);
                }
                responseLicense.add("VIEW");
            }

            //4. workspace 권한 및 소속 부여
            WorkspaceUser newWorkspaceUser = WorkspaceUser.builder()
                    .userId(userInfoRestResponse.getUuid())
                    .workspace(workspace)
                    .build();
            WorkspaceRole workspaceRole = workspaceRoleRepository.findByRole(Role.valueOf(memberAccountCreateInfo.getRole())).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_ROLE_NOT_FOUND));
            WorkspacePermission workspacePermission = workspacePermissionRepository.findById(Permission.ALL.getValue()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_PERMISSION_NOT_FOUND));
            WorkspaceUserPermission newWorkspaceUserPermission = WorkspaceUserPermission.builder()
                    .workspaceUser(newWorkspaceUser)
                    .workspacePermission(workspacePermission)
                    .workspaceRole(workspaceRole)
                    .build();

            workspaceUserRepository.save(newWorkspaceUser);
            workspaceUserPermissionRepository.save(newWorkspaceUserPermission);

            log.info(
                    "[CREATE WORKSPACE MEMBER ACCOUNT] Workspace add user success. Request User UUID : [{}], Role : [{}], JoinDate : [{}]",
                    userInfoRestResponse.getUuid(), workspaceRole.getRole(), newWorkspaceUser.getCreatedDate()
            );

            //5. response
            WorkspaceUserInfoResponse memberInfoResponse = restMapStruct.userInfoRestResponseToWorkspaceUserInfoResponse(userInfoRestResponse);
            memberInfoResponse.setRole(newWorkspaceUserPermission.getWorkspaceRole().getRole());
            memberInfoResponse.setRoleId(newWorkspaceUserPermission.getWorkspaceRole().getId());
            memberInfoResponse.setJoinDate(newWorkspaceUser.getCreatedDate());
            memberInfoResponse.setLicenseProducts(responseLicense.toArray(new String[0]));
            workspaceUserInfoResponseList.add(memberInfoResponse);
        }

        return new WorkspaceMemberInfoListResponse(workspaceUserInfoResponseList);
    }

    @Override
    public boolean deleteWorkspaceMemberAccount(String workspaceId, MemberAccountDeleteRequest
            memberAccountDeleteRequest) {
        //전용 계정인지 체크
        UserInfoRestResponse userInfoRestResponse = getUserInfoByUserId(memberAccountDeleteRequest.getDeleteUserId());
        if (!userInfoRestResponse.getUserType().equals("WORKSPACE_ONLY_USER")) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_ACCOUNT_DELETE_FAIL);
        }
        Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));

        //1. 요청한 사람의 권한 체크
        WorkspaceUserPermission deleteUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, memberAccountDeleteRequest.getDeleteUserId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        WorkspaceUserPermission requestUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, memberAccountDeleteRequest.getUserId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        // 본인 정보 수정은 권한체크하지 않는다.
        if (!memberAccountDeleteRequest.getUserId().equals(memberAccountDeleteRequest.getDeleteUserId())) {
            Optional<WorkspaceCustomSetting> workspaceCustomSettingOptional = workspaceCustomSettingRepository.findByWorkspace_UuidAndSetting_Name(workspaceId, SettingName.PRIVATE_USER_MANAGEMENT_ROLE_SETTING);
            if (!workspaceCustomSettingOptional.isPresent() || workspaceCustomSettingOptional.get().getValue() == SettingValue.UNUSED || workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER) {
                // 요청한 사람이 마스터 또는 매니저여야 한다.
                if (requestUserPermission.getWorkspaceRole().getRole() != Role.MASTER && requestUserPermission.getWorkspaceRole().getRole() != Role.MANAGER) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
                // 매니저 유저는 매니저 유저를 수정 할 수 없다.
                if (requestUserPermission.getWorkspaceRole().getRole() == Role.MANAGER && deleteUserPermission.getWorkspaceRole().getRole() == Role.MANAGER) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
            }
            if (workspaceCustomSettingOptional.isPresent()) {
                WorkspaceCustomSetting workspaceCustomSetting = workspaceCustomSettingOptional.get();
                // 마스터 유저만 수정할 수 있다.
                if (workspaceCustomSetting.getValue() == SettingValue.MASTER && requestUserPermission.getWorkspaceRole().getRole() != Role.MASTER) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
                //멤버 유저만 수정할 수 있다. //todo: 동급유저는 변경 가능한지 체크
                if (workspaceCustomSetting.getValue() == SettingValue.MASTER_OR_MANAGER_OR_MEMBER) {
                    if (requestUserPermission.getWorkspaceRole().getRole() != Role.MASTER && requestUserPermission.getWorkspaceRole().getRole() != Role.MANAGER && requestUserPermission.getWorkspaceRole().getRole() != Role.MEMBER) {
                        throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                    }
                }
            }
        }

        //2. license-sever revoke api 요청
        MyLicenseInfoListResponse myLicenseInfoListResponse = licenseRestService.getMyLicenseInfoRequestHandler(
                workspaceId, memberAccountDeleteRequest.getDeleteUserId()).getData();

        if (myLicenseInfoListResponse.getLicenseInfoList() != null && !myLicenseInfoListResponse.getLicenseInfoList()
                .isEmpty()) {
            myLicenseInfoListResponse.getLicenseInfoList().forEach(myLicenseInfoResponse -> {
                Boolean revokeResult = licenseRestService.revokeWorkspaceLicenseToUser(
                        workspaceId,
                        memberAccountDeleteRequest.getDeleteUserId(),
                        myLicenseInfoResponse.getProductName()
                ).getData();
                if (!revokeResult) {
                    log.error(
                            "[DELETE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license revoke fail. Request user UUID : [{}], Product License [{}]",
                            memberAccountDeleteRequest.getUserId(),
                            myLicenseInfoResponse.getProductName()
                    );
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_LICENSE_REVOKE_FAIL);
                }
            });
        }

        //3. user-server에 멤버 삭제 api 요청 -> 실패시 grant api 요청
        UserDeleteRestResponse userDeleteRestResponse = userRestService.userDeleteRequest(memberAccountDeleteRequest.getDeleteUserId(), "workspace-server").getData();
        if (userDeleteRestResponse == null || !StringUtils.hasText(userDeleteRestResponse.getUserUUID())) {
            log.error("[DELETE WORKSPACE MEMBER ACCOUNT] USER SERVER delete user fail.");
            if (myLicenseInfoListResponse.getLicenseInfoList() != null && !myLicenseInfoListResponse.getLicenseInfoList().isEmpty()) {
                myLicenseInfoListResponse.getLicenseInfoList().forEach(myLicenseInfoResponse -> {
                    MyLicenseInfoResponse grantResult = licenseRestService.grantWorkspaceLicenseToUser(workspaceId, memberAccountDeleteRequest.getDeleteUserId(), myLicenseInfoResponse.getProductName()
                    ).getData();
                    log.error(
                            "[DELETE WORKSPACE MEMBER ACCOUNT] USER SERVER delete user fail. >>>> LICENSE SERVER license revoke process. Request user UUID : [{}], Product License [{}]",
                            memberAccountDeleteRequest.getDeleteUserId(), grantResult.getProductName()
                    );
                });
            }
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_ACCOUNT_DELETE_FAIL);
        }
        log.info(
                "[DELETE WORKSPACE MEMBER ACCOUNT] USER SERVER delete user success. Request user UUID : [{}],Delete Date [{}]",
                userDeleteRestResponse.getUserUUID(), userDeleteRestResponse.getDeletedDate()
        );

        //4. workspace-sever 권한 및 소속 해제
        workspaceUserPermissionRepository.deleteAllByWorkspaceUser(deleteUserPermission.getWorkspaceUser());
        workspaceUserRepository.deleteById(deleteUserPermission.getWorkspaceUser().getId());

        log.info(
                "[DELETE WORKSPACE MEMBER ACCOUNT] Workspace delete user success. Request User UUID : [{}], Delete User UUID : [{}], DeleteDate : [{}]",
                memberAccountDeleteRequest.getUserId(), memberAccountDeleteRequest.getDeleteUserId(), LocalDateTime.now()
        );
        return true;
    }

    @Override
    public WorkspaceMemberPasswordChangeResponse memberPasswordChange(WorkspaceMemberPasswordChangeRequest passwordChangeRequest, String workspaceId) {
        //전용 계정인지 체크
        UserInfoRestResponse userInfoRestResponse = getUserInfoByUserId(passwordChangeRequest.getMemberUUID());
        if (!userInfoRestResponse.getUserType().equals("WORKSPACE_ONLY_USER")) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_PASSWORD_CHANGE);
        }

        //요청 권한 체크
        Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        WorkspaceUserPermission requestUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, passwordChangeRequest.getMasterUUID()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        WorkspaceUserPermission updateUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, passwordChangeRequest.getMemberUUID()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        if (!passwordChangeRequest.getMasterUUID().equals(passwordChangeRequest.getMemberUUID())) {
            Optional<WorkspaceCustomSetting> workspaceCustomSettingOptional = workspaceCustomSettingRepository.findByWorkspace_UuidAndSetting_Name(workspaceId, SettingName.PRIVATE_USER_MANAGEMENT_ROLE_SETTING);
            if (!workspaceCustomSettingOptional.isPresent() || workspaceCustomSettingOptional.get().getValue() == SettingValue.UNUSED || workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER) {
                // 요청한 사람이 마스터 또는 매니저여야 한다.
                if (requestUserPermission.getWorkspaceRole().getRole() != Role.MASTER && requestUserPermission.getWorkspaceRole().getRole() != Role.MANAGER) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
                // 매니저 유저는 매니저 유저를 수정 할 수 없다.
                if (requestUserPermission.getWorkspaceRole().getRole() == Role.MANAGER && updateUserPermission.getWorkspaceRole().getRole() == Role.MANAGER) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
            }
            if (workspaceCustomSettingOptional.isPresent()) {
                WorkspaceCustomSetting workspaceCustomSetting = workspaceCustomSettingOptional.get();
                // 마스터 유저만 수정할 수 있다.
                if (workspaceCustomSetting.getValue() == SettingValue.MASTER && requestUserPermission.getWorkspaceRole().getRole() != Role.MASTER) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
                //멤버 유저만 수정할 수 있다. //todo: 동급유저는 변경 가능한지 체크
                if (workspaceCustomSetting.getValue() == SettingValue.MASTER_OR_MANAGER_OR_MEMBER) {
                    if (requestUserPermission.getWorkspaceRole().getRole() != Role.MASTER && requestUserPermission.getWorkspaceRole().getRole() != Role.MANAGER && requestUserPermission.getWorkspaceRole().getRole() != Role.MEMBER) {
                        throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                    }
                }
            }
        }

        MemberUserPasswordChangeRequest changeRequest = new MemberUserPasswordChangeRequest(passwordChangeRequest.getMemberUUID(), passwordChangeRequest.getPassword());

        ApiResponse<MemberUserPasswordChangeResponse> responseMessage = userRestService.memberUserPasswordChangeRequest("workspace-server", changeRequest);

        if (responseMessage.getCode() != 200 || !responseMessage.getData().isChanged()) {
            log.error("[USER SERVER PASSWORD CHANGE REST RESULT] - [code: {}, data:{}, message: {}]",
                    responseMessage.getCode(), responseMessage.getData() == null ? "" : responseMessage.getData(),
                    responseMessage.getMessage()
            );
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_PASSWORD_CHANGE);
        }

        return new WorkspaceMemberPasswordChangeResponse(
                passwordChangeRequest.getMasterUUID(),
                responseMessage.getData().getUuid(),
                responseMessage.getData().getPasswordChangedDate()
        );
    }

    public RedirectView inviteWorkspaceReject(String sessionCode, String lang) {
        Locale locale = Locale.KOREAN;
        if (StringUtils.hasText(lang)) {
            locale = new Locale(lang, "");
        }

        Optional<UserInvite> optionalUserInvite = userInviteRepository.findById(sessionCode);
        if (!optionalUserInvite.isPresent()) {
            log.info("[WORKSPACE INVITE REJECT] Workspace invite session Info Not found. session code >> [{}]", sessionCode);
            return redirectView(redirectProperty.getWorkstationWeb());
        }
        UserInvite userInvite = optionalUserInvite.get();
        log.info("[WORKSPACE INVITE REJECT] Workspace Invite Session Info >> [{}] ", userInvite);

        //비회원 거절은 메일 전송 안함.
        ApiResponse<InviteUserInfoResponse> inviteUserInfoResponseApiResponse = getInviteUserInfoByEmail(userInvite.getInvitedUserEmail());
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
        Workspace workspace = workspaceRepository.findByUuid(userInvite.getWorkspaceId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        UserInfoRestResponse masterUserInfo = getUserInfoByUserId(workspace.getUserId());

        List<String> emailReceiverList = getMasterAndManagerEmail(workspace, masterUserInfo);

        WorkspaceInviteProcess workspaceInviteProcess = WorkspaceInviteProcess.builder()
                .applicationEventPublisher(applicationEventPublisher)
                .mailSendEvent(new MailSendEvent(mailContextHandler.getWorkspaceInviteRejectContext(workspace.getName(), inviteUserInfo), Mail.WORKSPACE_INVITE_REJECT, locale, emailReceiverList))
                .redirectUrl(redirectProperty.getWorkstationWeb())
                .inviteSessionDeleteEvent(new InviteSessionDeleteEvent(userInvite.getSessionCode()))
                .build();
        return workspaceInviteProcess.process();
    }
}
