package com.virnect.serviceserver.feign.service;


import com.virnect.data.ApiResponse;
import com.virnect.data.dto.feign.PushResponse;
import com.virnect.data.dto.request.PushSendRequest;
import com.virnect.serviceserver.feign.MessageRestFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//url = "http://192.168.6.3:8084"
@FeignClient(name = "${feign.message-prefix}", url = "${feign.message-url}", fallbackFactory = MessageRestFallbackFactory.class)
public interface MessageRestService {
    @PostMapping("/messages/push")
    ApiResponse<PushResponse> sendPush(@RequestBody PushSendRequest pushSendRequest);
}
