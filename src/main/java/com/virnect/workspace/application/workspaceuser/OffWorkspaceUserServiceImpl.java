package com.virnect.workspace.application.workspaceuser;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.dao.setting.WorkspaceCustomSettingRepository;
import com.virnect.workspace.dao.workspace.*;
import com.virnect.workspace.domain.setting.SettingName;
import com.virnect.workspace.domain.setting.SettingValue;
import com.virnect.workspace.domain.setting.WorkspaceCustomSetting;
import com.virnect.workspace.domain.workspace.*;
import com.virnect.workspace.dto.request.*;
import com.virnect.workspace.dto.response.WorkspaceMemberInfoListResponse;
import com.virnect.workspace.dto.response.WorkspaceMemberPasswordChangeResponse;
import com.virnect.workspace.dto.response.WorkspaceUserInfoResponse;
import com.virnect.workspace.dto.rest.*;
import com.virnect.workspace.event.mail.MailContextHandler;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.common.mapper.rest.RestMapStruct;
import com.virnect.workspace.global.constant.LicenseProduct;
import com.virnect.workspace.global.constant.Permission;
import com.virnect.workspace.global.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

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

    public OffWorkspaceUserServiceImpl(WorkspaceRepository workspaceRepository, WorkspaceUserRepository workspaceUserRepository, WorkspaceRoleRepository workspaceRoleRepository, WorkspaceUserPermissionRepository workspaceUserPermissionRepository, UserRestService userRestService, MessageSource messageSource, LicenseRestService licenseRestService, RestMapStruct restMapStruct, ApplicationEventPublisher applicationEventPublisher, WorkspaceCustomSettingRepository workspaceCustomSettingRepository, MailContextHandler mailContextHandler, WorkspacePermissionRepository workspacePermissionRepository) {
        super(workspaceRepository, workspaceUserRepository, workspaceRoleRepository, workspaceUserPermissionRepository, userRestService, messageSource, licenseRestService, restMapStruct, applicationEventPublisher, workspaceCustomSettingRepository, mailContextHandler, workspacePermissionRepository);
        this.workspaceRepository = workspaceRepository;
        this.workspaceUserRepository = workspaceUserRepository;
        this.workspaceRoleRepository = workspaceRoleRepository;
        this.workspacePermissionRepository = workspacePermissionRepository;
        this.workspaceUserPermissionRepository = workspaceUserPermissionRepository;
        this.userRestService = userRestService;
        this.licenseRestService = licenseRestService;
        this.restMapStruct = restMapStruct;
        this.workspaceCustomSettingRepository = workspaceCustomSettingRepository;
    }

    @Override
    @Transactional
    public WorkspaceMemberInfoListResponse createWorkspaceMemberAccount(
            String workspaceId, MemberAccountCreateRequest memberAccountCreateRequest
    ) {
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
            //1-1. 사용자에게 최소 1개 이상의 라이선스를 부여했는지 체크
            userLicenseValidCheck(memberAccountCreateInfo.isPlanRemote(), memberAccountCreateInfo.isPlanMake(),
                    memberAccountCreateInfo.isPlanView()
            );

            //2. user-server 멤버 정보 등록 api 요청
            RegisterMemberRequest registerMemberRequest = new RegisterMemberRequest();
            registerMemberRequest.setEmail(memberAccountCreateInfo.getId());
            registerMemberRequest.setPassword(memberAccountCreateInfo.getPassword());
            UserInfoRestResponse userInfoRestResponse = userRestService.registerMemberRequest(
                    registerMemberRequest,
                    serviceID
            )
                    .getData();

            if (userInfoRestResponse == null || !StringUtils.hasText(userInfoRestResponse.getUuid())) {
                log.error("[CREATE WORKSPACE MEMBER ACCOUNT] USER SERVER Member Register fail.");
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_ACCOUNT_CREATE_FAIL);
            }
            log.info(
                    "[CREATE WORKSPACE MEMBER ACCOUNT] USER SERVER account register success. Create UUID : [{}], Create Date : [{}]",
                    userInfoRestResponse.getUuid(), userInfoRestResponse.getCreatedDate()
            );

            //3. license-server grant api 요청 -> 실패시 user-server 롤백 api 요청
            if (memberAccountCreateInfo.isPlanRemote()) {
                MyLicenseInfoResponse myLicenseInfoResponse = licenseRestService.grantWorkspaceLicenseToUser(
                        workspaceId, userInfoRestResponse.getUuid(), "REMOTE").getData();
                if (myLicenseInfoResponse == null || !StringUtils.hasText(myLicenseInfoResponse.getProductName())) {
                    log.error(
                            "[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant fail. Request User UUID : [{}], Product License : [{}]",
                            userInfoRestResponse.getUuid(), "REMOTE"
                    );
                    UserDeleteRestResponse userDeleteRestResponse = userRestService.userDeleteRequest(
                            userInfoRestResponse.getUuid(), serviceID).getData();
                    log.error(
                            "[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant fail >>>> USER SERVER account delete process. Request User UUID : [{}], Delete Date : [{}]",
                            userDeleteRestResponse.getUserUUID(), userDeleteRestResponse.getDeletedDate()
                    );
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_LICENSE_GRANT_FAIL);
                }
                log.info(
                        "[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant success. Request User UUID : [{}], Product License : [{}]",
                        userInfoRestResponse.getUuid(), myLicenseInfoResponse.getProductName()
                );
                responseLicense.add("REMOTE");
            }
            if (memberAccountCreateInfo.isPlanMake()) {
                MyLicenseInfoResponse myLicenseInfoResponse = licenseRestService.grantWorkspaceLicenseToUser(
                        workspaceId, userInfoRestResponse.getUuid(), "MAKE").getData();
                if (myLicenseInfoResponse == null || !StringUtils.hasText(myLicenseInfoResponse.getProductName())) {
                    log.error(
                            "[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant fail. Request User UUID : [{}], Product License : [{}]",
                            userInfoRestResponse.getUuid(), "REMOTE"
                    );
                    UserDeleteRestResponse userDeleteRestResponse = userRestService.userDeleteRequest(
                            userInfoRestResponse.getUuid(), serviceID).getData();
                    log.error(
                            "[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant fail >>>> USER SERVER account delete process. Request User UUID : [{}], Delete Date : [{}]",
                            userDeleteRestResponse.getUserUUID(), userDeleteRestResponse.getDeletedDate()
                    );
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_LICENSE_GRANT_FAIL);
                }
                log.info(
                        "[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant success. Request User UUID : [{}], Product License : [{}]",
                        userInfoRestResponse.getUuid(), myLicenseInfoResponse.getProductName()
                );
                responseLicense.add("MAKE");
            }
            if (memberAccountCreateInfo.isPlanView()) {
                MyLicenseInfoResponse myLicenseInfoResponse = licenseRestService.grantWorkspaceLicenseToUser(
                        workspaceId, userInfoRestResponse.getUuid(), "VIEW").getData();
                if (myLicenseInfoResponse == null || !StringUtils.hasText(myLicenseInfoResponse.getProductName())) {
                    log.error(
                            "[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant fail. Request User UUID : [{}], Product License : [{}]",
                            userInfoRestResponse.getUuid(), "REMOTE"
                    );
                    UserDeleteRestResponse userDeleteRestResponse = userRestService.userDeleteRequest(
                            userInfoRestResponse.getUuid(), serviceID).getData();
                    log.error(
                            "[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant fail >>>> USER SERVER account delete process. Request User UUID : [{}], Delete Date : [{}]",
                            userDeleteRestResponse.getUserUUID(), userDeleteRestResponse.getDeletedDate()
                    );
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_LICENSE_GRANT_FAIL);
                }
                log.info(
                        "[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant success. Request User UUID : [{}], Product License : [{}]",
                        userInfoRestResponse.getUuid(), myLicenseInfoResponse.getProductName()
                );
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

    /*
    하위유저는 상위유저 또는 동급유저에 대한 권한이 없으므로 이에 대해 체크한다.
    단 멤버유저의 경우 동급유저(멤버)에 대한 권한을 허용한다.
     *//*
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
*/
    @Override
    @Transactional
    public boolean deleteWorkspaceMemberAccount(
            String workspaceId, MemberAccountDeleteRequest memberAccountDeleteRequest
    ) {
        if (!StringUtils.hasText(memberAccountDeleteRequest.getRequestUserPassword())) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        //1. 요청한 사람의 권한 체크
        checkWorkspaceAndUserRole(workspaceId, memberAccountDeleteRequest.getRequestUserId(), new Role[]{Role.MASTER});

        //1-1. user-server로 권한 체크
        UserInfoRestResponse userInfoRestResponse = userRestService.getUserInfoByUserId(
                memberAccountDeleteRequest.getRequestUserId()).getData();
        if (userInfoRestResponse == null || !StringUtils.hasText(userInfoRestResponse.getUuid()) || !userInfoRestResponse.getUserType().equals("WORKSPACE_ONLY_USER")) {
            log.error(
                    "[DELETE WORKSPACE MEMBER ACCOUNT] USER SERVER account not found. Request user UUID : [{}]",
                    memberAccountDeleteRequest.getRequestUserId()
            );
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }
        UserInfoAccessCheckRequest userInfoAccessCheckRequest = new UserInfoAccessCheckRequest();
        userInfoAccessCheckRequest.setEmail(userInfoRestResponse.getEmail());
        userInfoAccessCheckRequest.setPassword(memberAccountDeleteRequest.getRequestUserPassword());
        UserInfoAccessCheckResponse userInfoAccessCheckResponse = userRestService.userInfoAccessCheckRequest(
                memberAccountDeleteRequest.getRequestUserId(), userInfoAccessCheckRequest).getData();
        if (userInfoAccessCheckResponse == null || !userInfoAccessCheckResponse.isAccessCheckResult()) {
            log.error(
                    "[DELETE WORKSPACE MEMBER ACCOUNT] USER SERVER account invalid. Request user UUID : [{}]",
                    memberAccountDeleteRequest.getRequestUserId()
            );
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }

        //2. license-sever revoke api 요청
        MyLicenseInfoListResponse myLicenseInfoListResponse = licenseRestService.getMyLicenseInfoRequestHandler(
                workspaceId, memberAccountDeleteRequest.getRequestUserId()).getData();

        if (myLicenseInfoListResponse.getLicenseInfoList() != null && !myLicenseInfoListResponse.getLicenseInfoList()
                .isEmpty()) {
            myLicenseInfoListResponse.getLicenseInfoList().forEach(myLicenseInfoResponse -> {
                Boolean revokeResult = licenseRestService.revokeWorkspaceLicenseToUser(
                        workspaceId,
                        memberAccountDeleteRequest.getRequestUserId(),
                        myLicenseInfoResponse.getProductName()
                ).getData();
                if (!revokeResult) {
                    log.error(
                            "[DELETE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license revoke fail. Request user UUID : [{}], Product License [{}]",
                            memberAccountDeleteRequest.getRequestUserId(),
                            myLicenseInfoResponse.getProductName()
                    );
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_LICENSE_REVOKE_FAIL);
                }
                log.info(
                        "[DELETE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license revoke success. Request user UUID : [{}], Product License [{}]",
                        memberAccountDeleteRequest.getRequestUserId(),
                        myLicenseInfoResponse.getProductName()
                );
            });
        }

        //3. user-server에 멤버 삭제 api 요청 -> 실패시 grant api 요청
        UserDeleteRestResponse userDeleteRestResponse = userRestService.userDeleteRequest(
                memberAccountDeleteRequest.getRequestUserId(), serviceID).getData();
        if (userDeleteRestResponse == null || !StringUtils.hasText(userDeleteRestResponse.getUserUUID())) {
            log.error("[DELETE WORKSPACE MEMBER ACCOUNT] USER SERVER delete user fail.");
            if (myLicenseInfoListResponse.getLicenseInfoList() != null
                    && !myLicenseInfoListResponse.getLicenseInfoList().isEmpty()) {
                myLicenseInfoListResponse.getLicenseInfoList().forEach(myLicenseInfoResponse -> {
                    MyLicenseInfoResponse grantResult = licenseRestService.grantWorkspaceLicenseToUser(
                            workspaceId, memberAccountDeleteRequest.getRequestUserId(),
                            myLicenseInfoResponse.getProductName()
                    ).getData();
                    log.error(
                            "[DELETE WORKSPACE MEMBER ACCOUNT] USER SERVER delete user fail. >>>> LICENSE SERVER license revoke process. Request user UUID : [{}], Product License [{}]",
                            memberAccountDeleteRequest.getRequestUserId(), grantResult.getProductName()
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
        Optional<Workspace> workspace = workspaceRepository.findByUuid(workspaceId);
        WorkspaceUser workspaceUser = workspaceUserRepository.findByUserIdAndWorkspace(
                memberAccountDeleteRequest.getRequestUserId(), workspace.get());
        workspaceUserPermissionRepository.deleteAllByWorkspaceUser(workspaceUser);
        workspaceUserRepository.deleteById(workspaceUser.getId());

        log.info(
                "[DELETE WORKSPACE MEMBER ACCOUNT] Workspace delete user success. Request User UUID : [{}], Delete User UUID : [{}], DeleteDate : [{}]",
                memberAccountDeleteRequest.getRequestUserId(), memberAccountDeleteRequest.getRequestUserId(), LocalDateTime.now()
        );
        return true;
    }


    /**
     * 워크스페이스 멤버 비밀번호 변경
     *
     * @param passwordChangeRequest - 비밀번호 변경 요청 정보
     * @param workspaceId           - 워크스페이스 식별자 정보
     * @return - 워크스페이스 멤버 비밀번호 변경 처리 결과
     */
    @Override
    @Transactional
    public WorkspaceMemberPasswordChangeResponse memberPasswordChange(
            WorkspaceMemberPasswordChangeRequest passwordChangeRequest,
            String workspaceId
    ) {
        //전용 계정인지 체크
        ApiResponse<UserInfoRestResponse> apiResponse = userRestService.getUserInfoByUserId(passwordChangeRequest.getUserId());
        if (apiResponse.getCode() != 200) {
            log.error("[GET USER INFO BY USER UUID] request user uuid : {}, response code : {}, response message : {}", passwordChangeRequest.getUserId(), apiResponse.getCode(), apiResponse.getMessage());
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND);
        }
        UserInfoRestResponse userInfoRestResponse = apiResponse.getData();
        if (!userInfoRestResponse.getUserType().equals("WORKSPACE_ONLY_USER")) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_PASSWORD_CHANGE);
        }

        //변경 역할 체크
        checkWorkspaceAndUserRole(
                workspaceId, passwordChangeRequest.getRequestUserId(), new Role[]{Role.MASTER});
        MemberUserPasswordChangeRequest changeRequest = new MemberUserPasswordChangeRequest(
                passwordChangeRequest.getUserId(), passwordChangeRequest.getPassword()
        );

        ApiResponse<MemberUserPasswordChangeResponse> responseMessage = userRestService.memberUserPasswordChangeRequest(
                serviceID, changeRequest
        );

        if (responseMessage.getCode() != 200 || !responseMessage.getData().isChanged()) {
            log.error("[USER SERVER PASSWORD CHANGE REST RESULT] - [code: {}, data:{}, message: {}]",
                    responseMessage.getCode(), responseMessage.getData() == null ? "" : responseMessage.getData(),
                    responseMessage.getMessage()
            );
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_PASSWORD_CHANGE);
        }

        return new WorkspaceMemberPasswordChangeResponse(
                passwordChangeRequest.getRequestUserId(),
                responseMessage.getData().getUuid(),
                responseMessage.getData().getPasswordChangedDate()
        );

    }

    private Workspace checkWorkspaceAndUserRole(String workspaceId, String userId, Role[] roles) {
        Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        WorkspaceUserPermission workspaceUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, userId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));

        log.info(
                "[CHECK WORKSPACE USER ROLE] Acceptable User Workspace Role : {}, Present User Role : [{}]",
                Arrays.toString(roles),
                workspaceUserPermission.getWorkspaceRole().getRole()
        );
        if (Arrays.stream(roles).noneMatch(role -> workspaceUserPermission.getWorkspaceRole().getRole() == role)) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }
        return workspace;
    }

    @Override
    public ApiResponse<Boolean> inviteWorkspace(String workspaceId, WorkspaceInviteRequest workspaceInviteRequest, Locale locale) {
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

    private List<String> userLicenseValidCheck(boolean planRemote, boolean planMake, boolean planView) {
        if (!planRemote && !planMake && !planView) {
            throw new WorkspaceException(ErrorCode.ERR_INCORRECT_USER_LICENSE_INFO);
        }
        List<String> licenseProductList = new ArrayList<>();
        if (planRemote) {
            licenseProductList.add(LicenseProduct.REMOTE.toString());
        }
        if (planMake) {
            licenseProductList.add(LicenseProduct.MAKE.toString());
        }
        if (planView) {
            licenseProductList.add(LicenseProduct.VIEW.toString());
        }
        return licenseProductList;
    }

    private void checkFileSize(long requestSize) {
        if (requestSize < 0 || requestSize > (long) 3145728) {
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
