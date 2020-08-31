package com.virnect.data.feign.service;


import com.virnect.data.ApiResponse;
import com.virnect.data.dto.feign.LicenseInfoListResponse;
import com.virnect.data.dto.feign.WorkspaceLicensePlanInfoResponse;
import com.virnect.data.feign.LicenseRestFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//url = "http://192.168.6.3:8632"
@FeignClient(name = "license-server", fallbackFactory = LicenseRestFallbackFactory.class)
public interface LicenseRestService {

    @GetMapping("/licenses/{workspaceId}/{userId}")
    ApiResponse<LicenseInfoListResponse> getUserLicenseValidation(
            @PathVariable(name = "workspaceId") String workspaceId,
            @PathVariable(name = "userId") String userId);

    @GetMapping("/licenses/{workspaceId}/plan")
    ApiResponse<WorkspaceLicensePlanInfoResponse> getWorkspacePlan(@PathVariable(name = "workspaceId") String workspaceId);
}
