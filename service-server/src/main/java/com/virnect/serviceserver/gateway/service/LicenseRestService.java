package com.virnect.serviceserver.gateway.service;

import com.virnect.serviceserver.gateway.dto.rest.LicenseInfoListResponse;
import com.virnect.serviceserver.gateway.dto.rest.UserInfoListResponse;
import com.virnect.serviceserver.gateway.dto.rest.UserInfoResponse;
import com.virnect.serviceserver.gateway.global.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

//url = "http://192.168.6.3:8632"
@FeignClient(name = "license-server", fallbackFactory = LicenseRestFallbackFactory.class)
public interface LicenseRestService {

    @GetMapping("/licenses/{workspaceId}/{userId}")
    ApiResponse<LicenseInfoListResponse> getUserLicenseValidation(
            @PathVariable(name = "workspaceId") String workspaceId,
            @PathVariable(name = "userId") String userId);
}
