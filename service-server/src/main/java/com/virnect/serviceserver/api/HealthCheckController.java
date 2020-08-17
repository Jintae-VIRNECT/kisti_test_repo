package com.virnect.serviceserver.api;

import com.virnect.data.api.IHealthCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/remote")
public class HealthCheckController implements IHealthCheck {

    private static final String REST_PATH = "/remote/healthcheck";

    @Override
    public ResponseEntity<String> healthCheck() {
        log.info("REST API: GET {}", REST_PATH);
        return ResponseEntity.ok("WELCOME VIRNECT REMOTE SERVICE at " + LocalDateTime.now());
    }

    //    @GetMapping("healthcheck")
//    public ResponseEntity<String> healthCheck() {
//        log.info("REST API: GET {}", REST_PATH);
//        return ResponseEntity.ok("WELCOME VIRNECT REMOTE SERVICE at " + LocalDateTime.now());
//    }
}
