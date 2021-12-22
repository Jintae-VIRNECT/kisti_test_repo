package com.virnect.content.application.workspace;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.virnect.content.dto.rest.WorkspaceUserResponse;
import com.virnect.content.dto.rest.WorkspaceUserListResponse;
import com.virnect.content.dto.rest.WorkspaceInfoListResponse;
import com.virnect.content.global.common.ApiResponse;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.29
 */

@FeignClient(name = "workspace-server")
public interface WorkspaceRestService {
	/**
	 * 소속된 워크스페이스 목록 조회 API
	 *
	 * @param userId - 사용자 식별 고유 번호
	 * @return - 워크스페이스 정보 목록
	 */
	@GetMapping("/workspaces")
	ApiResponse<WorkspaceInfoListResponse> getMyWorkspaceInfoList(@RequestParam("userId") String userId);

	@GetMapping("/workspaces/{workspaceId}/members/simple")
	ApiResponse<WorkspaceUserListResponse> getSimpleWorkspaceUserList(@PathVariable("workspaceId") String workspaceId);

	@GetMapping("/workspaces/{workspaceId}/members/info")
	ApiResponse<WorkspaceUserResponse> getMemberInfo(@PathVariable("workspaceId") String workspaceId, @RequestParam("userId") String userId);

}
