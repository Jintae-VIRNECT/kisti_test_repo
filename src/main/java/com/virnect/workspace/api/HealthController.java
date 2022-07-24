package com.virnect.workspace.api;

import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import lombok.RequiredArgsConstructor;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Workspace
 * @email practice1356@gmail.com
 * @description
 * @since 2020.05.08
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class HealthController {

    private final BuildProperties buildProperties;
    @GetMapping("/healthcheck")
    public ResponseEntity<String> healthCheck() {
        String message =
            "\n\n"
                + "------------------------------------------------------------------------------\n" + "\n"
                + "   VIRNECT USER ACCOUNT AND AUTHENTICATION SERVER\n"
                + "   ---------------------------\n" + "\n"
                + "   * SERVER_VERSION: [ " + buildProperties.getVersion() + " ]\n" + "\n"
                + "   * SERVER_MODE: [ " + System.getenv("VIRNECT_ENV") + " ]\n" + "\n"
                + "   * HEALTH_CHECK_DATE: [ " + ZonedDateTime.now() + " ]\n" + "\n"
                + "------------------------------------------------------------------------------\n";
        return ResponseEntity.ok(message);
    }
}
