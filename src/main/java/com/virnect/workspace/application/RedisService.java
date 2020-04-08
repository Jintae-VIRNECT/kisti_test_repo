package com.virnect.workspace.application;

import com.virnect.workspace.dao.redis.UserInviteRepository;
import com.virnect.workspace.domain.redis.UserInvite;
import com.virnect.workspace.dto.redis.WorkspaceInviteRedisRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
            List<UserInvite.GroupInfo> groupInfoList = userInviteInfo.getGroups().stream().map(groupInfo -> modelMapper.map(groupInfo, UserInvite.GroupInfo.class)).collect(Collectors.toList());

            UserInvite userInvite = UserInvite.builder()
                    .inviteUser(workspaceInviteRequest.getUserId())
                    .workspace(workspaceInviteRequest.getWorkspaceId())
                    .joinUser(userInviteInfo.getName())//연동 후에 이름 가져오는 것으로 변경
                    .email(userInviteInfo.getEmail())
                    .code(workspaceInviteRequest.getInviteCode())
                    .permission(userInviteInfo.getPermission())
                    .groups(groupInfoList)
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
