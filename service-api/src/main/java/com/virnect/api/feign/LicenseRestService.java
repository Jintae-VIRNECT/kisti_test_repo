package com.virnect.api.feign;


import com.virnect.api.ApiResponse;
import com.virnect.api.dto.rest.LicenseInfoListResponse;
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
}
