package com.virnect.remote.application.message;


import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.remote.dto.rest.PushResponse;
import com.virnect.remote.dto.rest.PushSendRequest;
import com.virnect.remote.global.common.ApiResponse;

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
