package com.virnect.gateway.metric;

import java.time.ZonedDateTime;

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

	@GetMapping("/healthcheck")
	public ResponseEntity<String> healthCheck() {
		String message =
				"\n\n"
				+ "------------------------------------------------------------------------------\n" + "\n"
				+ "   VIRNECT PLATFORM API GATEWAY\n"
				+ "   ---------------------------\n" + "\n"
				+ "   * SERVER_MODE: [ " + System.getenv("VIRNECT_ENV") + " ]\n" + "\n"
				+ "   * HEALTH_CHECK_DATE: [ " + ZonedDateTime.now() + " ]\n" + "\n"
				+ "------------------------------------------------------------------------------\n";
		return ResponseEntity.ok(message);
	}
}
