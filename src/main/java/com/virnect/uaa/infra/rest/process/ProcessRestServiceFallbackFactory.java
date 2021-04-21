package com.virnect.uaa.infra.rest.process;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.user.dto.rest.TaskSecessionResponse;
import com.virnect.user.global.common.ApiResponse;

@Slf4j
@Component
public class ProcessRestServiceFallbackFactory implements FallbackFactory<ProcessRestService> {
	@Override
	public ProcessRestService create(Throwable cause) {
		return ((serviceId, workspaceUUID, userUUID) -> {
			log.error("[TASK_SECESSION_ERROR] - [ serviceID: {} , workspaceUUID: {} , userUUID: {}]",
				serviceId, workspaceUUID, userUUID
			);
			log.error(cause.getMessage(), cause);
			return new ApiResponse<>(new TaskSecessionResponse(workspaceUUID, false, LocalDateTime.now()));
		});
	}
}
