package com.virnect.serviceserver.application.license;


import com.virnect.serviceserver.global.common.ApiResponse;
import com.virnect.serviceserver.dto.rest.LicenseInfoListResponse;
import com.virnect.serviceserver.dto.rest.WorkspaceLicensePlanInfoResponse;

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
