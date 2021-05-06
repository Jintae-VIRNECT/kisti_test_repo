package com.virnect.data.application.license;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dto.rest.LicenseInfoListResponse;
import com.virnect.data.dto.rest.WorkspaceLicensePlanInfoResponse;
import com.virnect.data.global.common.ApiResponse;

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
