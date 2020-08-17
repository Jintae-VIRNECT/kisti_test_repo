package com.virnect.data.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public interface IHealthCheck {
    @GetMapping("healthcheck")
    ResponseEntity<String> healthCheck();
}
