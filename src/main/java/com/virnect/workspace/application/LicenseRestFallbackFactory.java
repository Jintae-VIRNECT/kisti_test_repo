package com.virnect.workspace.application;

import com.virnect.workspace.dto.rest.MyLicenseInfoListResponse;
import com.virnect.workspace.dto.rest.WorkspaceLicensePlanInfoResponse;
import com.virnect.workspace.global.common.ApiResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Project: PF-Workspace
 * DATE: 2020-04-29
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Component
public class LicenseRestFallbackFactory implements FallbackFactory<LicenseRestService> {
    @Override
    public LicenseRestService create(Throwable cause) {
        return new LicenseRestService() {
            @Override
            public ApiResponse<WorkspaceLicensePlanInfoResponse> getWorkspaceLicenses(String workspaceId) {
                return null;
            }

            @Override
            public ApiResponse<MyLicenseInfoListResponse> getMyLicenseInfoRequestHandler(String userId, String workspaceId) {
                return new ApiResponse<>(new MyLicenseInfoListResponse());
            }
        };
    }
}
