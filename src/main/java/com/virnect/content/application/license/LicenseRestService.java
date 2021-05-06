package com.virnect.content.application.license;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.virnect.content.dto.rest.LicenseInfoResponse;
import com.virnect.content.dto.rest.MyLicenseInfoListResponse;
import com.virnect.content.global.common.ApiResponse;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-02-03
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: User Server Rest Client Service
 */

@FeignClient(name = "license-server")
public interface LicenseRestService {

	@GetMapping("/licenses/{workspaceId}/plan")
	ApiResponse<LicenseInfoResponse> getWorkspaceLicenseInfo(@PathVariable("workspaceId") String workspaceId);

	@GetMapping("/licenses/{workspaceId}/{userId}")
	ApiResponse<MyLicenseInfoListResponse> getMyLicenseInfoRequestHandler(@PathVariable("userId") String userId, @PathVariable("workspaceId") String workspaceId);
}

