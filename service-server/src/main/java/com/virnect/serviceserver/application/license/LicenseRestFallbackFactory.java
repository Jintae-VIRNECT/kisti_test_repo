package com.virnect.serviceserver.application.license;

import com.virnect.service.ApiResponse;
import com.virnect.serviceserver.dto.rest.LicenseInfoListResponse;
import com.virnect.serviceserver.dto.rest.WorkspaceLicensePlanInfoResponse;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component
public class LicenseRestFallbackFactory implements FallbackFactory<LicenseRestService> {

    @Override
    public LicenseRestService create(Throwable cause) {
        log.info(cause.getMessage(), cause);
        return new LicenseRestService() {
            @Override
            public ApiResponse<LicenseInfoListResponse> getUserLicenseValidation(String workspaceId, String userId) {
                log.info("[LICENSE API FALLBACK] => USER_ID: {}", userId);
                LicenseInfoListResponse empty = new LicenseInfoListResponse();
                empty.setLicenseInfoList(new ArrayList<>());
                return new ApiResponse<>(empty);
            }

            @Override
            public ApiResponse<WorkspaceLicensePlanInfoResponse> getWorkspacePlan(String workspaceId) {
                log.info("[LICENSE API FALLBACK] => WORKSPACE_ID: {}", workspaceId);
                WorkspaceLicensePlanInfoResponse empty = new WorkspaceLicensePlanInfoResponse();
                empty.setLicenseProductInfoList(new ArrayList<>());
                return new ApiResponse<>(empty);
            }
        };
    }
}
