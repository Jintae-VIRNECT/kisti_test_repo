package com.virnect.serviceserver.gateway.service;

import com.virnect.serviceserver.gateway.dto.request.PushSendRequest;
import com.virnect.serviceserver.gateway.dto.rest.LicenseInfoListResponse;
import com.virnect.serviceserver.gateway.dto.rest.PushResponse;
import com.virnect.serviceserver.gateway.global.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//url = "http://192.168.6.3:8084"
@FeignClient(name = "message-server", fallbackFactory = MessageRestFallbackFactory.class)
public interface MessageRestService {
    @PostMapping("/messages/push")
    ApiResponse<PushResponse> sendPush(@RequestBody PushSendRequest pushSendRequest);
}
