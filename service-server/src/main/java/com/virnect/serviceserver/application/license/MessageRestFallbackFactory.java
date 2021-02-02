package com.virnect.serviceserver.application.license;


import com.virnect.serviceserver.global.common.ApiResponse;
import com.virnect.serviceserver.dto.rest.PushResponse;
import com.virnect.serviceserver.dto.push.PushSendRequest;
import com.virnect.serviceserver.application.message.MessageRestService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
