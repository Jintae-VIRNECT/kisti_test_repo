package com.virnect.serviceserver.serviceremote.api;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.global.config.RemoteServiceBuildInfo;

@Slf4j
@RestController
@RequestMapping("/remote")
@RequiredArgsConstructor
public class HealthCheckController {

	private final RemoteServiceBuildInfo remoteServiceBuildInfo;
	private static final String TAG = HealthCheckController.class.getSimpleName();
	private static final String REST_PATH = "/remote/healthcheck";

	@GetMapping("healthcheck")
	public ResponseEntity<String> healthCheck() {
		String message =
			"\n\n"
				+ "------------------------------------------------------------------------------\n" + "\n"
				+ "   VIRNECT REMOTE SERVICE SERVER\n"
				+ "   ---------------------------\n" + "\n"
				+ "   * SERVER_VERSION: [ " + remoteServiceBuildInfo.getVersion() + " ]\n" + "\n"
				+ "   * SERVER_MODE: [ " + System.getenv("VIRNECT_ENV") + " ]\n" + "\n"
				+ "   * HEALTH_CHECK_DATE: [ " + ZonedDateTime.now() + " ]\n" + "\n"
				+ "------------------------------------------------------------------------------\n";
		return ResponseEntity.ok(message);
	}
}
