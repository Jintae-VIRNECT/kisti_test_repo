package com.virnect.gateway.filter.security.session;

import java.util.Base64;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import com.virnect.gateway.error.ErrorCode;
import com.virnect.gateway.filter.security.GatewayServerAuthenticationException;
import com.virnect.gateway.filter.security.RequestValidationProcessor;
import com.virnect.security.UserAuthenticationDetails;
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
		log.info("[Session Authentication Filter] => Active");
		log.info("[Session Authentication Filter] => Session Cookie Properties :: [{}]", sessionCookieProperty);
		this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	@Override
	public Mono<Void> filter(
		ServerWebExchange exchange,
		GatewayFilterChain chain
	) {
		if (RequestValidationProcessor.process(exchange.getRequest())) {
			return chain.filter(exchange);
		}

		// if develop and onpremise environment and request is swagger ui related.
		if ((activeProfile.equals("develop") || activeProfile.equals("onpremise")) &&
			RequestValidationProcessor.isSwaggerApiDocsUrl(exchange.getRequest())
		) {
			log.info("SessionAuthenticationFilter - Skip swagger json request like [/v2/api-docs] ");
			return chain.filter(exchange);
		}

		// If request not contain session cookie but have authorization header.
		// It is already authorized jwt authentication filter.
		if (!RequestValidationProcessor.isSessionAuthenticationRequest(exchange.getRequest()) &&
			RequestValidationProcessor.isJwtAuthenticationRequest(exchange.getRequest())) {
			log.info("Already authorized request by JwtAuthenticationFilter");
			return chain.filter(exchange);
		}

		// Check Request Authentication Type
		if (!RequestValidationProcessor.isSessionAuthenticationRequest(exchange.getRequest())) {
			log.error("SessionAuthenticationFilter - Can't find session cookie On Request.");
			throw new GatewayServerAuthenticationException(ErrorCode.ERR_API_AUTHENTICATION);
		}

		return handleSessionRequest(exchange, chain, exchange.getRequest().getURI().getPath());
	}

	private Mono<Void> handleSessionRequest(
		ServerWebExchange exchange, GatewayFilterChain chain, String requestUrlPath
	) {
		HttpHeaders headers = exchange.getRequest().getHeaders();

		for (String key : headers.keySet()) {
			if (key.startsWith("client-")) {
				log.info("SessionAuthenticationFilter - [Header][{}]:: [{}]", key, headers.get(key));
			}
		}

		return exchange.getSession()
			.doOnError(e -> {
				log.info("SessionAuthenticationFilter - Can't not resolve WebSession From Request");
				throw new SessionNotFoundException(requestUrlPath);
			})
			.doOnNext(this::userSessionInformationResolver).then(chain.filter(exchange));
	}

	private void userSessionInformationResolver(WebSession webSession) {
		SecurityContext securityContext = webSession.getAttribute("SPRING_SECURITY_CONTEXT");
		if (securityContext != null) {
			UserDetailsImpl userDetails = (UserDetailsImpl)securityContext.getAuthentication()
				.getPrincipal();
			UserAuthenticationDetails authenticationDetails = (UserAuthenticationDetails)securityContext.getAuthentication()
				.getDetails();
			log.info(
				"SessionAuthenticationFilter - Security Context User Authentication  Details: {}",
				authenticationDetails
			);
			log.info("SessionAuthenticationFilter - Security Context Principal User Details: {}", userDetails);
		}
		log.info("SessionAuthenticationFilter - WebSession :: ID: [{}]", webSession.getId());
		log.info(
			"SessionAuthenticationFilter - WebSession :: uuid: [{}] email: [{}]",
			webSession.getAttributeOrDefault("userUUID", "None"),
			webSession.getAttributeOrDefault("userEmail", "None")
		);
		log.debug(
			"SessionAuthenticationFilter - WebSession :: name: [{}]",
			webSession.getAttributeOrDefault("userName", "None")
		);
		log.debug(
			"SessionAuthenticationFilter - WebSession :: country: [{}]",
			webSession.getAttributeOrDefault("country", "None")
		);
		log.debug(
			"SessionAuthenticationFilter - WebSession :: ip: [{}]",
			webSession.getAttributeOrDefault("ip", "None")
		);
	}
}
