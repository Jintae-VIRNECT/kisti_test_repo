package com.virnect.workspace.application;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.message.MessageRestService;
import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.dao.*;
import com.virnect.workspace.dao.redis.UserInviteRepository;
import com.virnect.workspace.domain.*;
import com.virnect.workspace.dto.response.WorkspaceUserInfoResponse;
import com.virnect.workspace.dto.WorkspaceInfoDTO;
import com.virnect.workspace.dto.request.WorkspaceCreateRequest;
import com.virnect.workspace.dto.request.WorkspaceUpdateRequest;
import com.virnect.workspace.dto.response.*;
import com.virnect.workspace.dto.rest.MailRequest;
import com.virnect.workspace.dto.rest.PageMetadataRestResponse;
import com.virnect.workspace.dto.rest.UserInfoRestResponse;
import com.virnect.workspace.dto.rest.WorkspaceLicensePlanInfoResponse;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.common.RedirectProperty;
import com.virnect.workspace.global.constant.*;
import com.virnect.workspace.global.error.ErrorCode;
import com.virnect.workspace.global.util.RandomStringTokenUtil;
import com.virnect.workspace.infra.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private static final String serviceID = "workspace-server";
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final WorkspaceRoleRepository workspaceRoleRepository;
    private final WorkspacePermissionRepository workspacePermissionRepository;
    private final WorkspaceUserPermissionRepository workspaceUserPermissionRepository;
    private final UserRestService userRestService;
    private final ModelMapper modelMapper;
    private final MessageRestService messageRestService;
    private final FileService fileUploadService;
    private final UserInviteRepository userInviteRepository;
    private final SpringTemplateEngine springTemplateEngine;
    private final HistoryRepository historyRepository;
    private final MessageSource messageSource;
    private final LicenseRestService licenseRestService;
    private final WorkspaceSettingRepository workspaceSettingRepository;
    private final RedirectProperty redirectProperty;
    private final CacheManager cacheManager;

    /**
     * 워크스페이스 생성
     *
     * @param workspaceCreateRequest - 생성 할 워크스페이스 정보
     * @return - 생성 된 워크스페이스 정보
     */
    @CacheEvict(value = "userWorkspaces", key = "#workspaceCreateRequest.userId")
    @Transactional
    public WorkspaceInfoDTO createWorkspace(WorkspaceCreateRequest workspaceCreateRequest) {
        //필수 값 체크
        if (!StringUtils.hasText(workspaceCreateRequest.getUserId()) || !StringUtils.hasText(
                workspaceCreateRequest.getName()) || !StringUtils.hasText(workspaceCreateRequest.getDescription())) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        //User Service 에서 유저 조회
        UserInfoRestResponse userInfoRestResponse = getUserInfo(workspaceCreateRequest.getUserId());

        //서브유저(유저가 만들어낸 유저)는 워크스페이스를 가질 수 없다.
        if (userInfoRestResponse.getUserType().equals("SUB_USER")) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        //이미 생성한 워크스페이스가 있는지 확인(사용자가 마스터로 소속되는 워크스페이스는 단 1개다.)
        boolean userHasWorkspace = workspaceRepository.existsByUserId(workspaceCreateRequest.getUserId());

        if (userHasWorkspace) {
            throw new WorkspaceException(ErrorCode.ERR_MASTER_WORKSPACE_ALREADY_EXIST);
        }
        //워크스페이스 생성
        String uuid = RandomStringTokenUtil.generate(UUIDType.UUID_WITH_SEQUENCE, 0);
        String pinNumber = RandomStringTokenUtil.generate(UUIDType.PIN_NUMBER, 0);

        String profile;
        if (workspaceCreateRequest.getProfile() != null) {
            try {
                profile = fileUploadService.upload(workspaceCreateRequest.getProfile());
            } catch (IOException e) {
                throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
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
        WorkspaceRole workspaceRole = workspaceRoleRepository.findById(Role.MASTER.getValue()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_ROLE_NOT_FOUND));
        WorkspacePermission workspacePermission = workspacePermissionRepository.findById(Permission.ALL.getValue()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_PERMISSION_NOT_FOUND));
        WorkspaceUserPermission newWorkspaceUserPermission = WorkspaceUserPermission.builder()
                .workspaceRole(workspaceRole)
                .workspacePermission(workspacePermission)
                .workspaceUser(newWorkspaceUser)
                .build();
        workspaceUserPermissionRepository.save(newWorkspaceUserPermission);

        WorkspaceInfoDTO workspaceInfoDTO = modelMapper.map(newWorkspace, WorkspaceInfoDTO.class);
        workspaceInfoDTO.setMasterUserId(newWorkspace.getUserId());
        return workspaceInfoDTO;
    }

    /**
     * 사용자 소속 워크스페이스 조회
     *
     * @param userId - 사용자 uuid
     * @return - 소속된 워크스페이스 정보
     */
    @Cacheable(value = "userWorkspaces", key = "#userId", unless = "#result.workspaceList.size()==0")
    public WorkspaceInfoListResponse getUserWorkspaces(
            String userId, com.virnect.workspace.global.common.PageRequest pageRequest
    ) {
        Page<WorkspaceUserPermission> workspaceUserPermissionPage = workspaceUserPermissionRepository.findByWorkspaceUser_UserId(userId, pageRequest.of());

        List<WorkspaceInfoListResponse.WorkspaceInfo> workspaceList = new ArrayList<>();

        for (WorkspaceUserPermission workspaceUserPermission : workspaceUserPermissionPage) {
            WorkspaceUser workspaceUser = workspaceUserPermission.getWorkspaceUser();
            Workspace workspace = workspaceUser.getWorkspace();

            WorkspaceInfoListResponse.WorkspaceInfo workspaceInfo = modelMapper.map(workspace, WorkspaceInfoListResponse.WorkspaceInfo.class);
            workspaceInfo.setJoinDate(workspaceUser.getCreatedDate());

            UserInfoRestResponse userInfoRestResponse = userRestService.getUserInfoByUserId(workspace.getUserId()).getData();
            workspaceInfo.setMasterName(userInfoRestResponse.getName());
            workspaceInfo.setMasterProfile(userInfoRestResponse.getProfile());
            workspaceInfo.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
            workspaceInfo.setMasterNickName(userInfoRestResponse.getNickname());
            workspaceInfo.setRoleId(workspaceUserPermission.getWorkspaceRole().getId());
            workspaceList.add(workspaceInfo);
        }

        PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
        pageMetadataResponse.setTotalElements(workspaceUserPermissionPage.getTotalElements());
        pageMetadataResponse.setTotalPage(workspaceUserPermissionPage.getTotalPages());
        pageMetadataResponse.setCurrentPage(pageRequest.of().getPageNumber());
        pageMetadataResponse.setCurrentSize(pageRequest.of().getPageSize());

        return new WorkspaceInfoListResponse(workspaceList, pageMetadataResponse);
    }

    /**
     * 워크스페이스 정보 조회
     *
     * @param workspaceId - 워크스페이스 uuid
     * @return - 워크스페이스 정보
     */
    public WorkspaceInfoResponse getWorkspaceDetailInfo(String workspaceId) {
        //workspace 정보 set
        Workspace workspace = workspaceRepository.findByUuid(workspaceId)
                .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        WorkspaceInfoDTO workspaceInfo = modelMapper.map(workspace, WorkspaceInfoDTO.class);
        workspaceInfo.setMasterUserId(workspace.getUserId());

        //user 정보 set
        List<WorkspaceUserPermission> workspaceUserPermissionList = workspaceUserPermissionRepository.findByWorkspaceUser_Workspace(workspace);
        List<WorkspaceUserInfoResponse> userInfoList = workspaceUserPermissionList.stream().map(workspaceUserPermission -> {
            UserInfoRestResponse userInfoRestResponse = userRestService.getUserInfoByUserId(workspaceUserPermission.getWorkspaceUser().getUserId()).getData();
            WorkspaceUserInfoResponse workspaceUserInfoResponse = modelMapper.map(userInfoRestResponse, WorkspaceUserInfoResponse.class);
            workspaceUserInfoResponse.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
            return workspaceUserInfoResponse;
        }).collect(Collectors.toList());


        //role 정보 set
        long masterUserCount = workspaceUserPermissionList.stream().filter(workspaceUserPermission -> workspaceUserPermission.getWorkspaceRole().getRole().equals(Role.MASTER.name())).count();
        long managerUserCount = workspaceUserPermissionList.stream().filter(workspaceUserPermission -> workspaceUserPermission.getWorkspaceRole().getRole().equals(Role.MANAGER.name())).count();
        long memberUserCount = workspaceUserPermissionList.stream().filter(workspaceUserPermission -> workspaceUserPermission.getWorkspaceRole().getRole().equals(Role.MEMBER.name())).count();

        //plan 정보 set
        int remotePlanCount = 0, makePlanCount = 0, viewPlanCount = 0;
        WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse = licenseRestService.getWorkspaceLicenses(workspaceId).getData();
        if (workspaceLicensePlanInfoResponse.getLicenseProductInfoList() != null && !workspaceLicensePlanInfoResponse.getLicenseProductInfoList().isEmpty()) {
            for (WorkspaceLicensePlanInfoResponse.LicenseProductInfoResponse licenseProductInfoResponse : workspaceLicensePlanInfoResponse.getLicenseProductInfoList()) {
                if (licenseProductInfoResponse.getProductName().equals(LicenseProduct.REMOTE.toString())) {
                    remotePlanCount = licenseProductInfoResponse.getUseLicenseAmount();
                }
                if (licenseProductInfoResponse.getProductName().equals(LicenseProduct.MAKE.toString())) {
                    makePlanCount = licenseProductInfoResponse.getUseLicenseAmount();
                }
                if (licenseProductInfoResponse.getProductName().equals(LicenseProduct.VIEW.toString())) {
                    viewPlanCount = licenseProductInfoResponse.getUseLicenseAmount();
                }
            }
        }
        return new WorkspaceInfoResponse(workspaceInfo, userInfoList, masterUserCount, managerUserCount, memberUserCount, remotePlanCount, makePlanCount, viewPlanCount);
    }

    /**
     * 워크스페이스 정보 조회
     *
     * @param workspaceId - 워크스페이스 식별자
     * @return - 워크스페이스 정보
     */
    public WorkspaceInfoDTO getWorkspaceInfo(String workspaceId) {
        Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        WorkspaceInfoDTO workspaceInfoDTO = modelMapper.map(workspace, WorkspaceInfoDTO.class);
        workspaceInfoDTO.setMasterUserId(workspace.getUserId());
        return workspaceInfoDTO;
    }

    /**
     * 유저 정보 조회(User Service)
     *
     * @param userId - 유저 uuid
     * @return - 유저 정보
     */
    private UserInfoRestResponse getUserInfo(String userId) {
        ApiResponse<UserInfoRestResponse> userInfoResponse = userRestService.getUserInfoByUserId(userId);
        return userInfoResponse.getData();
    }

    /**
     * 워크스페이스 유저 초대
     *
     * @param workspaceId            - 초대 할 워크스페이스 uuid
     * @param workspaceInviteRequest - 초대 유저 정보
     * @return - 초대 결과
     *//*
    public ApiResponse<Boolean> inviteWorkspace(
            String workspaceId, WorkspaceInviteRequest workspaceInviteRequest, Locale locale
    ) {
        // 워크스페이스 플랜 조회하여 최대 초대 가능 명 수를 초과했는지 체크
        WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse = licenseRestService.getWorkspaceLicenses(
                workspaceId).getData();
        if (workspaceLicensePlanInfoResponse == null || workspaceLicensePlanInfoResponse.getLicenseProductInfoList() == null
                || workspaceLicensePlanInfoResponse.getLicenseProductInfoList().isEmpty()) {
            throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_WORKSPACE_LICENSE_PLAN);
        }
        int workspaceUserAmount = workspaceUserRepository.findByWorkspace_Uuid(workspaceId).size();
        if (workspaceLicensePlanInfoResponse.getMaxUserAmount()
                < workspaceUserAmount + workspaceInviteRequest.getUserInfoList().size()) {
            throw new WorkspaceException(ErrorCode.ERR_NOMORE_JOIN_WORKSPACE);
        }

        //초대요청 라이선스
        int requestRemote = 0, requestMake = 0, requestView = 0;
        for (WorkspaceInviteRequest.UserInfo userInfo : workspaceInviteRequest.getUserInfoList()) {
            //초대받는 사람에게 부여되는 라이선스는 최소 1개 이상이도록 체크
            userLicenseValidCheck(userInfo.isPlanRemote(), userInfo.isPlanMake(), userInfo.isPlanView());
            if (userInfo.isPlanRemote()) {
                requestRemote++;
            }
            if (userInfo.isPlanMake()) {
                requestMake++;
            }
            if (userInfo.isPlanView()) {
                requestView++;
            }
        }

        //초대받는 사람에게 할당할 라이선스가 있는 지 체크.(useful license check)
        for (WorkspaceLicensePlanInfoResponse.LicenseProductInfoResponse licenseProductInfo : workspaceLicensePlanInfoResponse.getLicenseProductInfoList()) {
            if (licenseProductInfo.getProductName().equals(LicenseProduct.REMOTE.toString())) {
                log.debug(
                        "[WORKSPACE INVITE USER] Workspace Useful License Check. Workspace Unuse Remote License count >> {}, Request REMOTE License count >> {}",
                        licenseProductInfo.getUnUseLicenseAmount(),
                        requestRemote
                );
                if (!licenseProductInfo.getProductStatus().equals(LicenseProductStatus.ACTIVE)) {
                    log.error(
                            "[WORKSPACE INVITE USER] REMOTE License Product Status is not active. Product Status >>[{}]",
                            licenseProductInfo.getProductStatus()
                    );
                    throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_USEFUL_WORKSPACE_LICENSE);
                }
                if (licenseProductInfo.getUnUseLicenseAmount() < requestRemote) {
                    throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_USEFUL_WORKSPACE_LICENSE);
                }
            }
            if (licenseProductInfo.getProductName().equals(LicenseProduct.MAKE.toString())) {
                log.debug(
                        "[WORKSPACE INVITE USER] Workspace Useful License Check. Workspace Unuse Make License count >> {}, Request MAKE License count >> {}",
                        licenseProductInfo.getUnUseLicenseAmount(),
                        requestMake
                );
                if (!licenseProductInfo.getProductStatus().equals(LicenseProductStatus.ACTIVE)) {
                    log.error(
                            "[WORKSPACE INVITE USER] MAKE License Product Status is not active. Product Status >>[{}]",
                            licenseProductInfo.getProductStatus()
                    );
                    throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_USEFUL_WORKSPACE_LICENSE);
                }
                if (licenseProductInfo.getUnUseLicenseAmount() < requestMake) {
                    throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_USEFUL_WORKSPACE_LICENSE);
                }
            }
            if (licenseProductInfo.getProductName().equals(LicenseProduct.VIEW.toString())) {
                log.debug(
                        "[WORKSPACE INVITE USER] Workspace Useful License Check. Workspace Unuse View License count >> {}, Request VIEW License count >> {}",
                        licenseProductInfo.getUnUseLicenseAmount(),
                        requestView
                );
                if (!licenseProductInfo.getProductStatus().equals(LicenseProductStatus.ACTIVE)) {
                    log.error(
                            "[WORKSPACE INVITE USER] VIEW License Product Status is not active. Product Status >>[{}]",
                            licenseProductInfo.getProductStatus()
                    );
                    throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_USEFUL_WORKSPACE_LICENSE);
                }
                if (licenseProductInfo.getUnUseLicenseAmount() < requestView) {
                    throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_USEFUL_WORKSPACE_LICENSE);
                }
            }
        }

        //라이선스 플랜 타입 구하기 -- basic, pro..(한 워크스페이스에서 다른 타입의 라이선스 플랜을 동시에 가지고 있을 수 없으므로, 아무 플랜이나 잡고 타입을 구함.)
        String licensePlanType = workspaceLicensePlanInfoResponse.getLicenseProductInfoList()
                .stream()
                .map(WorkspaceLicensePlanInfoResponse.LicenseProductInfoResponse::getLicenseType)
                .collect(Collectors.toList())
                .get(0);

        */

    /**
     * 권한체크
     * 초대하는 사람 권한 - 마스터, 매니저만 가능
     * 초대받는 사람 권한 - 매니저, 멤버만 가능
     * 초대하는 사람이 매니저일때 - 멤버만 초대할 수 있음.
     *//*
        WorkspaceUserPermission requestUserPermission = workspaceUserPermissionRepository.findWorkspaceUser(workspaceId, workspaceInviteRequest.getUserId()).orElse(null);
        if (requestUserPermission == null || requestUserPermission.getWorkspaceRole().getRole().equals("MEMBER")) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }
        Workspace workspace = requestUserPermission.getWorkspaceUser().getWorkspace();
        workspaceInviteRequest.getUserInfoList().forEach(userInfo -> {
            log.debug("[WORKSPACE INVITE USER] Invite request user role >> [{}], response user role >> [{}]",
                    requestUserPermission.getWorkspaceRole().getRole(), userInfo.getRole());
            if (userInfo.getRole().equalsIgnoreCase("MASTER")) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
            if (requestUserPermission.getWorkspaceRole().getRole().equals("MANAGER") && userInfo.getRole().equalsIgnoreCase("MANAGER")) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
        });

        // 초대할 유저의 계정 유효성 체크(user 서비스)
        String[] inviteEmails = workspaceInviteRequest.getUserInfoList().stream().map(WorkspaceInviteRequest.UserInfo::getEmail).toArray(String[]::new);
        InviteUserInfoRestResponse responseUserList = userRestService.getUserInfoByEmailList(inviteEmails).getData();
        if (responseUserList == null || inviteEmails.length != responseUserList.getInviteUserInfoList().size()) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_USER_EXIST);
        }
        //TODO : 서브유저로 등록되어 있는 사용자가 포함되어 있는 경우.

        //마스터 유저 정보
        UserInfoRestResponse materUser = userRestService.getUserInfoByUserId(workspace.getUserId()).getData();

        Long duration = Duration.ofDays(7).getSeconds();
        responseUserList.getInviteUserInfoList().forEach(inviteUserResponse -> workspaceInviteRequest.getUserInfoList().forEach(userInfo -> {
            //이미 이 워크스페이스에 소속되어 있는 경우
            if (workspaceUserRepository.findByUserIdAndWorkspace(inviteUserResponse.getUserUUID(), workspace) != null) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_ALREADY_EXIST);
            }
            if (inviteUserResponse.getEmail().equals(userInfo.getEmail())) {
                //redis 긁어서 이미 초대한 정보 있는지 확인하고, 있으면 시간과 초대 정보 업데이트
                UserInvite userInvite = userInviteRepository.findById(
                        inviteUserResponse.getUserUUID() + "-" + workspaceId).orElse(null);
                if (userInvite != null) {
                    userInvite.setRole(userInfo.getRole());
                    userInvite.setPlanRemote(userInfo.isPlanRemote());
                    userInvite.setPlanMake(userInfo.isPlanMake());
                    userInvite.setPlanView(userInfo.isPlanView());
                    userInvite.setUpdatedDate(LocalDateTime.now());
                    userInvite.setExpireTime(duration);
                    userInviteRepository.save(userInvite);
                    log.debug(
                            "[WORKSPACE INVITE USER] Workspace Invite Info Redis Update >> {}", userInvite.toString());
                } else {
                    UserInvite newUserInvite = UserInvite.builder()
                            .inviteId(inviteUserResponse.getUserUUID() + "-" + workspaceId)
                            .responseUserId(inviteUserResponse.getUserUUID())
                            .responseUserEmail(inviteUserResponse.getEmail())
                            .responseUserName(inviteUserResponse.getName())
                            .responseUserNickName(inviteUserResponse.getNickname())
                            .requestUserId(materUser.getUuid())
                            .requestUserEmail(materUser.getEmail())
                            .requestUserName(materUser.getName())
                            .requestUserNickName(materUser.getNickname())
                            .workspaceId(workspace.getUuid())
                            .workspaceName(workspace.getName())
                            .role(userInfo.getRole())
                            .planRemote(userInfo.isPlanRemote())
                            .planMake(userInfo.isPlanMake())
                            .planView(userInfo.isPlanView())
                            .planRemoteType(licensePlanType)
                            .planMakeType(licensePlanType)
                            .planViewType(licensePlanType)
                            .invitedDate(LocalDateTime.now())
                            .updatedDate(null)
                            .expireTime(duration)
                            .build();
                    userInviteRepository.save(newUserInvite);
                    log.debug(
                            "[WORKSPACE INVITE USER] Workspace Invite Info Redis Set >> {}", newUserInvite.toString());
                }
                //메일은 이미 초대한 것 여부와 관계없이 발송한다.
                String rejectUrl = serverUrl + "/workspaces/" + workspaceId + "/invite/accept?userId="
                        + inviteUserResponse.getUserUUID() + "&accept=false&lang=" + locale.getLanguage();
                String acceptUrl = serverUrl + "/workspaces/" + workspaceId + "/invite/accept?userId="
                        + inviteUserResponse.getUserUUID() + "&accept=true&lang=" + locale.getLanguage();
                Context context = new Context();
                context.setVariable("workspaceMasterNickName", materUser.getNickname());
                context.setVariable("workspaceMasterEmail", materUser.getEmail());
                context.setVariable("workspaceName", workspace.getName());
                context.setVariable("workstationHomeUrl", redirectUrl);
                context.setVariable("rejectUrl", rejectUrl);
                context.setVariable("acceptUrl", acceptUrl);
                context.setVariable("responseUserName", inviteUserResponse.getName());
                context.setVariable("responseUserEmail", inviteUserResponse.getEmail());
                context.setVariable("responseUserNickName", inviteUserResponse.getNickname());
                context.setVariable("role", userInfo.getRole());
                context.setVariable("plan", generatePlanString(userInfo.isPlanRemote(), userInfo.isPlanMake(), userInfo.isPlanView()));
                context.setVariable("supportUrl", supportUrl);
                String subject = messageSource.getMessage(Mail.WORKSPACE_INVITE.getSubject(), null, locale);
                String template = messageSource.getMessage(Mail.WORKSPACE_INVITE.getTemplate(), null, locale);
                String html = springTemplateEngine.process(template, context);

                List<String> emailReceiverList = new ArrayList<>();
                emailReceiverList.add(inviteUserResponse.getEmail());

                sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);
            }
        }));

        return new ApiResponse<>(true);
    }*/


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

    /*public RedirectView inviteWorkspaceResult(String email, Boolean accept, String lang) {
        Locale locale = new Locale(lang, "");
        UserInvite userInvite = userInviteRepository.findById(email).orElse(null);
        if (userInvite == null) {
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(redirectUrl + "/?message=workspace.invite.invalid");
            redirectView.setContentType("application/json");
            return redirectView;
        }

        if (!userInvite.isUser()) {
            //회원가입하고나서 다시 요청하도록한다.
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("https://192.168.6.3:8883/terms?invite=true&lang=" + lang);
            redirectView.setContentType("application/json");
            return redirectView;
        } else {
            InviteUserInfoResponse inviteUserInfo = userRestService.getUserInfoByEmail(userInvite.getInvitedUserEmail()).getData();
            userInvite.setInvitedUserEmail(email);
            userInvite.setUser(true);
            userInvite.setInvitedUserId(inviteUserInfo.getInviteUserDetailInfo().getUserUUID());
            userInviteRepository.save(userInvite);
        }

        if (accept) {
            return inviteWorkspaceAccept(userInvite, locale);
        } else {
            return inviteWorkspaceReject(userInvite, locale);
        }
    }*/











    /**
     * 워크스페이스 정보 변경
     *
     * @param workspaceUpdateRequest
     * @param locale
     * @return
     */
    public WorkspaceInfoDTO setWorkspace(WorkspaceUpdateRequest workspaceUpdateRequest, Locale locale) {
        if (!StringUtils.hasText(workspaceUpdateRequest.getUserId()) || !StringUtils.hasText(workspaceUpdateRequest.getName())
                || !StringUtils.hasText(workspaceUpdateRequest.getDescription()) || !StringUtils.hasText(
                workspaceUpdateRequest.getWorkspaceId())) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        //마스터 유저 체크
        Workspace workspace = workspaceRepository.findByUuid(workspaceUpdateRequest.getWorkspaceId())
                .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        String oldWorkspaceName = workspace.getName();
        if (!workspace.getUserId().equals(workspaceUpdateRequest.getUserId())) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        if (!oldWorkspaceName.equals(workspaceUpdateRequest.getName())) {
            List<String> receiverEmailList = new ArrayList<>();
            Context context = new Context();
            context.setVariable("beforeWorkspaceName", oldWorkspaceName);
            context.setVariable("afterWorkspaceName", workspaceUpdateRequest.getName());
            context.setVariable("supportUrl", redirectProperty.getSupportWeb());

            List<WorkspaceUser> workspaceUserList = workspaceUserRepository.findByWorkspace_Uuid(
                    workspace.getUuid());
            workspaceUserList.forEach(workspaceUser -> {
                UserInfoRestResponse userInfoRestResponse = getUserInfo(workspaceUser.getUserId());
                receiverEmailList.add(userInfoRestResponse.getEmail());
                if (userInfoRestResponse.getUuid().equals(workspace.getUserId())) {
                    context.setVariable("workspaceMasterNickName", userInfoRestResponse.getNickname());
                }
            });

            String subject = messageSource.getMessage(Mail.WORKSPACE_INFO_UPDATE.getSubject(), null, locale);
            String template = messageSource.getMessage(Mail.WORKSPACE_INFO_UPDATE.getTemplate(), null, locale);
            String html = springTemplateEngine.process(template, context);
            sendMailRequest(html, receiverEmailList, MailSender.MASTER.getValue(), subject);
        }
        workspace.setName(workspaceUpdateRequest.getName());
        workspace.setDescription(workspaceUpdateRequest.getDescription());

        if (workspaceUpdateRequest.getProfile() != null) {
            String oldProfile = workspace.getProfile();
            //기존 프로필 이미지 삭제
            if (StringUtils.hasText(oldProfile)) {
                fileUploadService.delete(oldProfile);
            }
            //새 프로필 이미지 등록
            try {
                workspace.setProfile(fileUploadService.upload(workspaceUpdateRequest.getProfile()));
            } catch (Exception e) {
                throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
            }

        }

        workspaceRepository.save(workspace);

        WorkspaceInfoDTO workspaceInfoDTO = modelMapper.map(workspace, WorkspaceInfoDTO.class);
        workspaceInfoDTO.setMasterUserId(workspace.getUserId());

        return workspaceInfoDTO;
    }

    public ApiResponse<WorkspaceHistoryListResponse> getWorkspaceHistory(
            String workspaceId, String userId, Pageable pageable
    ) {

        Page<History> historyPage = historyRepository.findAllByUserIdAndWorkspace_Uuid(
                userId, workspaceId, pageable);
        List<WorkspaceHistoryListResponse.WorkspaceHistory> workspaceHistoryList = historyPage.stream().map(history -> modelMapper.map(history, WorkspaceHistoryListResponse.WorkspaceHistory.class)).collect(Collectors.toList());

        PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
        pageMetadataResponse.setTotalElements(historyPage.getTotalElements());
        pageMetadataResponse.setTotalPage(historyPage.getTotalPages());
        pageMetadataResponse.setCurrentPage(pageable.getPageNumber());
        pageMetadataResponse.setCurrentSize(pageable.getPageSize());

        return new ApiResponse<>(new WorkspaceHistoryListResponse(workspaceHistoryList, pageMetadataResponse));
    }



    public WorkspaceLicenseInfoResponse getWorkspaceLicenseInfo(String workspaceId) {
        WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse = licenseRestService.getWorkspaceLicenses(
                workspaceId).getData();
		/*if (workspaceLicensePlanInfoResponse.getLicenseProductInfoList() == null) {
			throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_WORKSPACE_LICENSE_PLAN);
		}*/

        WorkspaceLicenseInfoResponse workspaceLicenseInfoResponse = new WorkspaceLicenseInfoResponse();
        workspaceLicenseInfoResponse.setLicenseInfoList(new ArrayList<>());

        if (workspaceLicensePlanInfoResponse.getLicenseProductInfoList() != null
                && !workspaceLicensePlanInfoResponse.getLicenseProductInfoList().isEmpty()) {
            List<WorkspaceLicenseInfoResponse.LicenseInfo> licenseInfoList = workspaceLicensePlanInfoResponse.getLicenseProductInfoList()
                    .stream()
                    .map(licenseProductInfoResponse -> {
                        WorkspaceLicenseInfoResponse.LicenseInfo licenseInfo = new WorkspaceLicenseInfoResponse.LicenseInfo();
                        licenseInfo.setLicenseType(licenseProductInfoResponse.getLicenseType());
                        licenseInfo.setProductName(licenseProductInfoResponse.getProductName());
                        licenseInfo.setUseLicenseAmount(licenseProductInfoResponse.getUseLicenseAmount());
                        licenseInfo.setLicenseAmount(licenseProductInfoResponse.getUnUseLicenseAmount()
                                + licenseProductInfoResponse.getUseLicenseAmount());
                        return licenseInfo;
                    })
                    .collect(Collectors.toList());
            workspaceLicenseInfoResponse.setLicenseInfoList(licenseInfoList);
        }

        DecimalFormat decimalFormat = new DecimalFormat("0");
        long size = workspaceLicensePlanInfoResponse.getMaxStorageSize();
        workspaceLicenseInfoResponse.setMaxStorageSize(Long.parseLong(decimalFormat.format(size / 1024.0))); //MB -> GB
        workspaceLicenseInfoResponse.setMaxDownloadHit(workspaceLicensePlanInfoResponse.getMaxDownloadHit());
        workspaceLicenseInfoResponse.setMaxCallTime(workspaceLicenseInfoResponse.getMaxCallTime());

        return workspaceLicenseInfoResponse;
    }

    /***
     * 워크스페이스 정보 전체 삭제 처리
     * @param workspaceUUID - 삭제할 워크스페이스의 마스터 사용자 식별자
     * @return - 삭제 처리 결과
     */
    @Transactional
    public WorkspaceSecessionResponse deleteAllWorkspaceInfo(String workspaceUUID) {
        Optional<Workspace> workspaceInfo = workspaceRepository.findByUuid(workspaceUUID);

        // workspace 정보가 없는 경우
        if (!workspaceInfo.isPresent()) {
            return new WorkspaceSecessionResponse(workspaceUUID, true, LocalDateTime.now());
        }

        Workspace workspace = workspaceInfo.get();

        List<WorkspaceUser> workspaceUserList = workspace.getWorkspaceUserList();

        // workspace user permission 삭제
        workspaceUserPermissionRepository.deleteAllWorkspaceUserPermissionByWorkspaceUser(workspaceUserList);

        // workspace user 삭제
        workspaceUserRepository.deleteAllWorkspaceUserByWorkspace(workspace);

        // workspace history 삭제
        historyRepository.deleteAllHistoryInfoByWorkspace(workspace);

        // workspace profile 삭제 (기본 이미지인 경우 제외)
       /* if (!workspace.getProfile().equals("default")) {
            fileUploadService.delete(workspace.getProfile());
        }*/
        //file service로 default 이미지 체크 옮김
        fileUploadService.delete(workspace.getProfile());

        // workspace 삭제
        workspaceRepository.delete(workspace);

        return new WorkspaceSecessionResponse(workspaceUUID, true, LocalDateTime.now());
    }


}


