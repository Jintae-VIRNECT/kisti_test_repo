package com.virnect.gateway.filter.websocket;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import reactor.core.publisher.Mono;

public class WebSocketProxyTraceHandler implements WebSocketHandler {
	private final Logger log = LoggerFactory.getLogger(WebSocketProxyTraceHandler.class);
	private final String REQUEST_LGO_FORMAT = "[WebSocket][Request][{}] - [{}]";
	private final String RESPONSE_LGO_FORMAT = "[WebSocket][Response][{}] - [{}]";
	private final WebSocketClient client;
	private final URI url;
	private final HttpHeaders headers;
	private final List<String> subProtocols;

	public WebSocketProxyTraceHandler(
		URI url, WebSocketClient client, HttpHeaders headers, List<String> subProtocols
	) {
		this.url = url;
		this.client = client;
		this.headers = headers;
		this.subProtocols = subProtocols;
	}

	@Override
	public Mono<Void> handle(WebSocketSession session) {
		// pass headers along so custom headers can be sent through
		return client.execute(url, this.headers, new WebSocketHandler() {
			@Override
			public Mono<Void> handle(WebSocketSession proxySession) {
				String hostInfo = session.getHandshakeInfo().getRemoteAddress().getHostString();
				// Use retain() for Reactor Netty
				Mono<Void> proxySessionSend = proxySession
					.send(session.receive()
						.doOnNext(
							message -> log.info(REQUEST_LGO_FORMAT, hostInfo, message.retain().getPayloadAsText()))
						.doOnNext(WebSocketMessage::retain)
					);

				Mono<Void> serverSessionSend = session.send(
					proxySession.receive()
						.doOnNext(
							message -> log.info(RESPONSE_LGO_FORMAT, hostInfo, message.retain().getPayloadAsText()))
						.doOnNext(WebSocketMessage::retain)
				);
				return Mono.zip(proxySessionSend, serverSessionSend).then();
			}

			/**
			 * Copy subProtocols so they are available downstream.
			 * @return
			 */
			@Override
			public List<String> getSubProtocols() {
				return WebSocketProxyTraceHandler.this.subProtocols;
			}
		});
	}

	@Override
	public List<String> getSubProtocols() {
		return this.subProtocols;
	}
}
