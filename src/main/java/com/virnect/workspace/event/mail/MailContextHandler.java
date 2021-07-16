package com.virnect.workspace.event.mail;

import com.virnect.workspace.dto.request.WorkspaceInviteRequest;
import com.virnect.workspace.dto.rest.InviteUserDetailInfoResponse;
import com.virnect.workspace.dto.rest.UserInfoRestResponse;
import com.virnect.workspace.global.common.RedirectProperty;
import com.virnect.workspace.global.constant.LicenseProduct;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Project: PF-Workspace
 * DATE: 2021-07-16
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@RequiredArgsConstructor
public  class MailContextHandler {
    public Context getWorkspaceInviteContext(RedirectProperty redirectProperty, String sessionCode, Locale locale, String workspaceName, WorkspaceInviteRequest.UserInfo userInfo,
                                             InviteUserDetailInfoResponse inviteUserInfo, UserInfoRestResponse masterUserInfo
    ) {
        Context context = new Context();
        context.setVariable("rejectUrl", redirectProperty.getWorkspaceServer() + "/workspaces/invite/" + sessionCode + "/reject?lang=" + locale.getLanguage());
        context.setVariable("acceptUrl", redirectProperty.getWorkspaceServer() + "/workspaces/invite/" + sessionCode + "/accept?lang=" + locale.getLanguage());
        context.setVariable("workspaceName", workspaceName);
        context.setVariable("role", userInfo.getRole());
        context.setVariable("plan", generatePlanString(userInfo.isPlanRemote(), userInfo.isPlanMake(), userInfo.isPlanView()));
        context.setVariable("workstationHomeUrl", redirectProperty.getWorkstationWeb());
        context.setVariable("supportUrl", redirectProperty.getSupportWeb());
        context.setVariable("workspaceMasterNickName", masterUserInfo.getNickname());
        context.setVariable("workspaceMasterEmail", masterUserInfo.getEmail());
        context.setVariable("responseUserEmail", userInfo.getEmail());
        context.setVariable("responseUserName", inviteUserInfo.getName());
        context.setVariable("responseUserNickName", inviteUserInfo.getNickname());
        return context;
    }

    private String generatePlanString(boolean remote, boolean make, boolean view) {
        return generatePlanList(remote, make, view).stream().map(Enum::toString).collect(Collectors.joining(","));
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

    public Context getWorkspaceInviteNonUserContext(RedirectProperty redirectProperty, String sessionCode, Locale locale, String
            workspaceName, WorkspaceInviteRequest.UserInfo userInfo, UserInfoRestResponse masterUserInfo
    ) {
        Context context = new Context();
        context.setVariable("rejectUrl", redirectProperty.getWorkspaceServer() + "/workspaces/invite/" + sessionCode + "/reject?lang=" + locale.getLanguage());
        context.setVariable("acceptUrl", redirectProperty.getWorkspaceServer() + "/workspaces/invite/" + sessionCode + "/accept?lang=" + locale.getLanguage());
        context.setVariable("workspaceName", workspaceName);
        context.setVariable("role", userInfo.getRole());
        context.setVariable("plan", generatePlanString(userInfo.isPlanRemote(), userInfo.isPlanMake(), userInfo.isPlanView()));
        context.setVariable("workstationHomeUrl", redirectProperty.getWorkstationWeb());
        context.setVariable("supportUrl", redirectProperty.getSupportWeb());
        context.setVariable("workspaceMasterNickName", masterUserInfo.getNickname());
        context.setVariable("workspaceMasterEmail", masterUserInfo.getEmail());
        context.setVariable("responseUserEmail", userInfo.getEmail());
        return context;
    }

}