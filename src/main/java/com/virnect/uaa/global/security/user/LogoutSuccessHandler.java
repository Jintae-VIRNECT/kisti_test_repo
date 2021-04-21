package com.virnect.uaa.global.security.user;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.dto.user.response.SessionLogoutResponse;
import com.virnect.uaa.global.common.ApiResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
	private final ObjectMapper objectMapper;

	@Override
	public void onLogoutSuccess(
		HttpServletRequest request, HttpServletResponse response,
		Authentication authentication
	) throws IOException {
		if (authentication == null) {
			log.error("로그아웃 요청에 쿠키 정보가 없음.");
		} else {
			User user = (User)authentication.getPrincipal();
			log.info("LogoutSuccessHandler - userName: [{}]", user.getUsername());
			log.info("LogoutSuccessHandler - authorities: {}", Arrays.toString(
				user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray())
			);
		}
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter()
			.write(objectMapper.writeValueAsString(
				new ApiResponse<>(new SessionLogoutResponse(true))
			));
	}
}