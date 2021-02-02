package com.virnect.serviceserver.application.license;


import com.virnect.service.ApiResponse;
import com.virnect.service.dto.feign.PushResponse;
import com.virnect.service.dto.service.request.PushSendRequest;
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
