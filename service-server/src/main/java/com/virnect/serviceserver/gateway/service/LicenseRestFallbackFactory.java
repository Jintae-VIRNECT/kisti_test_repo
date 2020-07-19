package com.virnect.serviceserver.gateway.service;

import com.virnect.serviceserver.gateway.dto.rest.LicenseInfoListResponse;
import com.virnect.serviceserver.gateway.dto.rest.UserInfoResponse;
import com.virnect.serviceserver.gateway.global.common.ApiResponse;
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
                log.info("[LICENSE API FALLBACK] => USER_ID: {}");
                LicenseInfoListResponse empty = new LicenseInfoListResponse();
                empty.setLicenseInfoList(new ArrayList<>());
                return new ApiResponse<>(empty);
            }
        };
    }
}
