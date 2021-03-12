package com.virnect.gateway.filter.security.session;

import java.util.Base64;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.google.common.net.HttpHeaders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

import com.virnect.gateway.error.ErrorCode;
import com.virnect.gateway.filter.security.GatewayServerAuthenticationException;
import com.virnect.security.UserDetailsImpl;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(10)
public class SessionAuthenticationFilter implements GlobalFilter {
	private final static String SESSION_NOT_FOUND = "Session Authentication - Session Cookie Not Found. Current Session Cookie Key Name is [{}] and request will be reject.";
	private final static Logger logger = Loggers.getLogger(
		"com.virnect.gateway.filter.security.session.SessionAuthenticationFilter");
	private final SessionCookieProperty sessionCookieProperty;

	@Value("${jwt.secret}")
	private String secretKey;

	@PostConstruct
	public void init() {
		log.info("Session Authentication Filter - Active.");
		log.info("Session Authentication Filter - Session Cookie Properties :: [{}].", sessionCookieProperty);
		this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	@Override
	public Mono<Void> filter(
		ServerWebExchange exchange,
		GatewayFilterChain chain
	) {

		String requestUrlPath = exchange.getRequest().getURI().getPath();
		boolean isAuthenticateSkipUrl = requestUrlPath.startsWith("/auths") ||
			requestUrlPath.startsWith("/v1/auth") ||
			requestUrlPath.startsWith("/admin") ||
			requestUrlPath.startsWith("/users/find") ||
			requestUrlPath.startsWith("/licenses/allocate/check") ||
			requestUrlPath.startsWith("/licenses/allocate") ||
			requestUrlPath.startsWith("/licenses/deallocate") ||
			requestUrlPath.contains("/licenses/deallocate") ||
			requestUrlPath.contains("/licenses/sdk/authentication") ||
			requestUrlPath.matches("^/workspaces/([a-zA-Z0-9]+)/invite/accept$") ||
			requestUrlPath.matches("^/workspaces/invite/[a-zA-Z0-9]+/(accept|reject).*$");

		if (isAuthenticateSkipUrl) {
			if (isSessionCookieExist(exchange.getRequest())) {
				return showUserSessionInfoAndDoFilter(exchange, chain, requestUrlPath);
			}
		}

		if (!isAuthorizationHeaderEmpty(exchange.getRequest()) || !isSessionCookieExist(exchange.getRequest())) {
			throw new GatewayServerAuthenticationException(ErrorCode.ERR_API_AUTHENTICATION);
		}

		return showUserSessionInfoAndDoFilter(exchange, chain, requestUrlPath);
	}

	private Mono<Void> showUserSessionInfoAndDoFilter(
		ServerWebExchange exchange, GatewayFilterChain chain, String requestUrlPath
	) {
		return exchange.getSession()
			.doOnError(e -> {
				log.info("세션을 찾지 못했소...");
				throw new SessionNotFoundException(requestUrlPath);
			})
			.doOnNext(webSession -> {
				SecurityContextImpl securityContext = webSession.getAttribute("SPRING_SECURITY_CONTEXT");
				if (securityContext != null) {
					UserDetailsImpl userDetails = (UserDetailsImpl)securityContext.getAuthentication()
						.getPrincipal();
					log.info("Security Context Principal Use Details: {}", userDetails);
				}
				log.info("WebSession --  ID: {}", webSession.getId());
				log.info("WebSession --  uuid: {}", webSession.getAttributeOrDefault("uuid", "None"));
				log.info("WebSession --  email: {}", webSession.getAttributeOrDefault("userEmail", "None"));
				log.info("WebSession --  name: {}", webSession.getAttributeOrDefault("userName", "None"));
				log.info("WebSession --  country: {}", webSession.getAttributeOrDefault("country", "None"));
				log.info("WebSession --  ip: {}", webSession.getAttributeOrDefault("ip", "None"));
			}).then(chain.filter(exchange));
	}

	private boolean isAuthorizationHeaderEmpty(ServerHttpRequest request) {
		return !request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION);
	}

	private boolean isSessionCookieExist(ServerHttpRequest request) {
		return request.getCookies().containsKey(sessionCookieProperty.getName());
	}

}
