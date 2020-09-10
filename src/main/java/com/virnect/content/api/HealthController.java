package com.virnect.content.api;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jiyong.heo
 * @project PF-ContentManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.05.28
 */

@RestController
@RequestMapping("/")
public class HealthController {
	@GetMapping("/healthCheck")
	public ResponseEntity<String> healthCheck() {
		return ResponseEntity.ok("Content Server Health Check " + LocalDateTime.now());
	}
}
