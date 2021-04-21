package com.virnect.uaa.infra.rest.workspace;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.infra.rest.workspace.dto.WorkspaceInfoListResponse;
import com.virnect.uaa.infra.rest.workspace.dto.WorkspaceSecessionResponse;

/**
 * Project: user
 * DATE: 2020-01-17
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@FeignClient(name = "workspace-server", fallbackFactory = WorkspaceRestFallbackFactory.class)
public interface WorkspaceRestService {
	/**
	 * 소속된 워크스페이스 목록 조회 API
	 *
	 * @param userId - 사용자 식별 고유 번호
	 * @return - 워크스페이스 정보 목록
	 */
	@GetMapping("/workspaces")
	ApiResponse<WorkspaceInfoListResponse> getMyWorkspaceInfoList(
		@RequestParam("userId") String userId, @RequestParam("size") int size
	);

	/**
	 * 사용자 관련 워크스페이스 정보 삭제 API
	 * @param service - 계정 서버 명
	 * @param userUUID - 사용자 식별자
	 * @return - 삭제 처리 결과
	 */
	@DeleteMapping("/workspaces/secession/{userUUID}")
	ApiResponse<WorkspaceSecessionResponse> workspaceSecession(
		@RequestHeader("serviceID") String service,
		@PathVariable("userUUID") String userUUID
	);

	@GetMapping("/workspaces/invite/{sessionCode}/accept")
	void acceptInviteSession(
		@PathVariable("sessionCode") String sessionCode,
		@RequestParam(name = "lang") String lang
	);
}
