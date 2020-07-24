package com.virnect.process.application.workspace;

import com.virnect.process.dto.rest.response.workspace.MemberListResponse;
import com.virnect.process.global.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-02-03
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: User Server Rest Client Service
 */

@FeignClient(name = "workspace-server")
public interface WorkspaceRestService {

    @GetMapping("/workspaces/{workspaceId}/members/simple")
    ApiResponse<MemberListResponse> getSimpleWorkspaceUserList(@PathVariable("workspaceId") String workspaceId);
}
