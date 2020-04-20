package com.virnect.workspace.domain.redis;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
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
    private String joinUserId;
    private String inviteUserId;
    private String name;
    private String email;
    private String workspaceId;
    private String code;
    private String role;
    private String makeType;
    private String viewType;

    @TimeToLive(unit = TimeUnit.MINUTES)
    private Long expireTime;

    @Builder
    public UserInvite(String joinUserId, String inviteUserId, String name, String email, String workspaceId, String code, String role, String makeType, String viewType, Long expireTime) {
        this.joinUserId = joinUserId;
        this.inviteUserId = inviteUserId;
        this.name = name;
        this.email = email;
        this.workspaceId = workspaceId;
        this.code = code;
        this.role = role;
        this.makeType = makeType;
        this.viewType = viewType;
        this.expireTime = expireTime;
    }
}
