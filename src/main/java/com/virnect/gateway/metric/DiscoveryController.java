package com.virnect.gateway.metric;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@ConditionalOnProperty(name = "gateway.discovery-log", havingValue = "true")
@RestController
@RequiredArgsConstructor
public class DiscoveryController {
	private final DiscoveryClient discoveryClient;

	@GetMapping("/discovery")
	public ResponseEntity<Map<String, List>> getAllDiscoveryClientInfoList() {
		Map<String, List> list = discoveryClient.getServices()
			.stream()
			.collect(Collectors.toMap(s -> s, s -> discoveryClient.getInstances(s)));
		return ResponseEntity.ok(list);
	}
}
