package com.virnect.workspace.application.workspaceuser;

import com.virnect.workspace.dto.request.WorkspaceInviteRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2021-07-16
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
class WorkspaceUserServiceTest {
    @Test
    @DisplayName("워크스페이스 초대 - 마스터로 권한 부여해서 초대 할 수 없습니다.")
    void workspaceInvite_MasterInvite() {
        //given
        WorkspaceInviteRequest workspaceInviteRequest = new WorkspaceInviteRequest();
        WorkspaceInviteRequest.UserInfo userInfo = new WorkspaceInviteRequest.UserInfo();
        userInfo.setEmail("abc@abc.com");
        userInfo.setRole("MASTER");
        userInfo.setPlanRemote(false);
        userInfo.setPlanMake(false);
        userInfo.setPlanView(false);
        List<WorkspaceInviteRequest.UserInfo> userInfoList = new ArrayList<>();
        userInfoList.add(userInfo);
        workspaceInviteRequest.setUserId("master_user_id");
        workspaceInviteRequest.setUserInfoList(userInfoList);

        //then
        Assertions.assertTrue(workspaceInviteRequest.existMasterUserInvite());
    }

}