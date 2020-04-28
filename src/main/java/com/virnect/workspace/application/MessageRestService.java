package com.virnect.workspace.application;

import com.virnect.workspace.dto.rest.MailRequest;
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
@FeignClient(name = "message-server")
public interface MessageRestService {
    @PostMapping(value = "/messages/mail")
    ApiResponse sendMail(@RequestBody MailRequest mailSendRequest);
}
