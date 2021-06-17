package com.virnect.process.dto.rest.response.workspace;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-ProcessManagement
 * DATE: 2021-06-15
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class WorkspaceUserInfoResponse {
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
	
}
