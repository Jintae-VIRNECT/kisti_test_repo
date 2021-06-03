package com.virnect.serviceserver.serviceremote.api;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.domain.session.SessionType;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.global.config.RemoteServiceConfig;
import com.virnect.serviceserver.serviceremote.dto.response.CoturnResponse;

@Slf4j
@RestController
@RequestMapping("/remote")
public class HealthCheckController {

    private static final String TAG = HealthCheckController.class.getSimpleName();
    private static final String REST_PATH = "/remote/healthcheck";

    @GetMapping("healthcheck")
    public ResponseEntity<String> healthCheck() {
        LogMessage.formedInfo(
            TAG,
            "REST API: POST " + REST_PATH,
            "healthCheck"
        );
        return ResponseEntity.ok("WELCOME VIRNECT REMOTE SERVICE at " + LocalDateTime.now());
    }
}
