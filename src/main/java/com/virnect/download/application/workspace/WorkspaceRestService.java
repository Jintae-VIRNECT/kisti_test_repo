package com.virnect.download.application.workspace;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.virnect.download.dto.response.WorkspaceInfoListResponse;
import com.virnect.download.global.common.ApiResponse;
import com.virnect.download.global.config.FeignConfiguration;

/**
 * Project: PF-Download
 * DATE: 2021-11-29
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@FeignClient(name = "workspace-server", fallbackFactory = WorkspaceRestFallbackFactory.class, configuration = FeignConfiguration.class)
public interface WorkspaceRestService {

	@GetMapping("/workspaces")
	ApiResponse<WorkspaceInfoListResponse> getMyWorkspaceInfoList(@RequestParam("userId") String userUUID);
}
