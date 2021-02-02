package com.virnect.serviceserver.application.license;


import com.virnect.service.ApiResponse;
import com.virnect.service.dto.feign.LicenseInfoListResponse;
import com.virnect.service.dto.feign.WorkspaceLicensePlanInfoResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${feign.license-prefix}", url = "${feign.license-url}", fallbackFactory = LicenseRestFallbackFactory.class)
public interface LicenseRestService {

    @GetMapping("/licenses/{workspaceId}/{userId}")
    ApiResponse<LicenseInfoListResponse> getUserLicenseValidation(
            @PathVariable(name = "workspaceId") String workspaceId,
            @PathVariable(name = "userId") String userId);

    @GetMapping("/licenses/{workspaceId}/plan")
    ApiResponse<WorkspaceLicensePlanInfoResponse> getWorkspacePlan(@PathVariable(name = "workspaceId") String workspaceId);
}
