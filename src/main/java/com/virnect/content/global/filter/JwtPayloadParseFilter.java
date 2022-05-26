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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.global.security.TokenProvider;

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
@RequiredArgsConstructor
public class JwtPayloadParseFilter extends OncePerRequestFilter {
	private final TokenProvider tokenProvider;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
	) throws IOException, ServletException {
		String token = tokenProvider.getTokenByRequest(request);
		if (StringUtils.hasText(token)) {
			JwtPayload payload = tokenProvider.getTokenPayload(token);
			MDC.put("userUUID", payload.getUuid());
		}
		filterChain.doFilter(request, response);
		MDC.clear();
	}
}
