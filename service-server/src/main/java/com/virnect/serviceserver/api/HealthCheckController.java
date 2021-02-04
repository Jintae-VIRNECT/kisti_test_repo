package com.virnect.serviceserver.api;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class HealthCheckController {

    private static final String REST_PATH = "/remote/healthcheck";

    @GetMapping("healthcheck")
    public ResponseEntity<String> healthCheck() {
        log.info("REST API: GET {}", REST_PATH);
        return ResponseEntity.ok("WELCOME VIRNECT REMOTE SERVICE at " + LocalDateTime.now());
    }
}
