package com.virnect.uaa.infra.rest.remote;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.infra.rest.remote.dto.RemoteSecessionResponse;

@Slf4j
@Component
public class RemoteRestServiceFallbackFactory implements FallbackFactory<RemoteRestService> {
    @Override
    public RemoteRestService create(Throwable cause) {
        return userId -> {
            log.error("[REMOTE_SECESSION_ERROR] - [userUUID: {}]", userId);
            log.error(cause.getMessage(), cause);
            return new ApiResponse<>(new RemoteSecessionResponse(userId, false, LocalDateTime.now()));
        };
    }
}
