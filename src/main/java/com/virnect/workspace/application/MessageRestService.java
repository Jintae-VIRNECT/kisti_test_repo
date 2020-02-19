package com.virnect.workspace.application;

import com.virnect.workspace.dto.request.WorkspaceInviteMailRequest;
import com.virnect.workspace.dto.response.WorkspaceInviteResponse;
import com.virnect.workspace.global.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-13
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@FeignClient(name = "MessageServer", url = "${message.serverUrl}")
public interface MessageRestService {
    @PostMapping("workspace/invite")
    ApiResponse<WorkspaceInviteResponse> sendMail(@RequestBody WorkspaceInviteMailRequest workspaceInviteMailRequest);
}
