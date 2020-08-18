package com.virnect.data.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/remote")
public interface IHealthCheckRestAPI {

    @GetMapping("healthcheck")
    ResponseEntity<String> healthCheck();
}
