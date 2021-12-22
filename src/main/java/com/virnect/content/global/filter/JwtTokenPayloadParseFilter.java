package com.virnect.content.global.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
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
@Order(2)
public class JwtTokenPayloadParseFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(
		HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
	) throws IOException, ServletException {
		String authorizationToken = request.getHeader("Authorization");
		if (StringUtils.hasText(authorizationToken) && authorizationToken.startsWith("Bearer ")) {
			String[] payload = authorizationToken.split("\\.");
			String decodedPayload = TextCodec.BASE64URL.decodeToString(payload[1]);
			ObjectMapper objectMapper = new ObjectMapper();
			JwtTokenPayload jwtTokenPayload = objectMapper.readValue(decodedPayload, JwtTokenPayload.class);
			MDC.put("userUUID", jwtTokenPayload.getUuid());
		}
		filterChain.doFilter(request, response);
		MDC.clear();
	}
}
