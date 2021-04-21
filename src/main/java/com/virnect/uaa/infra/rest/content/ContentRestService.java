package com.virnect.uaa.infra.rest.content;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.infra.rest.content.dto.ContentSecessionResponse;

@FeignClient(name = "content-server", fallbackFactory = ContentRestFallbackFactory.class)
public interface ContentRestService {
	@DeleteMapping("/contents/secession/{workspaceUUID}")
	ApiResponse<ContentSecessionResponse> contentSecession(
		@RequestHeader("serviceID") String serviceId,
		@PathVariable("workspaceUUID") String workspaceUUID
	);
}
