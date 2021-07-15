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
import com.virnect.workspace.dto.onpremise.MemberAccountCreateRequest;
import com.virnect.workspace.dto.request.MemberAccountDeleteRequest;
import com.virnect.workspace.dto.request.WorkspaceInviteRequest;
import com.virnect.workspace.dto.request.WorkspaceMemberPasswordChangeRequest;
import com.virnect.workspace.dto.response.WorkspaceMemberInfoListResponse;
import com.virnect.workspace.dto.response.WorkspaceMemberPasswordChangeResponse;
import com.virnect.workspace.dto.rest.*;
import com.virnect.workspace.event.cache.UserWorkspacesDeleteEvent;
import com.virnect.workspace.event.history.HistoryAddEvent;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.common.RedirectProperty;
import com.virnect.workspace.global.common.mapper.rest.RestMapStruct;
import com.virnect.workspace.global.constant.*;
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
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
    private final MessageRestService messageRestService;
    private final UserInviteRepository userInviteRepository;
    private final SpringTemplateEngine springTemplateEngine;
    private final MessageSource messageSource;
    private final LicenseRestService licenseRestService;
    private final RedirectProperty redirectProperty;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final WorkspaceCustomSettingRepository workspaceCustomSettingRepository;
    private static final int MAX_JOIN_WORKSPACE_AMOUNT = 49;//최대 참여 가능한 워크스페이스 수
    private static final int MAX_INVITE_USER_AMOUNT = 49;//최대 초대 가능한 워크스페이스 멤버 수
    private static final int MAX_WORKSPACE_USER_AMOUNT = 50;//워크스페이스 최대 멤버 수(마스터 본인 포함)

    public OnWorkspaceUserServiceImpl(WorkspaceRepository workspaceRepository, WorkspaceUserRepository workspaceUserRepository, WorkspaceRoleRepository workspaceRoleRepository, WorkspaceUserPermissionRepository workspaceUserPermissionRepository, UserRestService userRestService, MessageRestService messageRestService, SpringTemplateEngine springTemplateEngine, MessageSource messageSource, LicenseRestService licenseRestService, RedirectProperty redirectProperty, RestMapStruct restMapStruct, ApplicationEventPublisher applicationEventPublisher, WorkspacePermissionRepository workspacePermissionRepository, UserInviteRepository userInviteRepository, WorkspaceCustomSettingRepository workspaceCustomSettingRepository) {
        super(workspaceRepository, workspaceUserRepository, workspaceRoleRepository, workspaceUserPermissionRepository, userRestService, messageRestService, springTemplateEngine, messageSource, licenseRestService, redirectProperty, restMapStruct, applicationEventPublisher, workspaceCustomSettingRepository);
        this.workspaceRepository = workspaceRepository;
        this.workspaceUserRepository = workspaceUserRepository;
        this.workspaceRoleRepository = workspaceRoleRepository;
        this.workspacePermissionRepository = workspacePermissionRepository;
        this.workspaceUserPermissionRepository = workspaceUserPermissionRepository;
        this.userRestService = userRestService;
        this.messageRestService = messageRestService;
        this.userInviteRepository = userInviteRepository;
        this.springTemplateEngine = springTemplateEngine;
        this.messageSource = messageSource;
        this.licenseRestService = licenseRestService;
        this.redirectProperty = redirectProperty;
        this.applicationEventPublisher = applicationEventPublisher;
        this.workspaceCustomSettingRepository = workspaceCustomSettingRepository;
    }


    @Override
    public ApiResponse<Boolean> inviteWorkspace(
            String workspaceId, WorkspaceInviteRequest workspaceInviteRequest, Locale locale
    ) {
        /*
         * 권한체크
         * 초대하는 사람 권한 - 마스터, 매니저만 가능
         * 초대받는 사람 권한 - 매니저, 멤버만 가능
         * 초대하는 사람이 매니저일때 - 멤버만 초대할 수 있음.*/
        Optional<WorkspaceCustomSetting> workspaceCustomSettingOptional = workspaceCustomSettingRepository.findByWorkspace_UuidAndSetting_Name(workspaceId, SettingName.PUBLIC_USER_MANAGEMENT_ROLE_SETTING);
        Optional<WorkspaceUserPermission> requestUserPermission = workspaceUserPermissionRepository.findWorkspaceUser(workspaceId, workspaceInviteRequest.getUserId());
        if (!requestUserPermission.isPresent()) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }
        workspaceInviteRequest.getUserInfoList().forEach(userInfo -> {
            if (workspaceCustomSettingOptional.isPresent()) {
                if (workspaceCustomSettingOptional.get().getValue() == SettingValue.UNUSED || workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER) {
                    if (!checkWorkspaceRole(SettingValue.MASTER_OR_MANAGER, requestUserPermission.get().getWorkspaceRole().getRole(), userInfo.getRole())) {
                        throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                    }
                }
                if (workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER) {
                    if (!checkWorkspaceRole(SettingValue.MASTER, requestUserPermission.get().getWorkspaceRole().getRole(), userInfo.getRole())) {
                        throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                    }
                }
                if (workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER_OR_MEMBER) {
                    if (!checkWorkspaceRole(SettingValue.MASTER_OR_MANAGER_OR_MEMBER, requestUserPermission.get().getWorkspaceRole().getRole(), userInfo.getRole())) {
                        throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                    }
                }
            } else {
                if (!checkWorkspaceRole(SettingValue.MASTER_OR_MANAGER, requestUserPermission.get().getWorkspaceRole().getRole(), userInfo.getRole())) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
            }
        });

        //1-1. 초대하는 유저 권한 체크
