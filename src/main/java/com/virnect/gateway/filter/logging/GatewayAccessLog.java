package com.virnect.gateway.filter.logging;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import reactor.util.Logger;
import reactor.util.Loggers;

@Slf4j
final class GatewayAccessLog {
	static final Logger LOGGER = Loggers.getLogger("com.virnect.gateway.filter.logging.GatewayAccessLog");
	static final DateTimeFormatter DATE_TIME_FORMATTER =
		DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z", Locale.KOREA);
	static final String COMMON_LOG_FORMAT =
		"{} - {} [{}] [{}] \"{} {} [{}]\" {} {} {} ms [{}]";
	static final String MISSING = "-";
	final String zonedDateTime;
	String address = MISSING;
	CharSequence method;
	CharSequence uri;
	String protocol = MISSING;
	String user = MISSING;
	CharSequence status;
	String contentType = MISSING;
	String userAgent = MISSING;
	long contentLength;
	long startTime = System.currentTimeMillis();
	int port;

	GatewayAccessLog() {
		this.zonedDateTime = ZonedDateTime.now().format(DATE_TIME_FORMATTER);
	}

	GatewayAccessLog address(String address) {
		this.address = Objects.requireNonNull(address, "address");
		return this;
	}

	GatewayAccessLog port(int port) {
		this.port = port;
		return this;
	}

	GatewayAccessLog method(CharSequence method) {
		this.method = Objects.requireNonNull(method, "method");
		return this;
	}

	GatewayAccessLog uri(CharSequence uri) {
		this.uri = Objects.requireNonNull(uri, "uri");
		return this;
	}

	GatewayAccessLog protocol(String protocol) {
		this.protocol = Objects.requireNonNull(protocol, "protocol");
		return this;
	}

	GatewayAccessLog status(CharSequence status) {
		this.status = Objects.requireNonNull(status, "status");
		return this;
	}

	GatewayAccessLog contentLength(long contentLength) {
		this.contentLength = contentLength;
		return this;
	}

	GatewayAccessLog user(String user) {
		this.user = user;
		return this;
	}

	GatewayAccessLog contentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	GatewayAccessLog userAgent(String userAgent) {
		this.userAgent = userAgent;
		return this;
	}

	long duration() {
		return System.currentTimeMillis() - startTime;
	}

	void log() {
		LOGGER.info(COMMON_LOG_FORMAT, address, user, zonedDateTime, contentType,
			method, uri, status, (contentLength > -1 ? contentLength : MISSING), port, duration(), userAgent
		);
	}
}
