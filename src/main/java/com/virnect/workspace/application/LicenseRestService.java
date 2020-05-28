package com.virnect.workspace.application;

import com.virnect.workspace.dto.rest.MyLicenseInfoListResponse;
import com.virnect.workspace.dto.rest.WorkspaceLicensePlanInfoResponse;
import com.virnect.workspace.global.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Project: PF-Workspace
 * DATE: 2020-04-23
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@FeignClient(name = "license-server", fallbackFactory = LicenseRestFallbackFactory.class)
public interface LicenseRestService {
    @GetMapping(value = "/licenses/{workspaceId}/plan")
    ApiResponse<WorkspaceLicensePlanInfoResponse> getWorkspaceLicenses(@PathVariable("workspaceId") String workspaceId);

    @GetMapping("/licenses/{workspaceId}/{userId}")
    ApiResponse<MyLicenseInfoListResponse> getMyLicenseInfoRequestHandler(@PathVariable("userId") String userId, @PathVariable("workspaceId") String workspaceId);
}
