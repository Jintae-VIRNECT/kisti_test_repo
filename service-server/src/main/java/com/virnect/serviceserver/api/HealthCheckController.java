package com.virnect.serviceserver.api;

import com.virnect.service.api.IHealthCheckRestAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
public class HealthCheckController implements IHealthCheckRestAPI {

    private static final String REST_PATH = "/remote/healthcheck";

    @Override
    public ResponseEntity<String> healthCheck() {
        log.info("REST API: GET {}", REST_PATH);
        return ResponseEntity.ok("WELCOME VIRNECT REMOTE SERVICE at " + LocalDateTime.now());
    }
}
