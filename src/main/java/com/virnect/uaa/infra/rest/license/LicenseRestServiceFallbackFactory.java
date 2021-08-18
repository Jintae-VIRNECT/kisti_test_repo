package com.virnect.uaa.infra.rest.license;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.infra.rest.license.dto.LicenseSecessionResponse;
import com.virnect.uaa.infra.rest.license.dto.UserLicenseInfoResponse;

@Slf4j
@Component
public class LicenseRestServiceFallbackFactory implements FallbackFactory<LicenseRestService> {
	@Override
	public LicenseRestService create(Throwable cause) {
		return new LicenseRestService() {
			@Override
			public ApiResponse<LicenseSecessionResponse> licenseSecession(
				String serviceId, String userUUID, long userNumber
			) {
				log.error("[LICENSE_SECESSION_ERROR] - [serviceId: {} , userUUID: {}, userNumber: {}]",
					serviceId, userUUID, userNumber
				);
				log.error(cause.getMessage(), cause);
				return new ApiResponse<>(new LicenseSecessionResponse(userUUID, false, LocalDateTime.now()));
			}

			@Override
			public ApiResponse<UserLicenseInfoResponse> getUserLicenseInfos(
				String workspaceId,
				List<String> userIds,
				String product
			) {
				log.error("[USER_LICENSE_INFORMATION_RETRIEVE_ERR] - [workspaceId: {} , userUUIDs: {}, product: {}]",
					workspaceId, userIds, product
				);
				log.error(cause.getMessage(), cause);
				return new ApiResponse<>(new UserLicenseInfoResponse(new ArrayList<>(), workspaceId));
			}
		};
	}
}
