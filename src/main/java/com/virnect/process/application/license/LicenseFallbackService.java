package com.virnect.process.application.license;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import feign.hystrix.FallbackFactory;

import com.virnect.process.dto.rest.response.license.LicenseInfoResponse;
import com.virnect.process.dto.rest.response.license.MyLicenseInfoListResponse;
import com.virnect.process.global.common.ApiResponse;

/**
 * Project: PF-ProcessManagement
 * DATE: 2021-03-09
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Service
public class LicenseFallbackService implements FallbackFactory<LicenseRestService> {
	@Override
	public LicenseRestService create(Throwable cause) {
		return new LicenseRestService() {
			@Override
			public ApiResponse<LicenseInfoResponse> getWorkspaceLicenseInfo(
				String workspaceId
			) {
				return new ApiResponse<>(new LicenseInfoResponse());
			}

			@Override
			public ApiResponse<MyLicenseInfoListResponse> getMyLicenseInfoRequestHandler(
				String userId, String workspaceId
			) {
				MyLicenseInfoListResponse myLicenseInfoListResponse = new MyLicenseInfoListResponse();
				myLicenseInfoListResponse.setLicenseInfoList(new ArrayList<>());
				return new ApiResponse<>(myLicenseInfoListResponse);
			}
		};
	}
}
