package com.virnect.gateway.filter.security.session;

import java.util.Base64;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.google.common.net.HttpHeaders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import com.virnect.gateway.error.ErrorCode;
import com.virnect.gateway.filter.security.GatewayServerAuthenticationException;
import com.virnect.security.UserDetailsImpl;

@Slf4j
@Order(11)
@Component
@RequiredArgsConstructor
public class SessionAuthenticationFilter implements GlobalFilter {
	private final SessionCookieProperty sessionCookieProperty;

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${spring.profiles.active}")
	private String activeProfile;

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
		boolean isAuthenticateSkipUrl = requestUrlPath.startsWith("/auth") ||
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
			return chain.filter(exchange);
		}

		// if develop environment
		if ((activeProfile.equals("develop") || activeProfile.equals("onpremise")) && requestUrlPath.contains(
			"/api-docs")) {
			log.info("SessionAuthenticationFilter - Skip swagger json request like [/v2/api-docs] ss");
			return chain.filter(exchange);
		}

		// if (!isAuthorizationHeaderEmpty(exchange.getRequest()) && !isSessionCookieExist(exchange.getRequest())) {
		// 	log.info(
		// 		"SessionAuthenticationFilter - isAuthorizationHeaderEmpty = {}",
		// 		!isAuthorizationHeaderEmpty(exchange.getRequest())
		// 	);
		// 	log.info(
		// 		"SessionAuthenticationFilter - isSessionCookieExist = {}",
		// 		!isSessionCookieExist(exchange.getRequest())
		// 	);
		// 	throw new GatewayServerAuthenticationException(ErrorCode.ERR_API_AUTHENTICATION);
		// }

		if ((activeProfile.equals("develop") || activeProfile.equals("onpremise")) && !isSessionCookieExist(exchange.getRequest())) {
			log.info("SessionAuthenticationFilter - Skip session authentication none session cookie request. Only Develop Environment.");
			return chain.filter(exchange);

		}

		return showUserSessionInfoAndDoFilter(exchange, chain, requestUrlPath);
	}

	private Mono<Void> showUserSessionInfoAndDoFilter(
		ServerWebExchange exchange, GatewayFilterChain chain, String requestUrlPath
	) {
		return exchange.getSession()
			.doOnError(e -> {
				log.info("SessionAuthenticationFilter - Can't not resolve WebSession From Request");
				throw new SessionNotFoundException(requestUrlPath);
			})
			.doOnNext(webSession -> {
				SecurityContextImpl securityContext = webSession.getAttribute("SPRING_SECURITY_CONTEXT");
				if (securityContext != null) {
					UserDetailsImpl userDetails = (UserDetailsImpl)securityContext.getAuthentication()
						.getPrincipal();
					log.info("SessionAuthenticationFilter - Security Context Principal Use Details: {}", userDetails);
				}
				log.info("SessionAuthenticationFilter - WebSession :: ID: [{}]", webSession.getId());
				log.info(
					"SessionAuthenticationFilter - WebSession :: uuid: [{}]",
					webSession.getAttributeOrDefault("userUUID", "None")
				);
				log.info(
					"SessionAuthenticationFilter - WebSession :: email: [{}]",
					webSession.getAttributeOrDefault("userEmail", "None")
				);
				log.info(
					"SessionAuthenticationFilter - WebSession :: name: [{}]",
					webSession.getAttributeOrDefault("userName", "None")
				);
				log.info(
					"SessionAuthenticationFilter - WebSession :: country: [{}]",
					webSession.getAttributeOrDefault("country", "None")
				);
				log.info(
					"SessionAuthenticationFilter - WebSession :: ip: [{}]",
					webSession.getAttributeOrDefault("ip", "None")
				);
			}).then(chain.filter(exchange));
	}

	private boolean isAuthorizationHeaderEmpty(ServerHttpRequest request) {
		return request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION);
	}

	private boolean isSessionCookieExist(ServerHttpRequest request) {
		return request.getCookies().containsKey(sessionCookieProperty.getName());
	}

}
