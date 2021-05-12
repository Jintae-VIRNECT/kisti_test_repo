package com.virnect.workspace.application;

import com.virnect.workspace.domain.Workspace;
import com.virnect.workspace.domain.WorkspaceRole;
import com.virnect.workspace.domain.redis.UserInvite;
import com.virnect.workspace.dto.request.MemberKickOutRequest;
import com.virnect.workspace.dto.request.MemberUpdateRequest;
import com.virnect.workspace.dto.request.WorkspaceInviteRequest;
import com.virnect.workspace.dto.response.WorkspaceNewMemberInfoResponse;
import com.virnect.workspace.dto.response.WorkspaceUserInfoListResponse;
import com.virnect.workspace.dto.response.WorkspaceUserInfoResponse;
import com.virnect.workspace.dto.response.WorkspaceUserLicenseListResponse;
import com.virnect.workspace.dto.rest.UserInfoRestResponse;
import com.virnect.workspace.global.common.ApiResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Project: PF-Workspace
 * DATE: 2021-05-10
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public interface MemberService {
    ApiResponse<WorkspaceUserInfoListResponse> getMembers(
            String workspaceId, String search, String filter, com.virnect.workspace.global.common.PageRequest pageRequest
    );

    List<WorkspaceUserInfoResponse> getSortedMemberList(
            com.virnect.workspace.global.common.PageRequest
                    pageRequest, List<WorkspaceUserInfoResponse> workspaceUserInfoResponseList
    );

    String[] getUserLicenseProductList(String workspaceId, String userId);

    List<WorkspaceNewMemberInfoResponse> getWorkspaceNewUserInfo(String workspaceId);

    @Transactional
    ApiResponse<Boolean> reviseMemberInfo(
            String workspaceId, MemberUpdateRequest memberUpdateRequest, Locale locale
    );

    //@CacheEvict(value = "userWorkspaces", key = "#requestUserId")
    @Transactional
    void updateUserPermission(
            Workspace workspace, String requestUserId, String responseUserId, WorkspaceRole workspaceRole,
            UserInfoRestResponse masterUser, UserInfoRestResponse user, Locale locale
    );

    @Transactional
    void workspaceUserLicenseHandling(
            String userId, Workspace workspace, UserInfoRestResponse masterUser, UserInfoRestResponse user,
            UserInfoRestResponse requestUser, Boolean remoteLicense, Boolean makeLicense, Boolean
                    viewLicense, Locale locale
    );

    WorkspaceUserInfoResponse getMemberInfo(String workspaceId, String userId);

    WorkspaceUserInfoListResponse getMemberInfoList(String workspaceId, String[] userIds);

    //@CacheEvict(value = "userWorkspaces", key = "#memberKickOutRequest.kickedUserId")
    @Transactional
    ApiResponse<Boolean> kickOutMember(
            String workspaceId, MemberKickOutRequest memberKickOutRequest, Locale locale
    );

    ApiResponse<Boolean> inviteWorkspace(
            String workspaceId, WorkspaceInviteRequest workspaceInviteRequest, Locale locale
    );

    RedirectView inviteWorkspaceAccept(String sessionCode, String lang) throws IOException;

    RedirectView worksapceOverJoinFailHandler(Workspace workspace, UserInvite userInvite, Locale locale);

    RedirectView worksapceOverMaxUserFailHandler(Workspace workspace, UserInvite userInvite, Locale locale);

    RedirectView workspaceOverPlanFailHandler(Workspace workspace, UserInvite
            userInvite, List<String> successPlan, List<String> failPlan, Locale locale);

    RedirectView inviteWorkspaceReject(String sessionCode, String lang);

    //@CacheEvict(value = "userWorkspaces", key = "#userId")
    ApiResponse<Boolean> exitWorkspace(String workspaceId, String userId, Locale locale);

    WorkspaceUserInfoListResponse getSimpleWorkspaceUserList(String workspaceId);

    WorkspaceUserLicenseListResponse getLicenseWorkspaceUserList(
            String workspaceId, com.virnect.workspace.global.common.PageRequest pageRequest
    );
}
