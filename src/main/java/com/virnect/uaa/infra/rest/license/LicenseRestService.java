package com.virnect.uaa.infra.rest.license;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.infra.rest.license.dto.LicenseSecessionResponse;
import com.virnect.uaa.infra.rest.license.dto.UserLicenseInfoResponse;

@FeignClient(name = "license-server", fallbackFactory = LicenseRestServiceFallbackFactory.class)
public interface LicenseRestService {
	@DeleteMapping("/licenses/secession/{userUUID}")
	ApiResponse<LicenseSecessionResponse> licenseSecession(
		@RequestHeader("serviceID") String serviceId,
		@PathVariable("userUUID") String userUUID,
		@RequestParam("userNumber") long userNumber
	);

	@GetMapping("/licenses/{workspaceId}")
	ApiResponse<UserLicenseInfoResponse> getUserLicenseInfos(
		@PathVariable("workspaceId") String workspaceId,
		@RequestParam("userIds") List<String> userIds,
		@RequestParam("product") String product
	);
}