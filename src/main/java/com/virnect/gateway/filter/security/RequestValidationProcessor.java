package com.virnect.gateway.filter.security;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

public class RequestValidationProcessor {
	private static final Logger logger = LoggerFactory.getLogger(RequestValidationProcessor.class);

	public static boolean isSkipUrl(ServerHttpRequest request) {
		String requestUrlPath = request.getURI().getPath();
		logger.info("RequestValidationProcessing - permitAllUrl :: Skip Url Check about [{}]", requestUrlPath);
		return requestUrlPath.startsWith("/auth") ||
			requestUrlPath.startsWith("/v1/auth") ||
			requestUrlPath.startsWith("/admin") ||
			requestUrlPath.startsWith("/users/find") ||
			requestUrlPath.startsWith("/licenses/allocate/check") ||
			requestUrlPath.startsWith("/licenses/allocate") ||
			requestUrlPath.startsWith("/licenses/deallocate") ||
			requestUrlPath.contains("/licenses/deallocate") ||
			requestUrlPath.contains("/licenses/sdk/authentication") ||
			requestUrlPath.contains(" /workspaces/setting") ||
			requestUrlPath.matches("^/workspaces/([a-zA-Z0-9]+)/invite/accept$") ||
			requestUrlPath.matches("^/workspaces/invite/[a-zA-Z0-9]+/(accept|reject).*$");
	}

	public static boolean isRequestAuthenticationProcessSkip(ServerHttpRequest request) {
		return isSkipUrl(request) || isSessionAuthenticationRequest(request) || allowedRequest(request);
	}

	private static boolean allowedRequest(ServerHttpRequest request) {
		// Client Ip Check
		String clientIp = request.getHeaders().getFirst("X-Forwarded-For");
		if (clientIp == null) {
			clientIp = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
		}
		logger.info("RequestValidationProcessing - ClientIPCheck :: Skip Url Check about [{}]", clientIp);

		if (!clientIp.equals("121.162.3.204")) {
			logger.info("RequestValidationProcessing - ClientIPCheck :: Not Allowed IP");
			return false;
		}

		// if (request.getHeaders().getHost() != null &&
		// 	request.getHeaders().getHost().getHostName().equals("192.168.6.3:8073")) {
		// 	logger.info(
		// 		"RequestValidationProcessing - Client Host Check [{}]:: Allowed IP",
		// 		request.getHeaders().getHost().getHostName()
		// 	);
		// 	return true;
		// }

		return true;
	}

	public static boolean isSwaggerApiDocsUrl(ServerHttpRequest request) {
		logger.info(
			"RequestValidationProcessing - SwaggerUrl :: Skip Url Check about [{}]", request.getURI().getPath());
		return request.getURI().getPath().contains("/v2/api-docs");
	}

	public static boolean isSessionAuthenticationRequest(ServerHttpRequest request) {
		logger.info("RequestValidationProcessing - Session :: Session Cookie Exist Check");
		return request.getCookies().keySet().stream().anyMatch(key -> key.contains("VSESSION"));
		// return request.getCookies().size() > 0 && !request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION);

	}

	public static boolean isJwtAuthenticationRequest(ServerHttpRequest request) {
		logger.info("RequestValidationProcessing - Jwt :: Authorization Header Bearer Token Exist Check");
		return request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION);
	}
}
