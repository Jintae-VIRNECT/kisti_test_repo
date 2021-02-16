package com.virnect.serviceserver.application.record;

import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.dto.rest.StopRecordingResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${feign.record-prefix}", url = "${feign.record-url}", fallbackFactory = RecordRestFallbackFactory.class)
public interface RecordRestService {

    @DeleteMapping("/remote/recorder/workspaces/{workspaceId}/users/{userId}/recordings")
    ApiResponse<StopRecordingResponse> stopRecordingBySessionId(
            @PathVariable(name = "workspaceId") String workspaceId,
            @PathVariable(name = "userId") String userId,
            @RequestParam(name = "sessionId") String sessionId);
}
