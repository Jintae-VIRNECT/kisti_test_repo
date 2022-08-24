package com.virnect.workspace.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

import com.virnect.workspace.domain.workspace.Role;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-19
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
	private Role role;
	private Long roleId;
	private String createdDate;
	private String updatedDate;
	private LocalDateTime joinDate;
	private String[] LicenseProducts = new String[0];

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
