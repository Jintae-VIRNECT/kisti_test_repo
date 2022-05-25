package com.virnect.gateway.filter.security;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

public class RequestValidationProcessor {
	private static final Logger logger = LoggerFactory.getLogger(RequestValidationProcessor.class);

	public static boolean isAuthenticationIgnoreUrl(ServerHttpRequest request) {
		String requestUrlPath = request.getURI().getPath();

		boolean isSkipUrlMatched = requestUrlPath.startsWith("/auth") ||
			requestUrlPath.startsWith("/v1/auth") ||
			requestUrlPath.startsWith("/admin") ||
			requestUrlPath.startsWith("/users/find") ||
			requestUrlPath.startsWith("/licenses/allocate/check") ||
			requestUrlPath.startsWith("/licenses/allocate") ||
			requestUrlPath.startsWith("/licenses/deallocate") ||
			requestUrlPath.startsWith("/download/list") ||
			requestUrlPath.contains("/licenses/deallocate") ||
			requestUrlPath.contains("/licenses/sdk/authentication") ||
			requestUrlPath.contains("/workspaces/setting") ||
			requestUrlPath.matches("^/workspaces/([a-zA-Z0-9]+)/invite/accept$") ||
			requestUrlPath.matches("^/remote/invitation/guest/([a-zA-Z0-9]+)$") ||
			requestUrlPath.matches("^/workspaces/invite/[a-zA-Z0-9]+/(accept|reject).*$");
		if (isSkipUrlMatched) {
			logger.info("[RequestValidationProcessing] - permitAllUrl :: Skip Url Check about [{}]", requestUrlPath);
		}
		return isSkipUrlMatched;
	}

	public static boolean process(ServerHttpRequest request) {
		return isAuthenticationIgnoreUrl(request) || hostNameCheck(request) || allowedOfficeInternalAPKDeployRequest(
			request);
	}

	private static boolean hostNameCheck(ServerHttpRequest request) {
		if (request.getHeaders().getHost() != null) {
			String hostName = request.getHeaders().getHost().getHostName();
			logger.debug("[RequestValidationProcessing] - Request HostName Check -> [{}]", hostName);
			if (hostName.equals("192.168.6.3")) {
				logger.info("[RequestValidationProcessing] - Request HostName Check Success. : -> [{}]", hostName);
				return true;
			}
		}
		return false;
	}

	private static boolean allowedOfficeInternalAPKDeployRequest(ServerHttpRequest request) {
		String requestUrlPath = request.getURI().getPath();
		if (requestUrlPath.startsWith("/download/app")) {
			logger.debug(
				"[RequestValidationProcessing] - allowedOfficeInternalAPKDeployRequest :: Skip Url Check about [{}]",
				requestUrlPath
			);

			// Client Ip Check
			String clientIp = request.getHeaders().getFirst("X-Forwarded-For");

			if (clientIp == null) {
				clientIp = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
			}

			logger.debug(
				"[RequestValidationProcessing] - allowedOfficeInternalAPKDeployRequest :: RemoteAddress Check -> [{}]",
				clientIp
			);

			if (clientIp.equals("1.232.152.48") || // office personal environment ip range
				clientIp.startsWith("10.100.") || // office freezing environment ip range
				clientIp.startsWith("10.200.") || // office develop environment ip range
				clientIp.startsWith("172.") // office public ip
			) {
				logger.info(
					"[RequestValidationProcessing] - allowedOfficeInternalAPKDeployRequest :: RemoteAddress Check Success. : -> [{}]",
					clientIp
				);
				return true;
			}
		}

		return false;
	}

	public static boolean isSwaggerApiDocsUrl(ServerHttpRequest request) {
		boolean isSwaggerApiDocsUrl = request.getURI().getPath().contains("/v2/api-docs");
		if (isSwaggerApiDocsUrl) {
			logger.info(
				"[RequestValidationProcessing] - SwaggerUrl :: Skip Url Check about [{}]", request.getURI().getPath());
		}
		return isSwaggerApiDocsUrl;
	}

	public static boolean isSessionAuthenticationRequest(ServerHttpRequest request) {
		boolean isSessionAuthenticatedRequest = request.getCookies()
			.keySet()
			.stream()
			.anyMatch(key -> key.contains("VSESSION"));
		if (isSessionAuthenticatedRequest) {
			logger.info("[RequestValidationProcessing] - Session :: Session Cookie Exist Check => True");
		}
		return isSessionAuthenticatedRequest;
	}

	public static boolean isJwtAuthenticationRequest(ServerHttpRequest request) {
		boolean isJwtAuthenticatedRequest = request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION);
		if (isJwtAuthenticatedRequest) {
			logger.info("[RequestValidationProcessing] - Jwt :: Authorization Header Bearer Token Exist Check => True");
		}
		return isJwtAuthenticatedRequest;
	}
}
