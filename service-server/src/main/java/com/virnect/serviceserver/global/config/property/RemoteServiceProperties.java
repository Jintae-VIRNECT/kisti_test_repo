package com.virnect.serviceserver.global.config.property;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import com.virnect.mediaserver.cdr.CDREventName;
import com.virnect.mediaserver.config.MediaServerProperties;
import com.virnect.mediaserver.recording.RecordingNotification;
import com.virnect.serviceserver.ServiceServerApplication;
import com.virnect.serviceserver.global.config.UrlConstants;

@Slf4j
@Component
@ConfigurationProperties(prefix = "service", ignoreInvalidFields = true)
public class RemoteServiceProperties extends PropertyService {

	protected Environment env;

	public final MediaServerProperties mediaServerProperties;

	@Autowired
	public RemoteServiceProperties(Environment env, MediaServerProperties mediaServerProperties) {
		this.env = env;
		this.mediaServerProperties = mediaServerProperties;
	}

	public RemoteServiceProperties deriveWithAdditionalPropertiesSource(Map<String, ?> propertiesSource) {
		RemoteServiceProperties prop = newRemoteServiceProperties();
		//prop.env = env;
		prop.propertiesSource = propertiesSource;
		return prop;
	}

	protected RemoteServiceProperties newRemoteServiceProperties() {
		return new RemoteServiceProperties(env, mediaServerProperties);
	}

	// Config properties
	private String servicePolicyLocation;
	private boolean isDotenvEnabled;
	private String dotenvPath;
	private String domainOrPublicIp;
	private String remoteServicePublicUrl;
	private String remoteWebsocketUrl;
	private Integer httpsPort;
	private String certificateType;

	public String getServicePolicyLocation() {
		return this.servicePolicyLocation;
	}

	public void setServicePolicyLocation() {
		servicePolicyLocation = getValue("service.policy.location");
	}

	public boolean isDotenvEnabled() {
		return isDotenvEnabled = asBoolean("service.dotenv");
	}

	public void setDotenvPath() {
		dotenvPath = getValue("service.dotenv_path");
	}

	public String getDotenvPath() {
		return dotenvPath;
	}

	public String getDomainOrPublicIp() {
		return this.domainOrPublicIp;
	}

	public String getRemoteServicePublicUrl() {
		return this.remoteServicePublicUrl;
	}

	public Integer getHttpsPort() {
		return this.httpsPort;
	}

	public String getRemoteServiceSecret() {
		return this.mediaServerProperties.serverProperty.getServiceSecret();
		//return this.remoteServiceSecret;
	}

	public boolean isCdrEnabled() {
		return this.mediaServerProperties.serverProperty.isServiceCdr();
		//return this.remoteServiceCdr;
	}

	public String getRemoteServiceCdrPath() {
		return this.mediaServerProperties.serverProperty.getServiceCdrPath();
	}

	public boolean isWebhookEnabled() {
		return this.mediaServerProperties.serverProperty.isWebhookEnabled();
	}

	public String getRemoteServiceWebhookEndpoint() {
		return this.mediaServerProperties.serverProperty.getWebhookEndpoint();
	}

	public List<Header> getRemoteServiceWebhookHeaders() {
		return this.mediaServerProperties.serverProperty.getWebhookHeadersList();
	}

	public List<CDREventName> getRemoteServiceWebhookEvents() {
		return this.mediaServerProperties.serverProperty.getWebhookEventsList();
	}

	public List<String> getKmsUrisConference() {
		return mediaServerProperties.serverProperty.getKmsUrisConference();
	}

	public List<String> getKmsUrisStreaming() {
		return mediaServerProperties.serverProperty.getKmsUrisStreaming();
	}

	public String getCoturnUsername() {
		return this.mediaServerProperties.coturnProperty.getCoturnUsername();
	}

	public String getCoturnCredential() {
		return mediaServerProperties.coturnProperty.getCoturnCredential();
	}

	public List<String> getCoturnUrisConference() {
		return mediaServerProperties.coturnProperty.getCoturnUrisConference();
	}

	public List<String> getCoturnUrisStreaming() {
		return mediaServerProperties.coturnProperty.getCoturnUrisStreaming();
	}

	public String getCoturnIp() {
		String coturnIp = mediaServerProperties.coturnProperty.getCoturnIp();
		log.info("getCoturnIp : {}", coturnIp);
		return coturnIp;
	}

	// Derived properties

