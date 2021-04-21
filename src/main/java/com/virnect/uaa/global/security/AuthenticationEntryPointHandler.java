package com.virnect.uaa.global.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {
	@Override
	public void commence(
		HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException
	) throws IOException, ServletException {
		log.error("Authentication Error - {}", authException.getMessage());

		// return
	}
}
