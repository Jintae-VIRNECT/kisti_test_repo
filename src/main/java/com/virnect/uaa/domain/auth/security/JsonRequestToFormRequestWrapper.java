package com.virnect.uaa.domain.auth.security;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.http.entity.ContentType;
import org.springframework.security.authentication.AuthenticationServiceException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonRequestToFormRequestWrapper extends HttpServletRequestWrapper {
	private Map<String, String> jsonRequest;

	/**
	 * Constructs a request object wrapping the given request.
	 *
	 * @param request The request to wrap
	 *
	 * @throws IllegalArgumentException
	 *             if the request is null
	 */
	public JsonRequestToFormRequestWrapper(HttpServletRequest request) {
		super(request);
		// if request content type is application/json
		String requestBody;
		try {
			ObjectMapper mapper = new ObjectMapper();
			requestBody = request.getReader().lines().collect(Collectors.joining());
			this.jsonRequest = mapper.readValue(
				requestBody,
				new TypeReference<Map<String, String>>() {
				}
			);
		} catch (IOException e) {
			e.printStackTrace();
			throw new AuthenticationServiceException("Request Content-Type(application/json) Parsing Error");
		}
		log.trace("REQUEST BODY: [{}]", requestBody);
	}

	@Override
	public String getParameter(String name) {
		return jsonRequest.get(name);
	}

	@Override
	public String getContentType() {
		return ContentType.APPLICATION_FORM_URLENCODED.getMimeType();
	}
}
