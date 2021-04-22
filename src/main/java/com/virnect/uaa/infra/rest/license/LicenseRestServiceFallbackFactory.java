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
		return ((serviceId, workspaceUUID, userUUID, userNumber) -> {
			log.error("[LICENSE_SECESSION_ERROR] - [ serviceId: {} , workspaceUUID: {}, userUUID: {}, userNumber: {} ]",
				serviceId, workspaceUUID, userUUID, userNumber
			);
			log.error(cause.getMessage(), cause);
			return new ApiResponse<>(new LicenseSecessionResponse(workspaceUUID, false, LocalDateTime.now()));
		});
	}
}
