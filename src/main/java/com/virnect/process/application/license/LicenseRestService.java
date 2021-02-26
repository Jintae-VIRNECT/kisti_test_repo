package com.virnect.process.application.license;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.virnect.process.dto.rest.response.license.LicenseInfoResponse;
import com.virnect.process.dto.rest.response.license.MyLicenseInfoListResponse;
import com.virnect.process.global.common.ApiResponse;

/**
 * Project: PF-ProcessManagement
 * DATE: 2021-02-25
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@FeignClient(name = "license-server")
public interface LicenseRestService {

	@GetMapping("/licenses/{workspaceId}/plan")
	ApiResponse<LicenseInfoResponse> getWorkspaceLicenseInfo(@PathVariable("workspaceId") String workspaceId);

	@GetMapping("/licenses/{workspaceId}/{userId}")
	ApiResponse<MyLicenseInfoListResponse> getMyLicenseInfoRequestHandler(
		@PathVariable("userId") String userId, @PathVariable("workspaceId") String workspaceId
	);
}
