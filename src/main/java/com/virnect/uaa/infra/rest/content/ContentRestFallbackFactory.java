package com.virnect.uaa.infra.rest.content;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.user.dto.rest.ContentSecessionResponse;
import com.virnect.user.global.common.ApiResponse;

@Slf4j
@Component
public class ContentRestFallbackFactory
	implements FallbackFactory<ContentRestService> {
	@Override
	public ContentRestService create(Throwable cause) {
		return ((serviceId, workspaceUUID) -> {
			log.error("[CONTENT_SECESSION_ERROR] - [serviceId: {} , workspaceUUID: {}]",
				serviceId, workspaceUUID
			);
			return new ApiResponse<>(new ContentSecessionResponse(workspaceUUID, false, LocalDateTime.now()));
		});
	}
}
