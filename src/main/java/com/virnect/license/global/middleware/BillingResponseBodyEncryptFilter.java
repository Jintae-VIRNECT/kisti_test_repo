package com.virnect.license.global.middleware;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import com.virnect.license.global.common.AES256Utils;

@Slf4j
public class BillingResponseBodyEncryptFilter implements Filter {
	private final String SECRET_KEY = "$37$15$TceNRIvGL$37$15$TceNRIvGL";
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {
		ResponseBodyEncryptWrapper responseWrapper = new ResponseBodyEncryptWrapper((HttpServletResponse)response);
		chain.doFilter(request, responseWrapper);
		String responseMessage = new String(responseWrapper.getDataStream(), StandardCharsets.UTF_8);
		log.info("[BILLING_RESPONSE][ENCRYPT][BEFORE] - [{}]", responseMessage);
		String encodedResponse = AES256Utils.encrypt(SECRET_KEY, responseMessage);
		String encodingResponseMessage = encodingResponseMessageConverter(encodedResponse);
		log.info("[BILLING_RESPONSE][ENCRYPT][AFTER] - [{}]", encodingResponseMessage);
		response.getOutputStream().write(encodingResponseMessage.getBytes());
	}

	public String encodingResponseMessageConverter(String encodingMessage) throws JsonProcessingException {
		EncodingRequestResponse encodingRequestResponse = new EncodingRequestResponse();
		encodingRequestResponse.setData(encodingMessage);
		return objectMapper.writeValueAsString(encodingRequestResponse);
	}
}
