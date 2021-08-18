package com.virnect.license.dto.license.response;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class UserLicenseInfoResponse {
	private final List<UserLicenseInfo> userLicenseInfos;
	private final String workspaceId;
}
