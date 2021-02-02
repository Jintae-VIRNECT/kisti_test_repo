package com.virnect.serviceserver.application.message;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.virnect.serviceserver.dto.push.PushSendRequest;
import com.virnect.serviceserver.dto.rest.PushResponse;
import com.virnect.serviceserver.global.common.ApiResponse;

@FeignClient(name = "${feign.message-prefix}", url = "${feign.message-url}", fallbackFactory = MessageRestFallbackFactory.class)
public interface MessageRestService {
    @PostMapping("/messages/push")
    ApiResponse<PushResponse> sendPush(@RequestBody PushSendRequest pushSendRequest);
}
