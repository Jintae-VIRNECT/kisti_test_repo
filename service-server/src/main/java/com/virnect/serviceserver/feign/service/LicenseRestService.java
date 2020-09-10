package com.virnect.serviceserver.feign.service;


import com.virnect.data.ApiResponse;
import com.virnect.data.dto.feign.LicenseInfoListResponse;
import com.virnect.data.dto.feign.WorkspaceLicensePlanInfoResponse;
import com.virnect.serviceserver.feign.LicenseRestFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.PropertySource;
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
