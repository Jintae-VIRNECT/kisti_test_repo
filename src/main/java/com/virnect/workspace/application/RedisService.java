package com.virnect.workspace.application;

import com.virnect.workspace.dao.redis.UserInviteRepository;
import com.virnect.workspace.domain.redis.UserInvite;
import com.virnect.workspace.dto.redis.WorkspaceInviteRedisRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
    private final ModelMapper modelMapper;
    public void setInviteInfo(WorkspaceInviteRedisRequest workspaceInviteRequest) {
        for (WorkspaceInviteRedisRequest.UserInfo userInviteInfo : workspaceInviteRequest.getUserInfoList()) {
            System.out.println("존재??:"+userInviteInfo.getExistUser());
            UserInvite userInvite = UserInvite.builder()
                    .inviteUser(workspaceInviteRequest.getUserId())
                    .workspace(workspaceInviteRequest.getWorkspaceId())
                    .joinUser(userInviteInfo.getName())//연동 후에 이름 가져오는 것으로 변경
                    .email(userInviteInfo.getEmail())
                    .code(workspaceInviteRequest.getInviteCode())
                    .permission(userInviteInfo.getPermission())
                    .groupName(userInviteInfo.getGroupName())
                    .groupRole(userInviteInfo.getGroupRole())
                    .existUser(userInviteInfo.getExistUser())
                    .expireTime(3L)
                    .build();
            this.userInviteRepository.save(userInvite);
        }
    }


    public UserInvite getInviteInfo(String userId, String code) {
        return this.userInviteRepository.findById(userId).orElse(null);
    }
}
