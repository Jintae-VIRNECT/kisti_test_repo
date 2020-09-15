package com.virnect.gateway.filter.logging;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

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
		return new OrderedGatewayFilter(((exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			ServerHttpResponse response = exchange.getResponse();
			StopWatch stopWatch = new StopWatch();

			// pre logging
			if (config.isPreLogger()) {
				stopWatch.start();
				String uri = request.getURI().toString();
				String clientIp = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostName();
				log.info(
					"[{}] [REQUEST] [{}] [{}] [{}] {}", config.messagePrefix, LocalDateTime.now(), clientIp,
					request.getMethodValue() + " " + uri, request.getHeaders().get("Content-Type")
				);
				log.info("[{}] [HEADER] {}", config.getMessagePrefix(), request.getHeaders().toString());
			}
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				stopWatch.stop();
				if (config.isPostLogger()) {
					String uri = request.getURI().toString();
					String clientIp = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
					log.info(
						"[{}] [RESPONSE] [{}] [{}] [{}] [{}] {} [{} ms]", config.messagePrefix, LocalDateTime.now(),
						clientIp, request.getMethodValue() + " " + uri,
						String.format("%d %s", response.getRawStatusCode(),
							HttpStatus.valueOf(response.getRawStatusCode()).name()
						), response.getHeaders().get("Content-Type"), stopWatch.getTotalTimeMillis()
					);
				}
			}));
		}),-3);
	}

	public static class Config {
		private String messagePrefix;
		private boolean preLogger;
		private boolean postLogger;

		public Config(String messagePrefix, boolean preLogger, boolean postLogger) {
			if (messagePrefix == null) {
				this.messagePrefix = "LOGGING_FILTER";
			} else {
				this.messagePrefix = messagePrefix;
			}
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

		public String getMessagePrefix() {
			return messagePrefix;
		}

		public void setMessagePrefix(String messagePrefix) {
			this.messagePrefix = messagePrefix;
		}
	}
}
