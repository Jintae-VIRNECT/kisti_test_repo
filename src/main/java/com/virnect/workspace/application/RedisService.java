package com.virnect.workspace.application;

import com.virnect.workspace.dao.redis.UserInviteRepository;
import com.virnect.workspace.domain.redis.UserInvite;
import com.virnect.workspace.dto.request.WorkspaceInviteRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisService {

    private final UserInviteRepository userInviteRepository;

    public void setInviteInfo(String userId, String workspaceId, String inviteCode, List<WorkspaceInviteRequest> workspaceInviteRequestList) {
        for (WorkspaceInviteRequest workspaceInviteInfo : workspaceInviteRequestList) {
            UserInvite userInvite = UserInvite.builder()
                    .inviteUser(userId)
                    .workspace(workspaceId)
                    .joinUser(workspaceInviteInfo.getUserEmail())//연동 후에 이름 가져오는 것으로 변경
                    .email(workspaceInviteInfo.getUserEmail())
                    .code(inviteCode)
                    .permission(workspaceInviteInfo.getWorkspacePermission())
                    .groups(workspaceInviteInfo.getGroups())
                    .expireTime(3L)
                    .build();
            this.userInviteRepository.save(userInvite);
        }
    }


    public UserInvite getInviteInfo(String userId, String code) {
        return this.userInviteRepository.findById(userId).orElse(null);
    }
}
