package com.virnect.content.application.workspace;

import com.virnect.content.dto.rest.WorkspaceInfoListResponse;
import com.virnect.content.global.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
