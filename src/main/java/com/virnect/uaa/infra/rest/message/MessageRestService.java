package com.virnect.uaa.infra.rest.message;

import com.virnect.uaa.domain.auth.dto.message.EventSendRequest;
import com.virnect.uaa.domain.auth.dto.message.EventSendResponse;
import com.virnect.uaa.global.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Project: PF-Auth
 * DATE: 2021-03-08
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@FeignClient(name = "message-server")
public interface MessageRestService {
    @PostMapping("/messages/event")
    ApiResponse<EventSendResponse> sendMessage(@RequestBody EventSendRequest eventSendRequest);
}
