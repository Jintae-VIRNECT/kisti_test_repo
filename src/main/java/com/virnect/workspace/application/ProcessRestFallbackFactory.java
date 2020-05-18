package com.virnect.workspace.application;

import com.virnect.workspace.dto.rest.SubProcessCountResponse;
import com.virnect.workspace.global.common.ApiResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Project: PF-Workspace
 * DATE: 2020-04-29
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Component
public class ProcessRestFallbackFactory implements FallbackFactory<ProcessRestService> {
    @Override
    public ProcessRestService create(Throwable cause) {
        log.error(cause.getMessage(), cause);
        return workerUUID -> {
            SubProcessCountResponse subProcessCountResponse = new SubProcessCountResponse();
            return new ApiResponse<>(subProcessCountResponse);
        };
    }
}
