package com.virnect.config.api;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cloud.bootstrap.config.BootstrapPropertySource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * @author delbert park
 * @project PF-Config
 * @email delbert@virnect.com
 * @description
 * @since 2020.09.21
 */
@RestController
@RequestMapping("/")
@PropertySource("classpath:version.properties")
public class HealthCheckController {

    @Value("${app.version}")
    private String version;

    @GetMapping("/healthcheck")
    public ResponseEntity<String> healthCheck() {


        String message =
            "\n\n"
                + "------------------------------------------------------------------------------\n" + "\n"
                + "   VIRNECT CONFIG SERVER\n"
                + "   ---------------------------\n" + "\n"
                + "   * SERVER_VERSION: [ " + version + " ]\n" + "\n"
                + "   * SERVER_MODE: [ " + System.getenv("VIRNECT_ENV") + " ]\n" + "\n"
                + "   * HEALTH_CHECK_DATE: [ " + ZonedDateTime.now() + " ]\n" + "\n"
                + "------------------------------------------------------------------------------\n";

        return ResponseEntity.ok(message);
    }
}
