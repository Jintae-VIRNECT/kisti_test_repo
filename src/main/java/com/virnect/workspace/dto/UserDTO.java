package com.virnect.workspace.dto;

import lombok.Data;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-13
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class UserDTO {
    @Data
    public static class UserInfo {
        private String uuid;
        private String email;
        private String name;
        private String description;
        private String profile;
        private String loginLock;
    }
}
