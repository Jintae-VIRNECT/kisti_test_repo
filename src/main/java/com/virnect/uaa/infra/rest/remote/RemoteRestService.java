package com.virnect.uaa.infra.rest.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.infra.rest.remote.dto.RemoteSecessionResponse;

@FeignClient(name = "remote-service-server", fallbackFactory = RemoteRestServiceFallbackFactory.class)
public interface RemoteRestService {
	@DeleteMapping("/remote/members/{userId}")
	ApiResponse<RemoteSecessionResponse> remoteUserSecession(@PathVariable("userId") String userId);
}
