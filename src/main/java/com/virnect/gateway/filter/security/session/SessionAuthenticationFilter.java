package com.virnect.gateway.filter.security.session;

import java.util.Base64;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import com.virnect.gateway.error.ErrorCode;
import com.virnect.gateway.filter.security.GatewayServerAuthenticationException;
import com.virnect.gateway.filter.security.RequestValidationProcessor;
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
		log.info("[Session Authentication Filter] - Active.");
		log.info("[Session Authentication Filter] - Session Cookie Properties :: [{}].", sessionCookieProperty);
		this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}

	@Override
	public Mono<Void> filter(
		ServerWebExchange exchange,
		GatewayFilterChain chain
	) {
		log.info("SessionAuthenticationFilter - doFilter");

		if (RequestValidationProcessor.isRequestAuthenticationProcessSkip(exchange.getRequest())) {
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

		return showUserSessionInfoAndDoFilter(exchange, chain, exchange.getRequest().getURI().getPath());
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
}
