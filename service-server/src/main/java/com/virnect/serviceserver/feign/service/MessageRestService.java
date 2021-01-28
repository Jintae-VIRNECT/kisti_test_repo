package com.virnect.serviceserver.feign.service;


import com.virnect.service.ApiResponse;
import com.virnect.service.dto.feign.PushResponse;
import com.virnect.service.dto.service.request.PushSendRequest;
import com.virnect.serviceserver.feign.MessageRestFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${feign.message-prefix}", url = "${feign.message-url}", fallbackFactory = MessageRestFallbackFactory.class)
public interface MessageRestService {
    @PostMapping("/messages/push")
    ApiResponse<PushResponse> sendPush(@RequestBody PushSendRequest pushSendRequest);
}