	public void setFinalUrl(String finalUrlParam) {
		this.mediaServerProperties.setFinalUrl(
			finalUrlParam.endsWith("/") ? (finalUrlParam) : (finalUrlParam + "/")
		);
	}

	public String getFinalUrl() {
		return mediaServerProperties.getFinalUrl();
	}

	public void setTurnadminAvailable(boolean available) {
		this.mediaServerProperties.coturnProperty.setTurnadminAvailable(available);
	}

	public boolean isTurnadminAvailable() {
		return this.mediaServerProperties.coturnProperty.isTurnadminAvailable();
	}

	public String getCoturnDatabaseInfo() {
		return "\"ip=" + mediaServerProperties.coturnProperty.getCoturnRedisIp() +
			" dbname=" + this.mediaServerProperties.coturnProperty.getCoturnRedisDbname() +
			" password=" + this.mediaServerProperties.coturnProperty.getCoturnRedisPassword() +
			" connect_timeout=" + this.mediaServerProperties.coturnProperty.getCoturnRedisConnectTimeout() +
			"\"";
	}

	public void checkConfigurationProperties(boolean loadDotenv) {
		checkHttpsPort();
		checkDomainOrPublicIp();
		populateSpringServerPort();

		mediaServerProperties.coturnProperty.setCoturnUsername(getValue("service.coturn-name"));
		mediaServerProperties.coturnProperty.setCoturnCredential(getValue("service.coturn-credential"));
		mediaServerProperties.coturnProperty.setCoturnUrisConference(checkCoturnUris("service.coturn-uris-conference"));
		mediaServerProperties.coturnProperty.setCoturnUrisSteaming(checkCoturnUris("service.coturn-uris-streaming"));
		UrlConstants.coturnConferenceUris = new ArrayList<>(
			mediaServerProperties.coturnProperty.getCoturnUrisConference());
		UrlConstants.coturnStreamingUris = new ArrayList<>(
			mediaServerProperties.coturnProperty.getCoturnUrisStreaming());

		mediaServerProperties.coturnProperty.setCoturnRedisDbname(getValue("service.coturn-redis-dbname"));
		mediaServerProperties.coturnProperty.setCoturnRedisPassword(getValue("service.coturn-redis-password"));
		mediaServerProperties.coturnProperty.setCoturnRedisConnectTimeout(
			getValue("service.coturn-redis-connect-timeout"));

		mediaServerProperties.serverProperty.setServiceSecret(asNonEmptyString("service.remote_secret"));
		mediaServerProperties.serverProperty.setServiceCdr(asBoolean("service.remote_cdr"));
		mediaServerProperties.serverProperty.setServiceCdrPath(
			mediaServerProperties.serverProperty.isServiceCdr() ? asWritableFileSystemPath("service.remote_cdr_path") :
				asFileSystemPath("service.remote_cdr_path")
		);
		mediaServerProperties.recordingProperty.setRecording(asBoolean("service.remote_recording"));
		mediaServerProperties.recordingProperty.setRecordingDebug(asBoolean("service.remote_recording_debug"));
		mediaServerProperties.recordingProperty.setRecordingPath(
			mediaServerProperties.recordingProperty.isRecording() ?
				asWritableFileSystemPath("service.remote_recording_path") :
				asFileSystemPath("service.remote_recording_path")
		);
		mediaServerProperties.recordingProperty.setRecordingPublicAccess(
			asBoolean("service.remote_recording_public_access"));
		mediaServerProperties.recordingProperty.setRecordingAutoStopTimeout(
			asNonNegativeInteger("service.remote_recording_autostop_timeout"));
		mediaServerProperties.recordingProperty.setRecordingCustomLayout(
			asFileSystemPath("service.remote_recording_custom_layout"));
		mediaServerProperties.recordingProperty.setRecordingVersion(
			asNonEmptyString("service.remote_recording_version"));
		mediaServerProperties.recordingProperty.setRecordingComposedUrl(
			asOptionalURL("service.remote_recording_composed_url"));

		checkRemoteServiceRecordingNotification();

		mediaServerProperties.bandwidthProperty.setStreamsVideoMaxRecvBandwidth(
			asNonNegativeInteger("service.remote_streams_video_max_recv_bandwidth"));
		mediaServerProperties.bandwidthProperty.setStreamsVideoMinRecvBandwidth(
			asNonNegativeInteger("service.remote_streams_video_min_recv_bandwidth"));
		mediaServerProperties.bandwidthProperty.setStreamsVideoMaxSendBandwidth(
			asNonNegativeInteger("service.remote_streams_video_max_send_bandwidth"));
		mediaServerProperties.bandwidthProperty.setStreamsVideoMinSendBandwidth(
			asNonNegativeInteger("service.remote_streams_video_min_send_bandwidth"));

		mediaServerProperties.serverProperty.setSessionsGarbageInterval(
			asNonNegativeInteger("service.remote_sessions_garbage_interval"));
		mediaServerProperties.serverProperty.setSessionsGarbageThreshold(
			asNonNegativeInteger("service.remote_sessions_garbage_threshold"));

		mediaServerProperties.serverProperty.setKmsUrisConference(checkKmsUris("service.kms-uris-conference"));
		mediaServerProperties.serverProperty.setKmsUrisStreaming(checkKmsUris("service.kms-uris-streaming"));
		UrlConstants.mediaConferenceUris = new ArrayList<>(
			mediaServerProperties.serverProperty.getKmsUrisConference());
		UrlConstants.mediaStreamingUris = new ArrayList<>(
			mediaServerProperties.serverProperty.getKmsUrisStreaming());

		/**
		 * check later...
		 */
		//checkCoturnIp();

		//checkCoturnRedisIp();

		//checkWebhook();

		//checkCertificateType();

	}

