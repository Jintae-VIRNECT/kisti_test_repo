package com.virnect.uaa.global.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;

import org.apache.http.entity.ContentType;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpringSecurityRestRequestSupportFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(
		HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
	) throws ServletException, IOException {
		if (request.getMethod().equals(HttpMethod.GET) ||
			!request.getContentType().equals(ContentType.APPLICATION_JSON.getMimeType())) {
			filterChain.doFilter(request, response);
		} else {
			log.info("SpringSecurityRestRequestSupportFilter - Start.");
			HttpServletRequest httpServletRequest = new JsonRequestToFormRequestWrapper(request);
			log.info("SpringSecurityRestRequestSupportFilter - Done.");
			filterChain.doFilter(httpServletRequest, response);
		}
	}
}
