package com.virnect.gateway.error;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Gateway
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.27
 */

@Component
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {
	private static final Logger logger = Loggers.getLogger(
		"com.virnect.gateway.error.GatewayExceptionHandler");

	private String errorMessage(ErrorCode error) {
		return "{\"code\":" + error.getCode() + ",\"message\":\"" + error.getMessage() + "\",\"data\":{}}";
	}

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		String message = "";

		// Gateway Security Related Exception Handling
		if (ex.getClass() == GatewaySecurityException.class) {
			logger.error("[GATEWAY EXCEPTION HANDLER][SECURITY] : {}", ex.getMessage(), ex);

			message = errorMessage(((GatewaySecurityException)ex).getErrorCode());
			ServerHttpResponse response = exchange.getResponse();
			response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			response.getHeaders().set("encrypt", "false");
			DataBuffer dataBuffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
			return response.writeWith(Mono.just(dataBuffer));
		}

		// Jwt Related Exception Handling
		if (ex.getClass() == NullPointerException.class) {
			message = errorMessage(ErrorCode.ERR_API_AUTHENTICATION);
		} else if (ex.getClass() == ExpiredJwtException.class) {
			message = errorMessage(ErrorCode.ERR_AUTHORIZATION_EXPIRED);
		} else if (ex.getClass() == MalformedJwtException.class || ex.getClass() == SignatureException.class
			|| ex.getClass() == UnsupportedJwtException.class) {
			message = errorMessage(ErrorCode.ERR_API_AUTHENTICATION);
		}
		ServerHttpResponse response = exchange.getResponse();
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		DataBuffer dataBuffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
		logger.error("[GATEWAY EXCEPTION HANDLER][TOKEN_SECURITY] : {}", ex.getMessage());
		return response.writeWith(Mono.just(dataBuffer));
	}
}
