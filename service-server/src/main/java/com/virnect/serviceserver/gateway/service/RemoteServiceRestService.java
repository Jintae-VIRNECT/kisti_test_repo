package com.virnect.serviceserver.gateway.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "remote-service-server", url = "https://192.168.13.36:5000", fallbackFactory = RemoteServiceRestFallbackFactory.class)
public interface RemoteServiceRestService {

    @GetMapping("/remote/service/sessions")
    String getServiceSessions(@RequestParam(name = "webRtcStats", required = false, defaultValue = "false") boolean webRtcStats);
    //ApiResponse<SessionListResponse> getServiceSessions(@RequestParam(name = "webRtcStats", required = false, defaultValue = "false") boolean webRtcStats);

    @PostMapping("/remote/service/sessions")
    String getSessionId(@RequestBody(required = false) Map<?, ?> params);

}
