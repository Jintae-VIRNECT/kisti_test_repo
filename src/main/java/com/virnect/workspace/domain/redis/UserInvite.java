package com.virnect.workspace.domain.redis;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@RedisHash(value = "userInvite")
@ToString
public class UserInvite implements Serializable {
    @Id
    private String responseUserId;
    private String responseUserEmail;
    private String responseUserName;
    private String responseUserNickName;
    private String requestUserId;
    private String requestUserEmail;
    private String requestUserName;
    private String requestUserNickName;
    private String workspaceId;
    private String workspaceName;
    private String code;
    private String role;
    private String makeType;
    private String viewType;

    @TimeToLive
    private Long expireTime;

    @Builder
    public UserInvite(String responseUserId, String responseUserEmail, String responseUserName, String responseUserNickName, String requestUserId, String requestUserEmail, String requestUserName, String requestUserNickName,
                      String workspaceId, String workspaceName, String code, String role, String makeType, String viewType, Long expireTime) {
        this.responseUserId = responseUserId;
        this.responseUserEmail = responseUserEmail;
        this.responseUserName = responseUserName;
        this.responseUserNickName = responseUserNickName;
        this.requestUserId = requestUserId;
        this.requestUserEmail = requestUserEmail;
        this.requestUserName = requestUserName;
        this.requestUserNickName = requestUserNickName;
        this.workspaceId = workspaceId;
        this.workspaceName = workspaceName;
        this.code = code;
        this.role = role;
        this.makeType = makeType;
        this.viewType = viewType;
        this.expireTime = expireTime;
    }
}
