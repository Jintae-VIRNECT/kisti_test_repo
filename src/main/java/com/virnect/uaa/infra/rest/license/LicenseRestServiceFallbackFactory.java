package com.virnect.uaa.infra.rest.license;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.infra.rest.license.dto.LicenseSecessionResponse;

@Slf4j
@Component
public class LicenseRestServiceFallbackFactory implements FallbackFactory<LicenseRestService> {
	@Override
	public LicenseRestService create(Throwable cause) {
		return ((serviceId, userUUID, userNumber) -> {
			log.error("[LICENSE_SECESSION_ERROR] - [ serviceId: {} , userUUID: {}, userNumber: {} ]",
				serviceId, userUUID, userNumber
			);
			log.error(cause.getMessage(), cause);
			return new ApiResponse<>(new LicenseSecessionResponse(userUUID, false, LocalDateTime.now()));
		});
	}
}
