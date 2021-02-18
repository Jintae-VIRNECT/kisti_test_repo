package com.virnect.serviceserver.api.dashboard;

import java.time.ZonedDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class DashboardHealthCheckController {
	@GetMapping("/healthcheck")
	public ResponseEntity<String> healthCheck() {
		String message =
			"\n\n"
				+ "------------------------------------------------------------------------------\n" + "\n"
				+ "   VIRNECT REMOTE DASHBOARD SERVER\n"
				+ "   ---------------------------\n" + "\n"
				+ "   * SERVER_MODE: [ " + System.getenv("VIRNECT_ENV") + " ]\n" + "\n"
				+ "   * HEALTH_CHECK_DATE: [ " + ZonedDateTime.now() + " ]\n" + "\n"
				+ "------------------------------------------------------------------------------\n";
		return ResponseEntity.ok(message);
	}
}
