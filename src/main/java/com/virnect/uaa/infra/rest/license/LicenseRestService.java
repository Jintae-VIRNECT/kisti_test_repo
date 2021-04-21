package com.virnect.uaa.infra.rest.license;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.infra.rest.license.dto.LicenseSecessionResponse;

@FeignClient(name = "license-server", fallbackFactory = LicenseRestServiceFallbackFactory.class)
public interface LicenseRestService {
	@DeleteMapping("/licenses/secession/{workspaceUUID}")
	ApiResponse<LicenseSecessionResponse> licenseSecession(
		@RequestHeader("serviceID") String serviceId,
		@PathVariable("workspaceUUID") String workspaceUUID,
		@RequestParam("userUUID") String userUUID,
		@RequestParam("userNumber") long userNumber
	);
}