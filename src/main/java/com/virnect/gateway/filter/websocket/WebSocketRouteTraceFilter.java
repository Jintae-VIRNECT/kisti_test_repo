package com.virnect.gateway.filter.websocket;

import static org.springframework.cloud.gateway.filter.headers.HttpHeadersFilter.*;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;
import static org.springframework.util.StringUtils.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.headers.HttpHeadersFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Mono;

@Component
public class WebSocketRouteTraceFilter implements GlobalFilter, Ordered {
	/**
	 * Sec-Websocket protocol.
	 */
	public static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";

	private static final Log log = LogFactory.getLog(WebSocketRouteTraceFilter.class);

	private final WebSocketClient webSocketClient;

	private final WebSocketService webSocketService;

	private final ObjectProvider<List<HttpHeadersFilter>> headersFiltersProvider;

	// do not use this headersFilters directly, use getHeadersFilters() instead.
	private volatile List<HttpHeadersFilter> headersFilters;

	public WebSocketRouteTraceFilter(
		WebSocketClient webSocketClient, WebSocketService webSocketService,
		ObjectProvider<List<HttpHeadersFilter>> headersFiltersProvider
	) {
		this.webSocketClient = webSocketClient;
		this.webSocketService = webSocketService;
		this.headersFiltersProvider = headersFiltersProvider;
	}

	/* for testing */
	static String convertHttpToWs(String scheme) {
		scheme = scheme.toLowerCase();
		return "http".equals(scheme) ? "ws" : "https".equals(scheme) ? "wss" : scheme;
	}

	static void changeSchemeIfIsWebSocketUpgrade(ServerWebExchange exchange) {
		// Check the Upgrade
		URI requestUrl = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);
		String scheme = requestUrl.getScheme().toLowerCase();
		String upgrade = exchange.getRequest().getHeaders().getUpgrade();
		// change the scheme if the socket client send a "http" or "https"
		if ("WebSocket".equalsIgnoreCase(upgrade)
			&& ("http".equals(scheme) || "https".equals(scheme))) {
			String wsScheme = convertHttpToWs(scheme);
			boolean encoded = containsEncodedParts(requestUrl);
			URI wsRequestUrl = UriComponentsBuilder.fromUri(requestUrl).scheme(wsScheme)
				.build(encoded).toUri();
			exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, wsRequestUrl);
			if (log.isTraceEnabled()) {
				log.trace("changeSchemeTo:[" + wsRequestUrl + "]");
			}
		}
	}

	@Override
	public Mono<Void> filter(
		ServerWebExchange exchange,
		GatewayFilterChain chain
	) {
		changeSchemeIfIsWebSocketUpgrade(exchange);

		URI requestUrl = exchange.getRequiredAttribute(GATEWAY_REQUEST_URL_ATTR);
		String scheme = requestUrl.getScheme();

		if (isAlreadyRouted(exchange)
			|| (!"ws".equals(scheme) && !"wss".equals(scheme))) {
			return chain.filter(exchange);
		}
		setAlreadyRouted(exchange);

		HttpHeaders headers = exchange.getRequest().getHeaders();
		HttpHeaders filtered = filterRequest(getHeadersFilters(), exchange);

		List<String> protocols = headers.get(SEC_WEBSOCKET_PROTOCOL);
		if (protocols != null) {
			protocols = headers.get(SEC_WEBSOCKET_PROTOCOL).stream().flatMap(
				header -> Arrays.stream(commaDelimitedListToStringArray(header)))
				.map(String::trim).collect(Collectors.toList());
		}

		return this.webSocketService.handleRequest(exchange, new WebSocketProxyTraceHandler(
			requestUrl, this.webSocketClient, filtered, protocols));
	}

	@Override
	public int getOrder() {
		// Before WebSocketRoutingFilter since this routes certain http requests
		return Ordered.LOWEST_PRECEDENCE - 2;
	}

	/* for testing */
	List<HttpHeadersFilter> getHeadersFilters() {
		if (this.headersFilters == null) {
			this.headersFilters = this.headersFiltersProvider
				.getIfAvailable(ArrayList::new);

			// remove host header unless specifically asked not to
			headersFilters.add((headers, exchange) -> {
				HttpHeaders filtered = new HttpHeaders();
				filtered.addAll(headers);
				filtered.remove(HttpHeaders.HOST);
				boolean preserveHost = exchange
					.getAttributeOrDefault(PRESERVE_HOST_HEADER_ATTRIBUTE, false);
				if (preserveHost) {
					String host = exchange.getRequest().getHeaders()
						.getFirst(HttpHeaders.HOST);
					filtered.add(HttpHeaders.HOST, host);
				}
				return filtered;
			});

			headersFilters.add((headers, exchange) -> {
				HttpHeaders filtered = new HttpHeaders();
				headers.entrySet().stream()
					.filter(entry -> !entry.getKey().toLowerCase()
						.startsWith("sec-websocket"))
					.forEach(header -> filtered.addAll(
						header.getKey(),
						header.getValue()
					));
				return filtered;
			});
		}

		return this.headersFilters;
	}
}
