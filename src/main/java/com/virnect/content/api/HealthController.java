package com.virnect.content.api;

import java.time.ZonedDateTime;

import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * @author jiyong.heo
 * @project PF-ContentManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.05.28
 */

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
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
