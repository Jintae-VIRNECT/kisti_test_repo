package com.virnect.workspace.application.workspaceuser;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.message.MessageRestService;
import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.dao.workspace.*;
import com.virnect.workspace.domain.workspace.*;
import com.virnect.workspace.dto.onpremise.MemberAccountCreateInfo;
import com.virnect.workspace.dto.onpremise.MemberAccountCreateRequest;
import com.virnect.workspace.dto.request.MemberAccountDeleteRequest;
import com.virnect.workspace.dto.request.WorkspaceInviteRequest;
import com.virnect.workspace.dto.request.WorkspaceMemberPasswordChangeRequest;
import com.virnect.workspace.dto.response.WorkspaceMemberInfoListResponse;
import com.virnect.workspace.dto.response.WorkspaceMemberPasswordChangeResponse;
import com.virnect.workspace.dto.response.WorkspaceUserInfoResponse;
import com.virnect.workspace.dto.rest.*;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.common.RedirectProperty;
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
import org.thymeleaf.spring5.SpringTemplateEngine;

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
public class OnPWorkspaceUserServiceImpl extends WorkspaceUserService {
    private static final String serviceID = "workspace-server";
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final WorkspaceRoleRepository workspaceRoleRepository;
    private final WorkspacePermissionRepository workspacePermissionRepository;
    private final WorkspaceUserPermissionRepository workspaceUserPermissionRepository;
    private final UserRestService userRestService;
    private final LicenseRestService licenseRestService;
    private final RestMapStruct restMapStruct;

    public OnPWorkspaceUserServiceImpl(WorkspaceRepository workspaceRepository, WorkspaceUserRepository workspaceUserRepository, WorkspaceRoleRepository workspaceRoleRepository, WorkspaceUserPermissionRepository workspaceUserPermissionRepository, UserRestService userRestService, MessageRestService messageRestService, SpringTemplateEngine springTemplateEngine, MessageSource messageSource, LicenseRestService licenseRestService, RedirectProperty redirectProperty, RestMapStruct restMapStruct, ApplicationEventPublisher applicationEventPublisher, WorkspacePermissionRepository workspacePermissionRepository) {
        super(workspaceRepository, workspaceUserRepository, workspaceRoleRepository, workspaceUserPermissionRepository, userRestService, messageRestService, springTemplateEngine, messageSource, licenseRestService, redirectProperty, restMapStruct, applicationEventPublisher);
        this.workspaceRepository = workspaceRepository;
        this.workspaceUserRepository = workspaceUserRepository;
        this.workspaceRoleRepository = workspaceRoleRepository;
        this.workspacePermissionRepository = workspacePermissionRepository;
        this.workspaceUserPermissionRepository = workspaceUserPermissionRepository;
        this.userRestService = userRestService;
        this.licenseRestService = licenseRestService;
        this.restMapStruct = restMapStruct;
    }


