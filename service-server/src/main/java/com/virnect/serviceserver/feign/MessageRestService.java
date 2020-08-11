package com.virnect.serviceserver.feign;


import com.virnect.serviceserver.api.ApiResponse;
import com.virnect.serviceserver.dto.request.PushSendRequest;
import com.virnect.serviceserver.dto.rest.PushResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//url = "http://192.168.6.3:8084"
@FeignClient(name = "message-server", fallbackFactory = MessageRestFallbackFactory.class)
public interface MessageRestService {
    @PostMapping("/messages/push")
    ApiResponse<PushResponse> sendPush(@RequestBody PushSendRequest pushSendRequest);
}
