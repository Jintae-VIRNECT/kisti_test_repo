package com.virnect.data.application.record;

import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dto.rest.StopRecordingResponse;
import com.virnect.data.global.common.ApiResponse;

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
