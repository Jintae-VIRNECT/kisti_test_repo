package com.virnect.workspace.application.remote;

import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.workspace.application.remote.dto.SendSignalRequest;

/**
 * Project: PF-Workspace
 * DATE: 2021-12-01
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Component
public class RemoteRestFallbackFactory implements FallbackFactory<RemoteRestService> {
	@Override
	public RemoteRestService create(Throwable cause) {
		return new RemoteRestService() {
			@Override
			public void sendSignal(
				String workspaceId, SendSignalRequest sendSignalRequest
			) {
				log.error(
					"[REST - RM_SERVICE][FALLBACK] Send signal fail. req workspace uuid : {}, req body : {}, failure cause : {}",
					workspaceId, sendSignalRequest.toString(), cause.getMessage());
			}
		};
	}
}
