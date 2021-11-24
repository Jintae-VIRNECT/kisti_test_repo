package com.virnect.uaa.infra.rest.license.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLicenseInfoResponse {
	private List<UserLicenseInfo> userLicenseInfos;
	private String workspaceId;

	public UserLicenseInfoResponse(List<UserLicenseInfo> userLicenseInfos, String workspaceId) {
		this.userLicenseInfos = userLicenseInfos;
		this.workspaceId = workspaceId;
	}

	@Override
	public String toString() {
		return "UserLicenseInfoResponse{" +
			"userLicenseInfos=" + userLicenseInfos +
			", workspaceId='" + workspaceId + '\'' +
			'}';
	}
}
