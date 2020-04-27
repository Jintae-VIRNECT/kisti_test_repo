package com.virnect.gateway.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Gateway
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.27
 */
@Slf4j
@Component
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {
    private String errorMessage(ErrorCode error) {
        return "{\"code\":" + error.getCode() + ",\"message\":\"" + error.getMessage() + "\",\"data\":{}}";
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.warn("[GATEWAY EXCEPTION HANDLER] : " + ex);
        String message = "";
        if (ex.getClass() == NullPointerException.class) {
            message = errorMessage(ErrorCode.ERR_API_AUTHENTICATION);
        } else if (ex.getClass() == ExpiredJwtException.class) {
            message = errorMessage(ErrorCode.ERR_AUTHORIZATION_EXPIRED);
        } else if (ex.getClass() == MalformedJwtException.class || ex.getClass() == SignatureException.class || ex.getClass() == UnsupportedJwtException.class) {
            message = errorMessage(ErrorCode.ERR_API_AUTHENTICATION);
        }
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer dataBuffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Flux.just(dataBuffer));
    }
}