	private void checkCertificateType() {
		String property = "service.certificate_type";
		certificateType = asNonEmptyString(property);

		if (certificateType != null && !certificateType.isEmpty()) {
			List<String> validValues = Arrays.asList("selfsigned", "owncert", "letsencrypt");
			if (!validValues.contains(certificateType)) {
				addError(property, "Invalid value '" + certificateType + "'. Valid values are " + validValues);
			}
		}
	}

	private void checkCoturnIp() {
		log.info("checkCoturnIp");
		String coturnIp = asOptionalIPv4OrIPv6("COTURN_IP");
		log.info("checkCoturnIp: {}", coturnIp);
		if (coturnIp == null || coturnIp.isEmpty()) {
			try {
				coturnIp = new URL(this.mediaServerProperties.getFinalUrl()).getHost();
				mediaServerProperties.coturnProperty.setCoturnIp(coturnIp);
			} catch (MalformedURLException e) {
				log.error("Can't get Domain name from RemoteService public Url: " + e.getMessage());
			}
		}
	}

	private void checkCoturnRedisIp() {
		log.info("checkCoturnRedisIp");
		String coturnRedisIp = asOptionalInetAddress("service.coturn_redis_ip");
		mediaServerProperties.coturnProperty.setCoturnRedisIp(coturnRedisIp);
	}

	private void checkWebhook() {
		boolean webhookEnabled = asBoolean("service.remote_webhook");
		String webhookEndpoint = asOptionalURL("service.remote_webhook_endpoint");
		mediaServerProperties.serverProperty.setWebhookEnabled(webhookEnabled);
		mediaServerProperties.serverProperty.setWebhookEndpoint(asOptionalURL("service.remote_webhook_endpoint"));
		mediaServerProperties.serverProperty.setWebhookHeadersList(checkWebhookHeaders());
		mediaServerProperties.serverProperty.setWebhookEventsList(getWebhookEvents());

		if (webhookEnabled && (webhookEndpoint == null || webhookEndpoint.isEmpty())) {
			addError(
				"remoteService_WEBHOOK_ENDPOINT", "With remoteService_WEBHOOK=true, this property cannot be empty");
		}
	}

	private void checkRemoteServiceRecordingNotification() {
		final String property = "service.remote_recording_notification";
		String recordingNotif = asNonEmptyString(property);
		try {
			mediaServerProperties.recordingProperty.setRecordingNotification(
				RecordingNotification.valueOf(recordingNotif));
		} catch (IllegalArgumentException e) {
			addError(property, "Must be one of the values " + Arrays.asList(RecordingNotification.values()));
		}
	}

