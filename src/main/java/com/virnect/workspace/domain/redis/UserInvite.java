package com.virnect.workspace.domain.redis;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class UserInvite implements Serializable {
    @Id
    private String sessionCode;
    private String invitedUserEmail; // 초대 받은 유저 이메일
    private String invitedUserId;   //초대받은 유저 식별자. 없으면 null
    private String requestUserId;   //초대한 유저 식별자
    private String workspaceId;
    private String role;
    private boolean planRemote;
    private boolean planMake;
    private boolean planView;
    private String planRemoteType;
    private String planMakeType;
    private String planViewType;
    private LocalDateTime invitedDate;
    private LocalDateTime updatedDate;
    @TimeToLive
    private Long expireTime;

    @Builder
    UserInvite(String sessionCode, String invitedUserEmail, String invitedUserId, String requestUserId, String workspaceId, String role,
               boolean planRemote, boolean planMake, boolean planView, String planRemoteType, String planMakeType, String planViewType,
                LocalDateTime invitedDate, LocalDateTime updatedDate, Long expireTime) {
        this.sessionCode=sessionCode;
        this.invitedUserEmail = invitedUserEmail;
        this.invitedUserId = invitedUserId;
        this.requestUserId = requestUserId;
        this.workspaceId = workspaceId;
        this.role = role;
        this.planRemote = planRemote;
        this.planMake = planMake;
        this.planView = planView;
        this.planRemoteType = planRemoteType;
        this.planMakeType = planMakeType;
        this.planViewType = planViewType;
        this.invitedDate = invitedDate;
        this.updatedDate = updatedDate;
        this.expireTime = expireTime;
    }

    @Override
    public String toString() {
        return "UserInvite{" +
                "sessionCode='" + sessionCode + '\'' +
                ", invitedUserEmail='" + invitedUserEmail + '\'' +
                ", invitedUserId='" + invitedUserId + '\'' +
                ", requestUserId='" + requestUserId + '\'' +
                ", workspaceId='" + workspaceId + '\'' +
                ", role='" + role + '\'' +
                ", planRemote=" + planRemote +
                ", planMake=" + planMake +
                ", planView=" + planView +
                ", planRemoteType='" + planRemoteType + '\'' +
                ", planMakeType='" + planMakeType + '\'' +
                ", planViewType='" + planViewType + '\'' +
                ", invitedDate=" + invitedDate +
                ", updatedDate=" + updatedDate +
                ", expireTime=" + expireTime +
                '}';
    }

    /* @Id
    private String inviteId;//userId + workspaceId
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
    private String role;
    private Boolean planRemote;
    private Boolean planMake;
    private Boolean planView;
    private String planRemoteType;
    private String planMakeType;
    private String planViewType;
    private LocalDateTime invitedDate;
    private LocalDateTime updatedDate;

    @TimeToLive
    private Long expireTime;

    @Builder
    public UserInvite(String inviteId, String responseUserId, String responseUserEmail, String responseUserName, String responseUserNickName, String requestUserId, String requestUserEmail, String requestUserName, String requestUserNickName,
                      String workspaceId, String workspaceName, String role, Boolean planRemote, Boolean planMake, Boolean planView, String planRemoteType, String planMakeType, String planViewType, LocalDateTime invitedDate, LocalDateTime updatedDate, Long expireTime) {
        this.inviteId = inviteId;
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
        this.role = role;
        this.planRemote = planRemote;
        this.planMake = planMake;
        this.planView = planView;
        this.planRemoteType = planRemoteType;
        this.planMakeType = planMakeType;
        this.planViewType = planViewType;
        this.invitedDate = invitedDate;
        this.updatedDate = updatedDate;
        this.expireTime = expireTime;
    }*/
}
