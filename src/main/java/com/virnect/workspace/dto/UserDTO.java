package com.virnect.workspace.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-17
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: UserInfo Data Transfer Object From User Server
 */
public class UserDTO {

    @Getter
    @Setter
    public static class UserInfoDTO {
        private String uuid;
        private String email;
        private String name;
        private String description;
        private String profile;
        private String loginLock;
        private String userType;
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;

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
                    ", createdDate='" + createdDate + '\'' +
                    ", updatedDate='" + updatedDate + '\'' +
                    '}';
        }
    }

}
