package com.virnect.serviceserver.gateway.service;

import com.virnect.serviceserver.gateway.dto.request.PushSendRequest;
import com.virnect.serviceserver.gateway.dto.rest.LicenseInfoListResponse;
import com.virnect.serviceserver.gateway.dto.rest.PushResponse;
import com.virnect.serviceserver.gateway.global.common.ApiResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component
public class MessageRestFallbackFactory implements FallbackFactory<MessageRestService> {
    @Override
    public MessageRestService create(Throwable cause) {
        log.info(cause.getMessage(), cause);
        return new MessageRestService() {
            @Override
            public ApiResponse<PushResponse> sendPush(PushSendRequest pushSendRequest) {
                log.info("[MESSAGE API FALLBACK] => USER_ID: {}", pushSendRequest.getUserId());
                PushResponse empty = new PushResponse();
                return new ApiResponse<>(empty);
            }
        };
    }
}