	private void checkDomainOrPublicIp() {
		final String property = "service.domain_or_public_ip";
		String domain = asOptionalInetAddress(property);

		// TODO: remove when possible deprecated OPENVIDU_DOMAIN_OR_PUBLIC_IP
		if (domain == null || domain.isEmpty()) {
			domain = asOptionalInetAddress("service.domain_or_public_ip");
			this.configProps.put("service.domain_or_public_ip", domain);
			this.configProps.remove("service.domain_or_public_ip");
		}

		if (domain != null && !domain.isEmpty()) {
			this.domainOrPublicIp = domain;
			this.remoteServicePublicUrl = "https://" + domain;
			//this.remoteServicePublicwss = "wss://" + getValue("service.gateway");
			this.remoteWebsocketUrl = getValue("service.wss");
			if (this.httpsPort != null && this.httpsPort != 443) {
				this.remoteServicePublicUrl += (":" + this.httpsPort);
			}
			calculatePublicUrl();
		} else {
			addError(property, "Cannot be empty");
		}
	}

	private void checkHttpsPort() {
		String property = "service.https_port";
		String httpsPort = getValue(property);
		if (httpsPort == null) {
			addError(property, "Cannot be undefined");
		}
		int httpsPortNumber = 0;
		try {
			httpsPortNumber = Integer.parseInt(httpsPort);
		} catch (NumberFormatException e) {
			addError(property, "Is not a valid port. Must be an integer. " + e.getMessage());
			return;
		}
		if (httpsPortNumber > 0 && httpsPortNumber <= 65535) {
			this.httpsPort = httpsPortNumber;
		} else {
			addError(property, "Is not a valid port. Valid port range exceeded with value " + httpsPortNumber);
			return;
		}
	}

	/**
	 * Will add to collection of configuration properties the property "SERVER_PORT"
	 * only if property "SERVER_PORT" or "server.port" was explicitly defined. This
	 * doesn't mean this property won't have a default value if not explicitly
	 * defined (8080 is the default value given by Spring)
	 */
	private void populateSpringServerPort() {
		String springServerPort = getValue("server.port", false);
		if (springServerPort == null) {
			springServerPort = getValue("SERVER_PORT", false);
		}
		if (springServerPort != null) {
			this.configProps.put("SERVER_PORT", springServerPort);
		}
	}

	private void calculatePublicUrl() {
		final String publicUrl = this.getRemoteServicePublicUrl();
		if (publicUrl.startsWith("https://")) {
			UrlConstants.wsUrl = publicUrl.replace("https://", "wss://");
		} else if (publicUrl.startsWith("http://")) {
			UrlConstants.wsUrl = publicUrl.replace("http://", "wss://");
		}
		if (UrlConstants.wsUrl.endsWith("/")) {
			UrlConstants.wsUrl = UrlConstants.wsUrl.substring(
				0, UrlConstants.wsUrl.length() - 1);
		}
		String finalUrl = UrlConstants.wsUrl.replaceFirst("wss://", "https://")
			.replaceFirst("ws://", "http://");
		log.info("calculatePublicUrl : {}", finalUrl);
		this.setFinalUrl(finalUrl);
		UrlConstants.httpUrl = finalUrl;
		//
		UrlConstants.wssUrl = this.remoteWebsocketUrl + UrlConstants.WS_PATH;
		log.info("calculateWssUrl : {}", UrlConstants.wssUrl);
	}

	public List<String> checkCoturnUris(String property) {
		return asCoturnUris(property, getValue(property));
	}

	public List<String> asCoturnUris(String property, String CoturnUris) {

		if (CoturnUris == null || CoturnUris.isEmpty()) {
			return Arrays.asList();
		}

		CoturnUris = CoturnUris.replaceAll("\\s", ""); // Remove all white spaces
		CoturnUris = CoturnUris.replaceAll("\\\\", ""); // Remove previous escapes
		CoturnUris = CoturnUris.replaceAll("\"", ""); // Remove previous double quotes
		CoturnUris = CoturnUris.replaceFirst("^\\[", "[\\\""); // Escape first char
		CoturnUris = CoturnUris.replaceFirst("\\]$", "\\\"]"); // Escape last char
		CoturnUris = CoturnUris.replaceAll(",", "\\\",\\\""); // Escape middle uris

		List<String> coturnUrisArray = asJsonStringsArray(property);
		return coturnUrisArray;
	}

	public List<String> checkKmsUris(String property) {
		return asKmsUris(property, getValue(property));
	}