/*
      Optional<WorkspaceUserPermission> requestUserPermission = workspaceUserPermissionRepository.findWorkspaceUser(workspaceId, workspaceInviteRequest.getUserId());
        if (!requestUserPermission.isPresent() || requestUserPermission.get().getWorkspaceRole().getRole().equals("MEMBER")) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }
        workspaceInviteRequest.getUserInfoList().forEach(userInfo -> {
            log.debug("[WORKSPACE INVITE USER] Invite request user role >> [{}], response user role >> [{}]", requestUserPermission.get().getWorkspaceRole().getRole(), userInfo.getRole());
            if (userInfo.getRole().equalsIgnoreCase("MASTER")) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
            if (requestUserPermission.get().getWorkspaceRole().getRole().equals("MANAGER") && userInfo.getRole().equalsIgnoreCase("MANAGER")) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
        });
        */

        //1-2. 워크스페이스 최대 초대가능 멤버 수 체크
        if (workspaceInviteRequest.getUserInfoList().size() > MAX_INVITE_USER_AMOUNT) {
            log.error("[WORKSPACE INVITE USER] maximum workspace user amount : [{}], request user amount [{}]", MAX_INVITE_USER_AMOUNT,
                    workspaceInviteRequest.getUserInfoList().size());
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE);
        }
        //1-3. 워크스페이스에 최대 참여 가능한 멤버 수 체크
        long workspaceUserAmount = workspaceUserRepository.countByWorkspace_Uuid(workspaceId);
        WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse = getWorkspaceLicenses(workspaceId);
        if (workspaceLicensePlanInfoResponse != null && workspaceLicensePlanInfoResponse.getLicenseProductInfoList() != null && !workspaceLicensePlanInfoResponse.getLicenseProductInfoList().isEmpty()) {
            //1-3-1. 라이선스를 구매한 워크스페이스는 라이선스에 종속된 값으로 체크
            if (workspaceInviteRequest.getUserInfoList().size() + workspaceUserAmount > workspaceLicensePlanInfoResponse.getMaxUserAmount()) {
                log.error("[WORKSPACE INVITE USER] maximum workspace user amount(by license) : [{}], request user amount [{}], current workspace user amount : [{}]", workspaceLicensePlanInfoResponse.getMaxUserAmount(),
                        workspaceInviteRequest.getUserInfoList().size(), workspaceUserAmount);
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE_MAX_USER);
            }
        } else {
            //1-3-2. 라이선스를 구매하지 않은 워크스페이스는 기본값으로 체크
            if (workspaceInviteRequest.getUserInfoList().size() + workspaceUserAmount > MAX_WORKSPACE_USER_AMOUNT) {
                log.error("[WORKSPACE INVITE USER] maximum workspace user amount : [{}], request user amount [{}], current workspace user amount : [{}]", MAX_WORKSPACE_USER_AMOUNT,
                        workspaceInviteRequest.getUserInfoList().size(), workspaceUserAmount);
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE_MAX_USER);
            }
        }

        //2. 초대 정보 저장
        workspaceInviteRequest.getUserInfoList().forEach(userInfo -> {
            //2-1. 유효한 초대 이메일인지 체크(탈퇴한 유저가 아닌지)
            ApiResponse<InviteUserInfoResponse> inviteUserInfoResponseApiResponse = userRestService.getInviteUserInfoByEmail(userInfo.getEmail());
            if (inviteUserInfoResponseApiResponse.getCode() != 200) {
                log.error("[GET INVITE USER INFO BY EMAIL] response code : {}, response message : {}", inviteUserInfoResponseApiResponse.getCode(), inviteUserInfoResponseApiResponse.getMessage());
                log.error("[WORKSPACE INVITE USER] Invalid Invited User Info.");
                if (inviteUserInfoResponseApiResponse.getCode() == 5002) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE_SECESSION_USER);
                }
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE);
            }
            InviteUserInfoResponse inviteUserResponse = inviteUserInfoResponseApiResponse.getData();
            //2-2. 유저가 이미 워크스페이스에 소속되어 있는지
            Optional<WorkspaceUser> optionalWorkspaceUser = workspaceUserRepository.findByUserIdAndWorkspace_Uuid(inviteUserResponse.getInviteUserDetailInfo().getUserUUID(), workspaceId);
            if (inviteUserResponse.isMemberUser() && optionalWorkspaceUser.isPresent()) {
                //비회원이 아니면서 이미 워크스페이스에 소속되어 있을 때
                log.error("[WORKSPACE INVITE USER] Invite User is already Workspace user. Invite user is Member >>> [{}], Invite user is Workspace User >>> [{}]", inviteUserResponse.isMemberUser(), true);
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_ALREADY_EXIST);
            }

            //2-3. 유저가 이미 최대 워크스페이스에 참여 가능한 수를 넘겼는지 체크
            long userIncludedWorkspaceAmount = workspaceUserRepository.countByUserId(inviteUserResponse.getInviteUserDetailInfo().getUserUUID());
            if (inviteUserResponse.isMemberUser() && userIncludedWorkspaceAmount + 1 > MAX_JOIN_WORKSPACE_AMOUNT) {
                log.error("[WORKSPACE INVITE USER] maximum join workspace amount : [{}], current user join workspace amount(include request) : [{}]", MAX_JOIN_WORKSPACE_AMOUNT, userIncludedWorkspaceAmount + 1);
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE_MAX_JOIN_USER);
            }

            boolean inviteSessionExist = false;
            String sessionCode = RandomStringTokenUtil.generate(UUIDType.INVITE_CODE, 20);
            for (UserInvite userInvite : userInviteRepository.findAll()) {
                if (userInvite != null && userInvite.getWorkspaceId().equals(workspaceId) && userInvite.getInvitedUserEmail().equals(userInfo.getEmail())) {
                    inviteSessionExist = true;
                    userInvite.setRole(userInfo.getRole());
                    userInvite.setPlanRemote(userInfo.isPlanRemote());
                    userInvite.setPlanMake(userInfo.isPlanMake());
                    userInvite.setPlanView(userInfo.isPlanView());
                    userInvite.setUpdatedDate(LocalDateTime.now());
                    userInvite.setExpireTime(Duration.ofDays(7).getSeconds());
                    userInviteRepository.save(userInvite);
                    sessionCode = userInvite.getSessionCode();
                    log.info("[WORKSPACE INVITE USER] Workspace Invite Info Redis Update >> {}", userInvite.toString());
                }
            }
            if (!inviteSessionExist) {
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
                        //               .planRemoteType(licensePlanType)
//                        .planMakeType(licensePlanType)
//                        .planViewType(licensePlanType)
                        .invitedDate(LocalDateTime.now())
                        .updatedDate(null)
                        .expireTime(Duration.ofDays(7).getSeconds())
                        .build();
                userInviteRepository.save(newUserInvite);
                log.info("[WORKSPACE INVITE USER] Workspace Invite Info Redis Set >> {}", newUserInvite.toString());
            }

            //메일 전송
            String rejectUrl = redirectProperty.getWorkspaceServer() + "/workspaces/invite/" + sessionCode + "/reject?lang=" + locale.getLanguage();
            String acceptUrl = redirectProperty.getWorkspaceServer() + "/workspaces/invite/" + sessionCode + "/accept?lang=" + locale.getLanguage();
            Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
            UserInfoRestResponse materUser = getUserInfoByUserId(workspace.getUserId());
            if (inviteUserResponse.isMemberUser()) {
                Context context = new Context();
                context.setVariable("rejectUrl", rejectUrl);
                context.setVariable("acceptUrl", acceptUrl);
                context.setVariable("workspaceMasterNickName", materUser.getNickname());
                context.setVariable("workspaceMasterEmail", materUser.getEmail());
                context.setVariable("workspaceName", workspace.getName());
                context.setVariable("responseUserName", inviteUserResponse.getInviteUserDetailInfo().getName());
                context.setVariable("responseUserEmail", inviteUserResponse.getInviteUserDetailInfo().getEmail());
                context.setVariable("responseUserNickName", inviteUserResponse.getInviteUserDetailInfo().getNickname());
                context.setVariable("role", userInfo.getRole());
                context.setVariable("plan", generatePlanString(userInfo.isPlanRemote(), userInfo.isPlanMake(), userInfo.isPlanView()));
                context.setVariable("workstationHomeUrl", redirectProperty.getWorkstationWeb());
                context.setVariable("supportUrl", redirectProperty.getSupportWeb());
                String subject = messageSource.getMessage(Mail.WORKSPACE_INVITE.getSubject(), null, locale);
                String template = messageSource.getMessage(Mail.WORKSPACE_INVITE.getTemplate(), null, locale);
                String html = springTemplateEngine.process(template, context);
                List<String> emailReceiverList = new ArrayList<>();
                emailReceiverList.add(userInfo.getEmail());
                sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);
            } else {
                Context context = new Context();
                context.setVariable("rejectUrl", rejectUrl);
                context.setVariable("acceptUrl", acceptUrl);
                context.setVariable("masterUserName", materUser.getName());
                context.setVariable("masterUserNickname", materUser.getNickname());
                context.setVariable("masterUserEmail", materUser.getEmail());
                context.setVariable("workspaceName", workspace.getName());
                context.setVariable("inviteUserEmail", userInfo.getEmail());
                context.setVariable("role", userInfo.getRole());
                context.setVariable("plan", generatePlanString(userInfo.isPlanRemote(), userInfo.isPlanMake(), userInfo.isPlanView()));
                context.setVariable("workstationHomeUrl", redirectProperty.getWorkstationWeb());
                context.setVariable("supportUrl", redirectProperty.getSupportWeb());
                String subject = messageSource.getMessage(Mail.WORKSPACE_INVITE_NON_USER.getSubject(), null, locale);
                String template = messageSource.getMessage(Mail.WORKSPACE_INVITE_NON_USER.getTemplate(), null, locale);
                String html = springTemplateEngine.process(template, context);
                List<String> emailReceiverList = new ArrayList<>();
                emailReceiverList.add(userInfo.getEmail());
                sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);
            }
        });
        return new ApiResponse<>(true);
    }

    /*
    하위유저는 상위유저 또는 동급유저에 대한 권한이 없으므로 이에 대해 체크한다.
    단 멤버유저의 경우 동급유저(멤버)에 대한 권한을 허용한다.
     */
    private boolean checkWorkspaceRole(SettingValue settingValue, String requestUserRole, String responseUserRole) {
        log.info("[WORKSPACE ROLE CHECK] setting value : [{}], request user role : [{}] , response user role : [{}]", settingValue, requestUserRole, responseUserRole);
        //요청자가 마스터 -> 대상자는 매니저, 멤버
        if (settingValue == SettingValue.MASTER) {
            if (!requestUserRole.equals("MASTER") || !responseUserRole.matches("MANAGER|MEMBER")) {
                return false;
            }

        }
        //요청자가 마스터 -> 대상자는 매니저, 멤버
        //요청자가 매니저 -> 대상자는 멤버
        if (settingValue == SettingValue.MASTER_OR_MANAGER) {
            if (requestUserRole.equals("MASTER") && !responseUserRole.matches("MANAGER|MEMBER")) {
                return false;
            }
            if (requestUserRole.equals("MANAGER") && !responseUserRole.equals("MEMBER")) {
                return false;
            }
            if (requestUserRole.equals("MEMBER")) {
                return false;
            }
        }
        //요청자가 마스터  -> 대상자는 매니저, 멤버
        //요청자가 매니저 -> 대상자는 멤버
        //요청자가 멤버 -> 대상자는 멤버
        if (settingValue == SettingValue.MASTER_OR_MANAGER_OR_MEMBER) {
            if (requestUserRole.equals("MASTER") && !responseUserRole.matches("MANAGER|MEMBER")) {
                return false;
            }
            if (requestUserRole.equals("MANAGER") && !responseUserRole.equals("MEMBER")) {
                return false;
            }
            if (requestUserRole.equals("MEMBER") && !responseUserRole.equals("MEMBER")) {
                return false;
            }
        }
        return true;
    }

    private WorkspaceLicensePlanInfoResponse getWorkspaceLicenses(String workspaceId) {
        //todo 로깅
        ApiResponse<WorkspaceLicensePlanInfoResponse> apiResponse = licenseRestService.getWorkspaceLicenses(workspaceId);
        if (apiResponse.getCode() != 200) {
            log.error("[GET WORKSPACE LICENSE PLAN INFO BY WORKSPACE UUID] response message : {}", apiResponse.getMessage());
            return null;
        }
        return apiResponse.getData();
    }

    /**
     * 유저 정보 조회(User Service)
     *
     * @param userId - 유저 uuid
     * @return - 유저 정보
     */
    private UserInfoRestResponse getUserInfoByUserId(String userId) {
        //todo : logging
        return userRestService.getUserInfoByUserId(userId).getData();
    }
