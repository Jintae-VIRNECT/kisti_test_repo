package com.virnect.gateway.filter.cors;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.DefaultCorsProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CorsCustomProcessor extends DefaultCorsProcessor {
	@Override
	protected String checkOrigin(CorsConfiguration config, String requestOrigin) {
		log.info("오리진 무적권 패스");
		return requestOrigin;
	}
}
