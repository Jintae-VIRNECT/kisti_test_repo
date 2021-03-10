package com.virnect.data.application.workspace;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.virnect.data.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.global.common.ApiResponse;

@FeignClient(name = "workspace-server", fallbackFactory = WorkspaceRestFallbackFactory.class)
public interface WorkspaceRestService {

	@GetMapping("/workspaces/{workspaceId}/members")
	ApiResponse<WorkspaceMemberInfoListResponse> getWorkspaceMemberInfoList(
		@PathVariable("workspaceId") String workspaceId,
		@RequestParam(value = "filter", required = false) String filter,
		@RequestParam(value = "search", required = false) String search,
		@RequestParam(value = "page") int page,
		@RequestParam(value = "size") int size
	);

	@GetMapping("/workspaces/{workspaceId}/members/info")
	ApiResponse<WorkspaceMemberInfoResponse> getWorkspaceMemberInfo(
		@PathVariable("workspaceId") String workspaceId,
		@RequestParam(value = "userId") String userId
	);


	@GetMapping("/workspaces/{workspaceId}/members/simple")
	ApiResponse<WorkspaceMemberInfoListResponse> getWorkspaceMembers(
		@PathVariable("workspaceId") String workspaceId
	);
}
