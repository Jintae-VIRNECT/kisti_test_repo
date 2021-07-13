package com.virnect.gateway.filter.cors;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.DefaultCorsProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CorsCustomProcessor extends DefaultCorsProcessor {
	@Override
	protected String checkOrigin(CorsConfiguration config, String requestOrigin) {
		log.debug("CorsCustomProcessor - CORS Origin Check SKip.");
		return requestOrigin;
	}
}