    @Override
    @Transactional
    public WorkspaceMemberInfoListResponse createWorkspaceMemberAccount(
            String workspaceId, MemberAccountCreateRequest memberAccountCreateRequest
    ) {
        //1. 요청한 사람의 권한 체크
        Workspace workspace = checkWorkspaceAndUserRole(workspaceId, memberAccountCreateRequest.getUserId(), new String[]{"MASTER", "MANAGER"});

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
            WorkspaceRole workspaceRole = workspaceRoleRepository.findByRole(memberAccountCreateInfo.getRole().toUpperCase()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_ROLE_NOT_FOUND));
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
    @Transactional
    public boolean deleteWorkspaceMemberAccount(
            String workspaceId, MemberAccountDeleteRequest memberAccountDeleteRequest
    ) {
        //1. 요청한 사람의 권한 체크
        checkWorkspaceAndUserRole(workspaceId, memberAccountDeleteRequest.getUserId(), new String[]{"MASTER"});

        //1-1. user-server로 권한 체크
        UserInfoRestResponse userInfoRestResponse = userRestService.getUserInfoByUserId(
                memberAccountDeleteRequest.getUserId()).getData();
        if (userInfoRestResponse == null || !StringUtils.hasText(userInfoRestResponse.getUuid())) {
            log.error(
                    "[DELETE WORKSPACE MEMBER ACCOUNT] USER SERVER account not found. Request user UUID : [{}]",
                    memberAccountDeleteRequest.getUserId()
            );
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }
        UserInfoAccessCheckRequest userInfoAccessCheckRequest = new UserInfoAccessCheckRequest();
        userInfoAccessCheckRequest.setEmail(userInfoRestResponse.getEmail());
        userInfoAccessCheckRequest.setPassword(memberAccountDeleteRequest.getUserPassword());
        UserInfoAccessCheckResponse userInfoAccessCheckResponse = userRestService.userInfoAccessCheckRequest(
                memberAccountDeleteRequest.getUserId(), userInfoAccessCheckRequest).getData();
        if (userInfoAccessCheckResponse == null || !userInfoAccessCheckResponse.isAccessCheckResult()) {
            log.error(
                    "[DELETE WORKSPACE MEMBER ACCOUNT] USER SERVER account invalid. Request user UUID : [{}]",
                    memberAccountDeleteRequest.getUserId()
            );
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
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
                log.info(
                        "[DELETE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license revoke success. Request user UUID : [{}], Product License [{}]",
                        memberAccountDeleteRequest.getUserId(),
                        myLicenseInfoResponse.getProductName()
                );
            });
        }

        //3. user-server에 멤버 삭제 api 요청 -> 실패시 grant api 요청
        UserDeleteRestResponse userDeleteRestResponse = userRestService.userDeleteRequest(
                memberAccountDeleteRequest.getDeleteUserId(), serviceID).getData();
        if (userDeleteRestResponse == null || !StringUtils.hasText(userDeleteRestResponse.getUserUUID())) {
            log.error("[DELETE WORKSPACE MEMBER ACCOUNT] USER SERVER delete user fail.");
            if (myLicenseInfoListResponse.getLicenseInfoList() != null
                    && !myLicenseInfoListResponse.getLicenseInfoList().isEmpty()) {
                myLicenseInfoListResponse.getLicenseInfoList().forEach(myLicenseInfoResponse -> {
                    MyLicenseInfoResponse grantResult = licenseRestService.grantWorkspaceLicenseToUser(
                            workspaceId, memberAccountDeleteRequest.getDeleteUserId(),
                            myLicenseInfoResponse.getProductName()
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
        Optional<Workspace> workspace = workspaceRepository.findByUuid(workspaceId);
        WorkspaceUser workspaceUser = workspaceUserRepository.findByUserIdAndWorkspace(
                memberAccountDeleteRequest.getDeleteUserId(), workspace.get());
        workspaceUserPermissionRepository.deleteAllByWorkspaceUser(workspaceUser);
        workspaceUserRepository.deleteById(workspaceUser.getId());

        log.info(
                "[DELETE WORKSPACE MEMBER ACCOUNT] Workspace delete user success. Request User UUID : [{}], Delete User UUID : [{}], DeleteDate : [{}]",
                memberAccountDeleteRequest.getUserId(), memberAccountDeleteRequest.getDeleteUserId(), LocalDateTime.now()
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
        checkWorkspaceAndUserRole(
                workspaceId, passwordChangeRequest.getMasterUUID(), new String[]{"MASTER"});
        MemberUserPasswordChangeRequest changeRequest = new MemberUserPasswordChangeRequest(
                passwordChangeRequest.getMemberUUID(), passwordChangeRequest.getPassword()
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
                passwordChangeRequest.getMasterUUID(),
                responseMessage.getData().getUuid(),
                responseMessage.getData().getPasswordChangedDate()
        );

    }

    private Workspace checkWorkspaceAndUserRole(String workspaceId, String userId, String[] role) {
        Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        WorkspaceUserPermission workspaceUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, userId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));

        log.info(
                "[CHECK WORKSPACE USER ROLE] Acceptable User Workspace Role : {}, Present User Role : [{}]",
                Arrays.toString(role),
                workspaceUserPermission.getWorkspaceRole().getRole()
        );
        if (Arrays.stream(role).noneMatch(workspaceUserPermission.getWorkspaceRole().getRole()::equals)) {
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
