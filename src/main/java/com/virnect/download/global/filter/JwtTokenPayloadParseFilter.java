package com.virnect.download.global.filter;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.impl.TextCodec;
import lombok.extern.slf4j.Slf4j;

/**
 * Project: PF-Workspace
 * DATE: 2021-09-14
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Component
public class JwtTokenPayloadParseFilter extends OncePerRequestFilter {
	 private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	protected void doFilterInternal(
		HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
	) throws IOException, ServletException {
		String authorizationToken = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (StringUtils.hasText(authorizationToken) && authorizationToken.startsWith("Bearer ")) {
			String[] payload = authorizationToken.split("\\.");
			String decodedPayload = TextCodec.BASE64URL.decodeToString(payload[1]);
			JwtTokenPayload jwtTokenPayload = objectMapper.readValue(decodedPayload, JwtTokenPayload.class);
			MDC.put("userUUID", jwtTokenPayload.getUuid());
		}
		filterChain.doFilter(request, response);
		MDC.clear();
	}
}
