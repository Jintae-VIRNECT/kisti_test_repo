package com.virnect.uaa.domain;

import java.time.ZonedDateTime;

import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description
 * @since 2020.05.14
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class HealthCheckController {

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

	@ApiOperation(value = "dummy", hidden = true)
	@GetMapping("/v1/auth/hihi")
	public ResponseEntity userPrincipalResponseEntity(@AuthenticationPrincipal Authentication authentication) {
		return ResponseEntity.ok(authentication.getPrincipal());
	}
}
