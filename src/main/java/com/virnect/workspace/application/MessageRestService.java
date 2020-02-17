package com.virnect.workspace.application;

import com.virnect.workspace.dto.WorkspaceDTO;
import com.virnect.workspace.global.common.ResponseMessage;
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
    ResponseMessage sendMail(@RequestBody WorkspaceDTO.WorkspaceInviteMailReq workspaceInviteMailReq);

}
