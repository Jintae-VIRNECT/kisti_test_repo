package com.virnect.workspace.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class MemberInfoDTO {
    private String uuid;
    private String email;
    private String name;
    private String description;
    private String profile;
    private String loginLock;
    private String userType;
    private String nickName;
    private String role;
    private Long roleId;
    private String createdDate;
    private String updatedDate;
    private Integer countProgressing;
    private Integer countAssigned;
    private LocalDateTime joinDate;
    private String[] planList;

    @Override
    public String toString() {
        return "UserInfoResponseDto{" +
                "uuid='" + uuid + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", profile='" + profile + '\'' +
                ", loginLock='" + loginLock + '\'' +
                ", userType='" + userType + '\'' +
                ", role='" + role + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", updatedDate='" + updatedDate + '\'' +
                '}';
    }
}
