package com.virnect.workspace.domain.redis;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@RedisHash("userInvite")
public class UserInvite implements Serializable {

    @Id
    private String joinUser;

    private String inviteUser;
    private String email;
    private String workspace;
    private String code;
    private List<Long> permission;
    private List<Map<String, String>> groups;

    @TimeToLive(unit = TimeUnit.MINUTES)
    private Long expireTime;

    @Builder
    public UserInvite(String joinUser, String inviteUser, String email, String workspace, String code, List<Long> permission, List<Map<String, String>> groups, Long expireTime){
        this.joinUser = joinUser;
        this.inviteUser = inviteUser;
        this.email = email;
        this.workspace = workspace;
        this.code= code;
        this.permission = permission;
        this.groups = groups;
        this.expireTime = expireTime;
    }

}
