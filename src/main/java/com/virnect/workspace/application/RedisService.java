package com.virnect.workspace.application;

import com.virnect.workspace.dao.redis.UserInviteRepository;
import com.virnect.workspace.domain.redis.UserInvite;
import com.virnect.workspace.dto.WorkspaceDTO;
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

    public void setInviteInfo(String userId, String workspaceId, String inviteCode, List<WorkspaceDTO.WorkspaceInviteMemberReq> userInfoList) {
        for(WorkspaceDTO.WorkspaceInviteMemberReq metaUserInfo :userInfoList) {
            UserInvite userInvite = UserInvite.builder()
                    .inviteUser(userId)
                    .workspace(workspaceId)
                    .joinUser(metaUserInfo.getUserEmail())
                    .email(metaUserInfo.getUserEmail())
                    .code(inviteCode)
                    .permission(metaUserInfo.getWorkspacePermission())
                    .groups(metaUserInfo.getGroups())
                    .expireTime(3L)
                    .build();
            this.userInviteRepository.save(userInvite);
        }

    }


    public UserInvite getInviteInfo(String userId, String code) {
        return this.userInviteRepository.findById(userId).orElse(null);
    }
}
