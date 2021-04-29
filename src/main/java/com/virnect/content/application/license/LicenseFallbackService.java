package com.virnect.content.application.license;

import com.virnect.content.dto.rest.LicenseInfoResponse;
import com.virnect.content.dto.rest.MyLicenseInfoListResponse;
import com.virnect.content.global.common.ApiResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-02-04
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Slf4j
@Service
public class LicenseFallbackService implements FallbackFactory<LicenseRestService> {
    @Override
    public LicenseRestService create(Throwable cause) {
        return new LicenseRestService() {
            @Override
            public ApiResponse<LicenseInfoResponse> getWorkspaceLicenseInfo(String workspaceId) {
                return new ApiResponse<>(new LicenseInfoResponse());
            }

            @Override
            public ApiResponse<MyLicenseInfoListResponse> getMyLicenseInfoRequestHandler(String userId, String workspaceId) {
                MyLicenseInfoListResponse myLicenseInfoListResponse = new MyLicenseInfoListResponse();
                myLicenseInfoListResponse.setLicenseInfoList(new ArrayList<>());
                return new ApiResponse<>(myLicenseInfoListResponse);
            }
        };
    }
}
