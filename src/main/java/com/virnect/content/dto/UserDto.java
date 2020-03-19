package com.virnect.content.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-02-06
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDto {

    @Getter
    @Setter
    public static class UserInfo {
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
            return "UserInfo{" +
                    "uuid='" + uuid + '\'' +
                    ", email='" + email + '\'' +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", profile='" + profile + '\'' +
                    ", loginLock='" + loginLock + '\'' +
                    ", userType='" + userType + '\'' +
                    ", createdDate=" + createdDate +
                    ", updatedDate=" + updatedDate +
                    '}';
        }
    }
}
