package com.virnect.uaa.domain.auth.security.middleware;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.device.dto.response.DeviceAuthenticationResponse;
import com.virnect.uaa.global.common.AES256Utils;
import com.virnect.uaa.global.common.ApiResponse;

@Slf4j
public class DeviceAuthenticationResponseEncryptFilter extends OncePerRequestFilter {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	protected void doFilterInternal(
		HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		ResponseBodyEncryptWrapper responseWrapper = new ResponseBodyEncryptWrapper(response);
		filterChain.doFilter(request, responseWrapper);
		String responseMessage = new String(responseWrapper.getDataStream(), StandardCharsets.UTF_8);
		log.info("[DEVICE_AUTHENTICATION_RESPONSE][ENCRYPT][BEFORE] - [{}]", responseMessage);

		if (response.getHeader("encrypt").equals("false")) {
			response.getOutputStream().write(responseMessage.getBytes());
		} else {
			ApiResponse<DeviceAuthenticationResponse> deviceAuthInfo = objectMapper.readValue(
				responseMessage, new TypeReference<ApiResponse<DeviceAuthenticationResponse>>() {
				});
			String encodedResponse = AES256Utils.encrypt(deviceAuthInfo.getData().getAppSignature(), responseMessage);
			String encodingResponseMessage = encodingResponseMessageConverter(encodedResponse);
			log.info("[DEVICE_AUTHENTICATION_RESPONSE][ENCRYPT][AFTER] - [{}]", encodingResponseMessage);
			response.getOutputStream().write(encodingResponseMessage.getBytes());
		}
	}

	public String encodingResponseMessageConverter(String encodingMessage) throws JsonProcessingException {
		DeviceAuthenticationEncryptedResponse deviceAuthenticationEncryptedResponse = new DeviceAuthenticationEncryptedResponse();
		deviceAuthenticationEncryptedResponse.setData(encodingMessage);
		return objectMapper.writeValueAsString(deviceAuthenticationEncryptedResponse);
	}
}
