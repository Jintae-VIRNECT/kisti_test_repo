package com.virnect.uaa.global.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import com.virnect.uaa.domain.auth.common.error.AuthenticationErrorCode;
import com.virnect.uaa.global.common.ErrorResponseMessage;

/**
 * @project: PF-Auth
 * @author: jeonghyeon.chang (johnmark)
 * @email: practice1356@gmail.com
 * @since: 2020.03.09
 * @description: Rest Authentication Error Entry Point Class
 */
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private final ObjectMapper objectMapper;

	@Override
	public void commence(
		HttpServletRequest request, HttpServletResponse response, AuthenticationException authException
	) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_ACCEPTED);
		response.getWriter()
			.println(objectMapper.writeValueAsString(
				ErrorResponseMessage.parseError(AuthenticationErrorCode.ERR_API_AUTHENTICATION))
			);
	}
}
