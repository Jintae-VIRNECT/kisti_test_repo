package com.virnect.data.dto.rest;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WorkspaceMemberInfoResponse {
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
    private LocalDateTime joinDate;
    private String[] LicenseProducts;

    @Override
    public String toString() {
        return "WorkspaceMemberInfoResponse{" +
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
