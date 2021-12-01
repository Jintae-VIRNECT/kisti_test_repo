package com.virnect.workspace.application.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.virnect.workspace.dto.rest.SendSignalRequest;

/**
 * Project: PF-Workspace
 * DATE: 2021-12-01
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@FeignClient(name = "remote-server", fallbackFactory = RemoteRestFallbackFactory.class)
public interface RemoteRestService {
	@PostMapping("/remote/room/{workspaceId}/signal")
	void sendSignal(@PathVariable("workspaceId") String workspaceId, @RequestBody SendSignalRequest sendSignalRequest);

}
