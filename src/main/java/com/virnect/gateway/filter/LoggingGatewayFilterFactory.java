package com.virnect.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Gateway
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.28
 */

@Slf4j
@Component
public class LoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<LoggingGatewayFilterFactory.Config> {
    public LoggingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            StopWatch stopWatch = new StopWatch();

            // pre logging
            if (config.isPreLogger()) {
                stopWatch.start();
                String uri = request.getURI().toString();
                Optional<String> forwardHeaders = Optional.ofNullable(request.getHeaders().get("X-Forwarded-For").get(0));
                String clientIp = forwardHeaders.orElse(request.getRemoteAddress().getAddress().getHostAddress());

                log.info("-----------------------------------------------------------------------------------------------------------------------------------------");
                log.info("[REQUEST] [{}] [{}] [{}] {}", LocalDateTime.now(), clientIp, request.getMethodValue() + " " + uri, request.getHeaders().get("Content-Type"));
                request.getHeaders().entrySet().forEach((entry -> log.info("[HEADER] [{}] => {} ", entry.getKey(), Arrays.toString(entry.getValue().toArray()))));
                log.info("-----------------------------------------------------------------------------------------------------------------------------------------");
            }
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                stopWatch.stop();
                if (config.isPostLogger()) {
                    String uri = request.getURI().toString();
                    String clientIp = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
                    log.info("-----------------------------------------------------------------------------------------------------------------------------------------");
                    log.info("[RESPONSE] [{}] [{}] [{}] [{}] {} [{} ms]", LocalDateTime.now(), clientIp, request.getMethodValue() + " " + uri, String.format("%d %s", response.getRawStatusCode(), HttpStatus.valueOf(response.getRawStatusCode()).name()), response.getHeaders().get("Content-Type"), stopWatch.getTotalTimeMillis());
                    log.info("-----------------------------------------------------------------------------------------------------------------------------------------");
                }
            }));
        };
    }

    public static class Config {
        private boolean preLogger;
        private boolean postLogger;

        public Config(boolean preLogger, boolean postLogger) {
            this.preLogger = preLogger;
            this.postLogger = postLogger;
        }

        public boolean isPreLogger() {
            return preLogger;
        }

        public void setPreLogger(boolean preLogger) {
            this.preLogger = preLogger;
        }

        public boolean isPostLogger() {
            return postLogger;
        }

        public void setPostLogger(boolean postLogger) {
            this.postLogger = postLogger;
        }
    }
}
