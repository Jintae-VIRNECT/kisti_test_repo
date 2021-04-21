package com.virnect.uaa.global.security.user;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.application.rest.user.UserRestService;
import com.virnect.uaa.domain.auth.dto.rest.UserDetailsInfoResponse;
import com.virnect.uaa.domain.auth.dto.user.ClientGeoIPInfo;
import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.global.common.ClientUserAgentInformationParser;
import com.virnect.security.UserDetailsImpl;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final ObjectMapper objectMapper;
	private final UserAccessLogRepository userAccessLogRepository;
	private final ClientUserAgentInformationParser clientUserAgentInformationParser;
	private final UserRestService userRestService;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request, HttpServletResponse response,
		Authentication authentication
	) throws IOException {
		UserDetailsImpl userDetails = (UserDetailsImpl)authentication.getPrincipal();

		log.info(
			"Login Success - [email: {}, name: {}, uuid: {}, authorities: {}]", userDetails.getEmail(),
			userDetails.getName(), userDetails.getUuid(), authentication.getAuthorities()
		);

		ClientGeoIPInfo clientGeoIPInfo = clientUserAgentInformationParser.getClientGeoIPInformation(request);
		HttpSession loginUserSession = request.getSession();

		saveUserInfoInSession(userDetails, clientGeoIPInfo, loginUserSession);
		saveUserAccessLog(userDetails, clientGeoIPInfo);

		ApiResponse<UserDetailsInfoResponse> userDetailsInfoResponseApiResponse = userRestService.getUserDetailsInfo("auth-server",userDetails.getUuid());

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write(objectMapper.writeValueAsString(userDetailsInfoResponseApiResponse));
	}

	protected void saveUserInfoInSession(
		UserDetailsImpl userDetails, ClientGeoIPInfo clientGeoIPInfo, HttpSession session
	) {
		log.info("Login Success - Save User Information in Session....");

		session.setAttribute("userId", userDetails.getUserId());
		session.setAttribute("userUUID", userDetails.getUuid());
		session.setAttribute("userEmail", userDetails.getEmail());
		session.setAttribute("userName", userDetails.getUsername());
		session.setAttribute("userType", userDetails.getUserType());
		session.setAttribute("ip", clientGeoIPInfo.getIp());
		session.setAttribute("location", clientGeoIPInfo.getLocation());
		session.setAttribute("country", clientGeoIPInfo.getCountry());
		session.setAttribute("userAgent", clientGeoIPInfo.getUserAgent());

		log.info("Login Success - Save User Information in Session.... Done!");
	}

	@Transactional
	protected void saveUserAccessLog(UserDetailsImpl userDetails, ClientGeoIPInfo clientGeoIPInfo) {
		User user = new User();
		user.setId(userDetails.getUserId());

		log.info("Login Success - Save User Access Log....");
		UserAccessLog userAccessLog = UserAccessLog.builder()
			.user(user)
			.deviceDetails(clientGeoIPInfo.getDeviceDetails())
			.ip(clientGeoIPInfo.getIp())
			.location(clientGeoIPInfo.getLocation())
			.country(clientGeoIPInfo.getCountry())
			.lastLoggedIn(LocalDateTime.now())
			.userAgent(clientGeoIPInfo.getUserAgent())
			.build();

		userAccessLogRepository.save(userAccessLog);

		log.info("Login Success - Access Log => {}", userAccessLog);

		log.info("Login Success - Save User Access Log..Done!");

	}
}
