package com.virnect.uaa.infra.rest.process;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.infra.rest.process.dto.TaskSecessionResponse;

@FeignClient(name = "process-server", fallbackFactory = ProcessRestServiceFallbackFactory.class)
public interface ProcessRestService {
	@DeleteMapping("/tasks/secession/{workspaceUUID}")
	ApiResponse<TaskSecessionResponse> taskSecession(
		@RequestHeader("serviceID") String serviceId,
		@PathVariable("workspaceUUID") String workspaceUUID,
		@RequestParam("userUUID") String userUUID
	);
}
