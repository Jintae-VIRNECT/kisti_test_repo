package com.virnect.remote.application.license;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.virnect.remote.dto.rest.LicenseInfoListResponse;
import com.virnect.remote.dto.rest.WorkspaceLicensePlanInfoResponse;
import com.virnect.remote.global.common.ApiResponse;

@FeignClient(name = "${feign.license-prefix}", url = "${feign.license-url}", fallbackFactory = LicenseRestFallbackFactory.class)
public interface LicenseRestService {

    @GetMapping("/licenses/{workspaceId}/{userId}")
    ApiResponse<LicenseInfoListResponse> getUserLicenseValidation(
            @PathVariable(name = "workspaceId") String workspaceId,
            @PathVariable(name = "userId") String userId);

    @GetMapping("/licenses/{workspaceId}/plan")
    ApiResponse<WorkspaceLicensePlanInfoResponse> getWorkspacePlan(@PathVariable(name = "workspaceId") String workspaceId);
}