	public List<String> asKmsUris(String property, String kmsUris) {

		if (kmsUris == null || kmsUris.isEmpty()) {
			return Arrays.asList();
		}

		kmsUris = kmsUris.replaceAll("\\s", ""); // Remove all white spaces
		kmsUris = kmsUris.replaceAll("\\\\", ""); // Remove previous escapes
		kmsUris = kmsUris.replaceAll("\"", ""); // Remove previous double quotes
		kmsUris = kmsUris.replaceFirst("^\\[", "[\\\""); // Escape first char
		kmsUris = kmsUris.replaceFirst("\\]$", "\\\"]"); // Escape last char
		kmsUris = kmsUris.replaceAll(",", "\\\",\\\""); // Escape middle uris

		List<String> kmsUrisArray = asJsonStringsArray(property);
		for (String uri : kmsUrisArray) {
			try {
				this.checkWebsocketUri(uri);
			} catch (Exception e) {
				addError(property, uri + " is not a valid WebSocket URL");
			}
		}
		return kmsUrisArray;
	}

	public URI checkWebsocketUri(String uri) throws Exception {
		try {
			if (!uri.startsWith("ws://") || uri.startsWith("wss://")) {
				throw new Exception("WebSocket protocol not found");
			}
			String parsedUri = uri.replaceAll("^ws://", "http://").replaceAll("^wss://", "https://");
			return new URL(parsedUri).toURI();
		} catch (Exception e) {
			throw new RuntimeException(
				"URI '" + uri + "' has not a valid WebSocket endpoint format: " + e.getMessage());
		}
	}

	private List<Header> checkWebhookHeaders() {
		String property = "service.remote_webhook_headers";
		List<String> headers = asJsonStringsArray(property);
		List<Header> headerList = new ArrayList<>();

		for (String header : headers) {
			String[] headerSplit = header.split(": ", 2);
			if (headerSplit.length != 2) {
				addError(property, "HTTP header '" + header
					+ "' syntax is not correct. Must be 'HEADER_NAME: HEADER_VALUE'. For example: 'Authorization: Basic YWxhZGRpbjpvcGVuc2VzYW1l'");
				continue;
			}
			String headerName = headerSplit[0];
			String headerValue = headerSplit[1];
			if (headerName.isEmpty()) {
				addError(property, "HTTP header '" + header + "' syntax is not correct. Header name cannot be empty");
			}
			if (headerValue.isEmpty()) {
				addError(property, "HTTP header '" + header + "' syntax is not correct. Header value cannot be empty");
			}
			headerList.add(new BasicHeader(headerName, headerValue));
		}
		return headerList;
	}

	private List<CDREventName> getWebhookEvents() {
		String property = "service.remote_webhook_events";
		List<String> events = asJsonStringsArray(property);
		List<CDREventName> eventList = new ArrayList<>();

		for (String event : events) {
			try {
				eventList.add(CDREventName.valueOf(event));
			} catch (IllegalArgumentException e) {
				addError(property, "Event '" + event + "' does not exist");
			}
		}
		return eventList;
	}

	/*
	 * This method checks all types of Internet addresses (IPv4, IPv6 and Domains)
	 */
	protected String asOptionalInetAddress(String property) {
		String inetAddress = getValue(property);
		if (inetAddress != null && !inetAddress.isEmpty()) {
			try {
				Inet6Address.getByName(inetAddress).getHostAddress();
			} catch (UnknownHostException e) {
				addError(property, "Is not a valid Internet Address (IP or Domain Name): " + e.getMessage());
			}
		}
		return inetAddress;
	}

	protected String asOptionalURL(String property) {
		String optionalUrl = getValue(property);
		try {
			if (!optionalUrl.isEmpty()) {
				checkUrl(optionalUrl);
			}
			return optionalUrl;
		} catch (Exception e) {
			addError(property, "Is not a valid URL. " + e.getMessage());
			return null;
		}
	}

	protected void checkUrl(String url) throws Exception {
		try {
			new URL(url).toURI();
		} catch (MalformedURLException | URISyntaxException e) {
			throw new Exception("String '" + url + "' has not a valid URL format: " + e.getMessage());
		}
	}

	protected String asOptionalIPv4OrIPv6(String property) {
		String ip = getValue(property);
		if (ip != null && !ip.isEmpty()) {
			boolean isIP;
			try {
				final InetAddress inet = InetAddress.getByName(ip);
				isIP = inet instanceof Inet4Address || inet instanceof Inet6Address;
				if (isIP) {
					ip = inet.getHostAddress();
				}
			} catch (final UnknownHostException e) {
				isIP = false;
			}
			if (!isIP) {
				addError(property, "Is not a valid IP Address (IPv4 or IPv6)");
			}
		}
		return ip;
	}
}
