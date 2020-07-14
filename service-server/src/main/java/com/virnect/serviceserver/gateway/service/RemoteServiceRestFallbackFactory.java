package com.virnect.serviceserver.gateway.service;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class RemoteServiceRestFallbackFactory implements FallbackFactory<RemoteServiceRestService> {

    @Override
    public RemoteServiceRestService create(Throwable cause) {
        log.info(cause.getMessage(), cause);
        return new RemoteServiceRestService() {
            @Override
            public String getServiceSessions(boolean webRtcStats) {
                log.info("[REMOTE SERVICE SESSION LIST API FALLBACK] => USER_ID: {}");
                return null;
            }

            @Override
            public String getSessionId(Map<?, ?> params) {
                log.info("[REMOTE SERVICE SESSION CREATE API FALLBACK] => USER_ID: {}");
                return null;
            }

            /*@Override
            public ApiResponse<SessionListResponse> getServiceSessions(boolean webRtcStats) {
                log.info("[REMOTE SERVICE SESSION LIST API FALLBACK] => USER_ID: {}");
                SessionListResponse empty = new SessionListResponse();
                //empty.setContent(new ArrayList<>());
                return new ApiResponse<>(empty);
            }
            @Override
            public ApiResponse<UserInfoListResponse> getSessionId(boolean webRtcStats) {
                return null;
            }*/
        };
    }
}
