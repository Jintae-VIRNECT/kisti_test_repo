package com.virnect.download.api;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * Project: PF-Download
 * DATE: 2020-05-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class HealthController {

	private final BuildProperties buildProperties;

	@GetMapping("/healthCheck")
	public ResponseEntity<String> healthCheck() {

		String message =
			"\n\n"
				+ "------------------------------------------------------------------------------\n" + "\n"
				+ "   VIRNECT DOWNLOAD SERVER\n"
				+ "   ---------------------------\n" + "\n"
				+ "   * SERVER_VERSION: [ " + buildProperties.getVersion() + " ]\n" + "\n"
				+ "   * SERVER_MODE: [ " + System.getenv("VIRNECT_ENV") + " ]\n" + "\n"
				+ "   * HEALTH_CHECK_DATE: [ " + ZonedDateTime.now() + " ]\n" + "\n"
				+ "------------------------------------------------------------------------------\n";
		return ResponseEntity.ok(message);
	}
}