/*
    private InviteUserInfoResponse getInviteUserInfoByEmail(String email) {
        //todo : logging
        ApiResponse<InviteUserInfoResponse> apiResponse = userRestService.getInviteUserInfoByEmail(email);
        //탈퇴한 유저 등의 경우 200 ok가 아님.
        if (apiResponse.getCode() != 200) {
            log.error("[GET INVITE USER INFO BY EMAIL] response message : {}", apiResponse.getMessage());
            return null;
            //throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVITE);
        }
        return apiResponse.getData();
    }*/

    public RedirectView inviteWorkspaceAccept(String sessionCode, String lang) throws IOException {
        Locale locale = new Locale(lang, "");
        //1-1. 초대 세션 유효성 체크
        Optional<UserInvite> optionalUserInvite = userInviteRepository.findById(sessionCode);
        if (!optionalUserInvite.isPresent()) {
            log.info("[WORKSPACE INVITE ACCEPT] Workspace invite session Info Not found. session code >> [{}]", sessionCode);
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(redirectProperty.getConsoleWeb() + RedirectPath.WORKSPACE_INVITE_FAIL.getValue());
            redirectView.setContentType("application/json");
            return redirectView;
        }
        UserInvite userInvite = optionalUserInvite.get();
        log.info("[WORKSPACE INVITE ACCEPT] Workspace invite session Info >> [{}]", userInvite.toString());

        //1-2. 초대받은 유저가 유효한지 체크
        ApiResponse<InviteUserInfoResponse> inviteUserInfoResponseApiResponse = userRestService.getInviteUserInfoByEmail(userInvite.getInvitedUserEmail());
        if (inviteUserInfoResponseApiResponse.getCode() != 200) {
            log.error("[GET INVITE USER INFO BY EMAIL] response code : {}, response message : {}", inviteUserInfoResponseApiResponse.getCode(), inviteUserInfoResponseApiResponse.getMessage());
            log.info("[WORKSPACE INVITE ACCEPT] Invalid Invited User Info.");
            //탈퇴한 유저의 캐싱은 삭제, 이외의 유저는 보류.
            if (inviteUserInfoResponseApiResponse.getCode() == 5002) {
                userInviteRepository.delete(userInvite);
            }
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(redirectProperty.getConsoleWeb() + RedirectPath.WORKSPACE_INVITE_FAIL.getValue());
            redirectView.setContentType("application/json");
            return redirectView;
        }
        InviteUserInfoResponse inviteUserResponse = inviteUserInfoResponseApiResponse.getData();
        //1-3. 초대받은 유저가 비회원인지 체크
        if (!inviteUserResponse.isMemberUser()) {
            log.info("[WORKSPACE INVITE ACCEPT] Invited User is Member >> [{}]", inviteUserResponse.isMemberUser());
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(redirectProperty.getTermsWeb() + "?inviteSession=" + sessionCode + "&lang=" + lang + "&email=" + userInvite.getInvitedUserEmail());
            redirectView.setContentType("application/json");
            return redirectView;
        }
        //1-4. 비회원인경우 초대 session정보에 uuid가 null값이므로 회원가입 후에 수락하게 하고 user서버에서 회원정보를 가져온다.
        InviteUserDetailInfoResponse inviteUserDetailInfoResponse = inviteUserResponse.getInviteUserDetailInfo();
        userInvite.setInvitedUserEmail(inviteUserDetailInfoResponse.getEmail());
        userInvite.setInvitedUserId(inviteUserDetailInfoResponse.getUserUUID());
        userInviteRepository.save(userInvite);

        applicationEventPublisher.publishEvent(new UserWorkspacesDeleteEvent(inviteUserDetailInfoResponse.getUserUUID()));

        Workspace workspace = workspaceRepository.findByUuid(userInvite.getWorkspaceId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));

        //1-5. 유저가 최대 참여 가능한 워크스페이스 수 체크
        long maxJoinWorkspaceAmount = workspaceUserRepository.countByWorkspace_Uuid(workspace.getUuid());
        if (maxJoinWorkspaceAmount + 1 > MAX_JOIN_WORKSPACE_AMOUNT) {
            log.error("[WORKSPACE INVITE ACCEPT] Over Max Workspace join amount. max join workspace Amount >> [{}], current workspace joined amount(include master user) >> [{}]"
                    , MAX_JOIN_WORKSPACE_AMOUNT, maxJoinWorkspaceAmount);
            return workspaceOverJoinFailHandler(workspace, userInvite, locale);
        }

        //1-6. 워크스페이스에서 최대 참여 가능한 멤버 수 체크
        long workspaceUserAmount = workspaceUserRepository.countByWorkspace_Uuid(workspace.getUuid());
        WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse = getWorkspaceLicenses(workspace.getUuid());
        if (workspaceLicensePlanInfoResponse != null && workspaceLicensePlanInfoResponse.getLicenseProductInfoList() != null && !workspaceLicensePlanInfoResponse.getLicenseProductInfoList().isEmpty()) {
            //1-6-1. 라이선스를 구매한 워크스페이스는 라이선스에 종속된 값으로 체크
            if (workspaceUserAmount + 1 > workspaceLicensePlanInfoResponse.getMaxUserAmount()) {
                log.error("[WORKSPACE INVITE ACCEPT] Over Max Workspace Member amount. max user Amount(by license) >> [{}], current user amount >> [{}]",
                        workspaceLicensePlanInfoResponse.getMaxUserAmount(),
                        workspaceUserAmount);
                return workspaceOverMaxUserFailHandler(workspace, userInvite, locale);

            }
        } else {
            //1-6-2. 라이선스를 구매하지 않은 워크스페이스는 기본값으로 체크
            if (workspaceUserAmount + 1 > MAX_WORKSPACE_USER_AMOUNT) {
                log.error("[WORKSPACE INVITE ACCEPT] Over Max Workspace Member amount. max user Amount(by workspace) >> [{}], current user amount >> [{}]",
                        MAX_WORKSPACE_USER_AMOUNT,
                        workspaceUserAmount);
                return workspaceOverMaxUserFailHandler(workspace, userInvite, locale);
            }
        }

        //2. 초대 수락 수행 - 플랜 할당
        boolean licenseGrantResult = true;
        List<String> successPlan = new ArrayList<>();
        List<String> failPlan = new ArrayList<>();

        List<LicenseProduct> licenseProductList = generatePlanList(userInvite.isPlanRemote(), userInvite.isPlanMake(), userInvite.isPlanView());
        for (LicenseProduct licenseProduct : licenseProductList) {
            MyLicenseInfoResponse grantResult = licenseRestService.grantWorkspaceLicenseToUser(workspace.getUuid(), inviteUserDetailInfoResponse.getUserUUID(), licenseProduct.toString()).getData();
            if (grantResult == null || !StringUtils.hasText(grantResult.getProductName())) {
                failPlan.add(licenseProduct.toString());
                licenseGrantResult = false;
            } else {
                successPlan.add(licenseProduct.toString());
            }
        }
        if (!licenseGrantResult) {
            RedirectView redirectView = workspaceOverPlanFailHandler(workspace, userInvite, successPlan, failPlan, locale);
            successPlan.forEach(s -> {
                Boolean revokeResult = licenseRestService.revokeWorkspaceLicenseToUser(workspace.getUuid(), userInvite.getInvitedUserId(), s).getData();
                log.info("[WORKSPACE INVITE ACCEPT] [{}] License Grant Fail. Revoke user License Result >> [{}]", s, revokeResult);
            });
            return redirectView;
        }
        //워크스페이스 소속 넣기 (workspace_user)
        WorkspaceUser workspaceUser = WorkspaceUser.builder().workspace(workspace).userId(userInvite.getInvitedUserId()).build();
        workspaceUserRepository.save(workspaceUser);

        //워크스페이스 권한 부여하기 (workspace_user_permission)
        WorkspaceRole workspaceRole = workspaceRoleRepository.findByRole(userInvite.getRole().toUpperCase()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
        WorkspacePermission workspacePermission = workspacePermissionRepository.findById(Permission.ALL.getValue()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
        WorkspaceUserPermission newWorkspaceUserPermission = WorkspaceUserPermission.builder()
                .workspaceUser(workspaceUser)
                .workspaceRole(workspaceRole)
                .workspacePermission(workspacePermission)
                .build();
        workspaceUserPermissionRepository.save(newWorkspaceUserPermission);

        //MAIL 발송
        UserInfoRestResponse inviteUserInfo = getUserInfoByUserId(userInvite.getInvitedUserId());
        UserInfoRestResponse masterUserInfo = getUserInfoByUserId(workspace.getUserId());
        Context context = new Context();
        context.setVariable("workspaceName", workspace.getName());
        context.setVariable("workspaceMasterNickName", masterUserInfo.getNickname());
        context.setVariable("workspaceMasterEmail", masterUserInfo.getEmail());
        context.setVariable("acceptUserNickName", inviteUserInfo.getNickname());
        context.setVariable("acceptUserEmail", userInvite.getInvitedUserEmail());
        context.setVariable("role", userInvite.getRole());
        context.setVariable("workstationHomeUrl", redirectProperty.getWorkstationWeb());
        context.setVariable("plan", generatePlanString(userInvite.isPlanRemote(), userInvite.isPlanMake(), userInvite.isPlanView()));
        context.setVariable("supportUrl", redirectProperty.getSupportWeb());

        String subject = messageSource.getMessage(Mail.WORKSPACE_INVITE_ACCEPT.getSubject(), null, locale);
        String template = messageSource.getMessage(Mail.WORKSPACE_INVITE_ACCEPT.getTemplate(), null, locale);
        String html = springTemplateEngine.process(template, context);
        List<String> emailReceiverList = new ArrayList<>();
        emailReceiverList.add(masterUserInfo.getEmail());
        List<WorkspaceUserPermission> managerUserPermissionList = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(workspace, "MANAGER");
        if (managerUserPermissionList != null && !managerUserPermissionList.isEmpty()) {
            managerUserPermissionList.forEach(workspaceUserPermission -> {
                UserInfoRestResponse managerUserInfo = getUserInfoByUserId(workspaceUserPermission.getWorkspaceUser().getUserId());
                emailReceiverList.add(managerUserInfo.getEmail());
            });
        }

        sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);

        //redis 에서 삭제
        userInviteRepository.delete(userInvite);

        //history 저장
        if (workspaceRole.getRole().equalsIgnoreCase("MANAGER")) {
            String message = messageSource.getMessage("WORKSPACE_INVITE_MANAGER", new String[]{inviteUserInfo.getNickname(), generatePlanString(userInvite.isPlanRemote(), userInvite.isPlanMake(), userInvite.isPlanView())}, locale);
            applicationEventPublisher.publishEvent(new HistoryAddEvent(message, userInvite.getInvitedUserId(), workspace));
        } else {
            String message = messageSource.getMessage("WORKSPACE_INVITE_MEMBER", new String[]{inviteUserInfo.getNickname(), generatePlanString(userInvite.isPlanRemote(), userInvite.isPlanMake(), userInvite.isPlanView())}, locale);
            applicationEventPublisher.publishEvent(new HistoryAddEvent(message, userInvite.getInvitedUserId(), workspace));
        }

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectProperty.getWorkstationWeb());
        redirectView.setContentType("application/json");
        return redirectView;
    }

    private List<LicenseProduct> generatePlanList(boolean remote, boolean make, boolean view) {
        List<LicenseProduct> productList = new ArrayList<>();
        if (remote) {
            productList.add(LicenseProduct.REMOTE);
        }
        if (make) {
            productList.add(LicenseProduct.MAKE);
        }
        if (view) {
            productList.add(LicenseProduct.VIEW);
        }
        return productList;
    }

    private String generatePlanString(boolean remote, boolean make, boolean view) {
        return generatePlanList(remote, make, view).stream().map(Enum::toString).collect(Collectors.joining(","));
    }

    @Override
    public WorkspaceMemberInfoListResponse createWorkspaceMemberAccount(String workspaceId, MemberAccountCreateRequest memberAccountCreateRequest) {
        return null;
    }

    @Override
    public boolean deleteWorkspaceMemberAccount(String workspaceId, MemberAccountDeleteRequest memberAccountDeleteRequest) {
        return false;
    }

    @Override
    public WorkspaceMemberPasswordChangeResponse memberPasswordChange(WorkspaceMemberPasswordChangeRequest passwordChangeRequest, String workspaceId) {
        return null;
    }

    public RedirectView workspaceOverJoinFailHandler(Workspace workspace, UserInvite userInvite, Locale locale) {
        UserInfoRestResponse inviteUserInfo = getUserInfoByUserId(userInvite.getInvitedUserId());
        UserInfoRestResponse masterUserInfo = getUserInfoByUserId(workspace.getUserId());
        Context context = new Context();
        context.setVariable("workspaceName", workspace.getName());
        context.setVariable("workspaceMasterNickName", masterUserInfo.getNickname());
        context.setVariable("workspaceMasterEmail", masterUserInfo.getEmail());
        context.setVariable("userNickName", inviteUserInfo.getNickname());
        context.setVariable("userEmail", inviteUserInfo.getEmail());
        context.setVariable("plan", generatePlanString(userInvite.isPlanRemote(), userInvite.isPlanMake(), userInvite.isPlanView()));
        context.setVariable("planRemoteType", userInvite.getPlanRemoteType());
        context.setVariable("planMakeType", userInvite.getPlanMakeType());
        context.setVariable("planViewType", userInvite.getPlanViewType());
        context.setVariable("workstationHomeUrl", redirectProperty.getWorkstationWeb());
        context.setVariable("workstationMembersUrl", redirectProperty.getMembersWeb());
        context.setVariable("supportUrl", redirectProperty.getSupportWeb());

        String subject = messageSource.getMessage(Mail.WORKSPACE_OVER_JOIN_FAIL.getSubject(), null, locale);
        String template = messageSource.getMessage(Mail.WORKSPACE_OVER_JOIN_FAIL.getTemplate(), null, locale);
        String html = springTemplateEngine.process(template, context);

        List<String> emailReceiverList = new ArrayList<>();
        emailReceiverList.add(masterUserInfo.getEmail());
        List<WorkspaceUserPermission> managerUserPermissionList = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(workspace, "MANAGER");
        if (managerUserPermissionList != null && !managerUserPermissionList.isEmpty()) {
            managerUserPermissionList.forEach(workspaceUserPermission -> {
                UserInfoRestResponse managerUserInfo = getUserInfoByUserId(workspaceUserPermission.getWorkspaceUser().getUserId());
                emailReceiverList.add(managerUserInfo.getEmail());
            });
        }

        sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);

        userInviteRepository.delete(userInvite);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectProperty.getWorkstationWeb() + RedirectPath.WORKSPACE_OVER_JOIN_FAIL.getValue());
        redirectView.setContentType("application/json");
        return redirectView;
    }

    public RedirectView workspaceOverMaxUserFailHandler(Workspace workspace, UserInvite userInvite, Locale locale) {
        UserInfoRestResponse inviteUserInfo = getUserInfoByUserId(userInvite.getInvitedUserId());
        UserInfoRestResponse masterUserInfo = getUserInfoByUserId(workspace.getUserId());

        Context context = new Context();
        context.setVariable("workspaceName", workspace.getName());
        context.setVariable("workspaceMasterNickName", masterUserInfo.getNickname());
        context.setVariable("workspaceMasterEmail", masterUserInfo.getEmail());
        context.setVariable("userNickName", inviteUserInfo.getNickname());
        context.setVariable("userEmail", inviteUserInfo.getEmail());
        context.setVariable("plan", generatePlanString(userInvite.isPlanRemote(), userInvite.isPlanMake(), userInvite.isPlanView()));
        context.setVariable("planRemoteType", userInvite.getPlanRemoteType());
        context.setVariable("planMakeType", userInvite.getPlanMakeType());
        context.setVariable("planViewType", userInvite.getPlanViewType());
        context.setVariable("contactUrl", redirectProperty.getContactWeb());
        context.setVariable("workstationHomeUrl", redirectProperty.getWorkstationWeb());
        context.setVariable("supportUrl", redirectProperty.getSupportWeb());
        String subject = messageSource.getMessage(Mail.WORKSPACE_OVER_MAX_USER_FAIL.getSubject(), null, locale);
        String template = messageSource.getMessage(Mail.WORKSPACE_OVER_MAX_USER_FAIL.getTemplate(), null, locale);
        String html = springTemplateEngine.process(template, context);
        List<String> emailReceiverList = new ArrayList<>();
        emailReceiverList.add(masterUserInfo.getEmail());
        List<WorkspaceUserPermission> managerUserPermissionList = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(workspace, "MANAGER");
        if (managerUserPermissionList != null && !managerUserPermissionList.isEmpty()) {
            managerUserPermissionList.forEach(workspaceUserPermission -> {
                UserInfoRestResponse managerUserInfo = getUserInfoByUserId(workspaceUserPermission.getWorkspaceUser().getUserId());
                emailReceiverList.add(managerUserInfo.getEmail());
            });
        }
        sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);

        userInviteRepository.delete(userInvite);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectProperty.getWorkstationWeb() + RedirectPath.WORKSPACE_OVER_MAX_USER_FAIL.getValue());
        redirectView.setContentType("application/json");
        return redirectView;
    }


    public RedirectView workspaceOverPlanFailHandler(Workspace workspace, UserInvite
            userInvite, List<String> successPlan, List<String> failPlan, Locale locale) {
        UserInfoRestResponse inviteUserInfo = getUserInfoByUserId(userInvite.getInvitedUserId());
        UserInfoRestResponse masterUserInfo = getUserInfoByUserId(workspace.getUserId());

        Context context = new Context();
        context.setVariable("workspaceName", workspace.getName());
        context.setVariable("workspaceMasterNickName", masterUserInfo.getNickname());
        context.setVariable("workspaceMasterEmail", masterUserInfo.getEmail());
        context.setVariable("userNickName", inviteUserInfo.getNickname());
        context.setVariable("userEmail", inviteUserInfo.getEmail());
        context.setVariable("successPlan", org.apache.commons.lang.StringUtils.join(successPlan, ","));
        context.setVariable("failPlan", org.apache.commons.lang.StringUtils.join(failPlan, ","));
        context.setVariable("planRemoteType", userInvite.getPlanRemoteType());
        context.setVariable("planMakeType", userInvite.getPlanMakeType());
        context.setVariable("planViewType", userInvite.getPlanViewType());
        context.setVariable("workstationHomeUrl", redirectProperty.getWorkstationWeb());
        context.setVariable("workstationMembersUrl", redirectProperty.getMembersWeb());
        context.setVariable("supportUrl", redirectProperty.getSupportWeb());

        String subject = messageSource.getMessage(Mail.WORKSPACE_OVER_PLAN_FAIL.getSubject(), null, locale);
        String template = messageSource.getMessage(Mail.WORKSPACE_OVER_PLAN_FAIL.getTemplate(), null, locale);
        String html = springTemplateEngine.process(template, context);
        List<String> emailReceiverList = new ArrayList<>();
        emailReceiverList.add(masterUserInfo.getEmail());
        List<WorkspaceUserPermission> managerUserPermissionList = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(workspace, "MANAGER");
        if (managerUserPermissionList != null && !managerUserPermissionList.isEmpty()) {
            managerUserPermissionList.forEach(workspaceUserPermission -> {
                UserInfoRestResponse managerUserInfo = getUserInfoByUserId(workspaceUserPermission.getWorkspaceUser().getUserId());
                emailReceiverList.add(managerUserInfo.getEmail());
            });
        }
        sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);

        userInviteRepository.delete(userInvite);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectProperty.getWorkstationWeb() + RedirectPath.WORKSPACE_OVER_PLAN_FAIL.getValue());
        redirectView.setContentType("application/json");
        return redirectView;
    }

    public RedirectView inviteWorkspaceReject(String sessionCode, String lang) {
        Locale locale = new Locale(lang, "");
        Optional<UserInvite> optionalUserInvite = userInviteRepository.findById(sessionCode);
        if (!optionalUserInvite.isPresent()) {
            log.info("[WORKSPACE INVITE REJECT] Workspace invite session Info Not found. session code >> [{}]", sessionCode);
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(redirectProperty.getWorkstationWeb());
            redirectView.setContentType("application/json");
            return redirectView;
        }
        UserInvite userInvite = optionalUserInvite.get();
        log.info("[WORKSPACE INVITE REJECT] Workspace Invite Session Info >> [{}] ", userInvite);

        //비회원 거절은 메일 전송 안함.
        ApiResponse<InviteUserInfoResponse> inviteUserInfoResponseApiResponse = userRestService.getInviteUserInfoByEmail(userInvite.getInvitedUserEmail());
        if (inviteUserInfoResponseApiResponse.getCode() != 200) {
            log.error("[GET INVITE USER INFO BY EMAIL] response code : {}, response message : {}", inviteUserInfoResponseApiResponse.getCode(), inviteUserInfoResponseApiResponse.getMessage());
            log.error("[WORKSPACE INVITE REJECT] Invalid Invited User Info.");
            //탈퇴한 유저의 캐싱은 삭제, 이외의 유저는 보류.
            if (inviteUserInfoResponseApiResponse.getCode() == 5002) {
                userInviteRepository.delete(userInvite);
            }
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(redirectProperty.getWorkstationWeb());
            redirectView.setContentType("application/json");
            return redirectView;
        }
        InviteUserInfoResponse inviteUserResponse = inviteUserInfoResponseApiResponse.getData();

        //비회원
        if (!inviteUserResponse.isMemberUser()) {
            log.info("[WORKSPACE INVITE REJECT] Invited User is Member >> [{}]", inviteUserResponse.isMemberUser());
            userInviteRepository.delete(userInvite);
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(redirectProperty.getWorkstationWeb());
            redirectView.setContentType("application/json");
            return redirectView;
        }
        //비회원일경우 초대 session정보에 uuid가 안들어가므로 user서버에서 조회해서 가져온다.
        InviteUserDetailInfoResponse inviteUserDetailInfoResponse = inviteUserResponse.getInviteUserDetailInfo();
        userInvite.setInvitedUserEmail(inviteUserDetailInfoResponse.getEmail());
        userInvite.setInvitedUserId(inviteUserDetailInfoResponse.getUserUUID());
        userInviteRepository.save(userInvite);

        userInviteRepository.delete(userInvite);

        //MAIL 발송
        Workspace workspace = workspaceRepository.findByUuid(userInvite.getWorkspaceId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        UserInfoRestResponse inviterUserInfo = getUserInfoByUserId(userInvite.getInvitedUserId());
        UserInfoRestResponse masterUserInfo = getUserInfoByUserId(workspace.getUserId());
        Context context = new Context();
        context.setVariable("rejectUserNickname", inviterUserInfo.getNickname());
        context.setVariable("rejectUserEmail", userInvite.getInvitedUserEmail());
        context.setVariable("workspaceName", workspace.getName());
        context.setVariable("accountUrl", redirectProperty.getAccountWeb());
        context.setVariable("supportUrl", redirectProperty.getSupportWeb());

        String subject = messageSource.getMessage(Mail.WORKSPACE_INVITE_REJECT.getSubject(), null, locale);
        String template = messageSource.getMessage(Mail.WORKSPACE_INVITE_REJECT.getTemplate(), null, locale);
        String html = springTemplateEngine.process(template, context);

        List<String> emailReceiverList = new ArrayList<>();
        emailReceiverList.add(masterUserInfo.getEmail());

        List<WorkspaceUserPermission> managerUserPermissionList = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(workspace, "MANAGER");
        if (managerUserPermissionList != null && !managerUserPermissionList.isEmpty()) {
            managerUserPermissionList.forEach(workspaceUserPermission -> {
                UserInfoRestResponse managerUserInfo = getUserInfoByUserId(workspaceUserPermission.getWorkspaceUser().getUserId());
                emailReceiverList.add(managerUserInfo.getEmail());
            });
        }
        sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectProperty.getWorkstationWeb());
        redirectView.setContentType("application/json");
        return redirectView;
    }

    /**
     * pf-message 서버로 보낼 메일 전송 api body
     *
     * @param html      - 본문
     * @param receivers - 수신정보
     * @param sender    - 발신정보
     * @param subject   - 제목
     */
    private void sendMailRequest(String html, List<String> receivers, String sender, String subject) {
        MailRequest mailRequest = new MailRequest();
        mailRequest.setHtml(html);
        mailRequest.setReceivers(receivers);
        mailRequest.setSender(sender);
        mailRequest.setSubject(subject);
        messageRestService.sendMail(mailRequest);
    }


}
