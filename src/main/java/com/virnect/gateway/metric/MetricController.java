package com.virnect.gateway.metric;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Gateway
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.28
 */


@RestController
@RequestMapping("/")
public class MetricController {

    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("HELLO VIRNECT PLATFORM API GATEWAY");
    }
}
