package com.virnect.uaa.infra.rest.app;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.virnect.uaa.infra.rest.app.dto.AppDetailInfoResponse;
import com.virnect.uaa.global.common.ApiResponse;

@FeignClient(name = "download-server")
public interface AppRestService {
	@GetMapping("/download/app/latest")
	ApiResponse<AppDetailInfoResponse> getLatestAppInformationByPackageName(
		@RequestParam("packageName") String packageName
	);
}
