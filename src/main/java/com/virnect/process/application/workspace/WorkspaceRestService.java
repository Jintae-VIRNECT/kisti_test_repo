package com.virnect.process.application.workspace;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.virnect.process.dto.rest.response.workspace.AllMemberInfoResponse;
import com.virnect.process.dto.rest.response.workspace.MemberListResponse;
import com.virnect.process.dto.rest.response.workspace.WorkspaceSettingInfoListResponse;
import com.virnect.process.global.common.ApiResponse;
import com.virnect.process.global.config.FeignConfiguration;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-02-03
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: User Server Rest Client Service
 */

@FeignClient(name = "workspace-server", configuration = FeignConfiguration.class)
public interface WorkspaceRestService {

	@GetMapping("/workspaces/{workspaceId}/members/simple")
	ApiResponse<MemberListResponse> getSimpleWorkspaceUserList(@PathVariable("workspaceId") String workspaceId);

	@GetMapping("/workspaces/{workspaceId}/members")
	ApiResponse<MemberListResponse> getWorkspaceUserList(
		@PathVariable("workspaceId") String workspaceId, @RequestParam("paging") boolean paging, @RequestParam("search") String search
	);

	/*@GetMapping("/workspaces/{workspaceId}/settings")
	ApiResponse<WorkspaceSettingInfoListResponse> getWorkspaceSettingList(
		@PathVariable("workspaceId") String workspaceId, @RequestParam("product") String product
	);*/

	@GetMapping("/workspaces/{workspaceId}/members/info")
	ApiResponse<AllMemberInfoResponse> getWorkspaceUserInfo(
		@PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId
	);
}
