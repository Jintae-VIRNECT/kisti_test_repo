package com.virnect.serviceserver.application.record;

import com.virnect.serviceserver.global.common.ApiResponse;
import com.virnect.serviceserver.dto.rest.StopRecordingResponse;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RecordRestFallbackFactory implements FallbackFactory<RecordRestService> {

    @Override
    public RecordRestService create(Throwable cause) {
        log.info(cause.getMessage(), cause);
        return new RecordRestService() {
            @Override
            public ApiResponse<StopRecordingResponse> stopRecordingBySessionId(String workspaceId, String userId, String sessionId) {
                log.info("[RECORD API FALLBACK] => USER_ID: {}", userId);
                StopRecordingResponse empty = new StopRecordingResponse();
                return new ApiResponse<>(empty);
            }
        };
    }
}
