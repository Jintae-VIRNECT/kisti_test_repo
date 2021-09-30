package com.virnect.uaa.infra.rest.message;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.infra.rest.message.message.EventSendRequest;
import com.virnect.uaa.infra.rest.message.message.EventSendResponse;

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
