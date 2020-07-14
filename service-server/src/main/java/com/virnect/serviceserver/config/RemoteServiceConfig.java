/*
 * (C) Copyright 2017-2020 OpenVidu (https://openvidu.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.virnect.serviceserver.config;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.kurento.jsonrpc.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;

import com.virnect.java.client.RemoteServiceRole;
import com.virnect.serviceserver.ServiceServerApplication;
import com.virnect.serviceserver.cdr.CDREventName;
import com.virnect.serviceserver.config.Dotenv.DotenvFormatException;
import com.virnect.serviceserver.recording.RecordingNotification;

@Component
public class RemoteServiceConfig {

	public static class Error {

		private String property;
		private String value;
		private String message;

		public Error(String property, String value, String message) {
			super();
			this.property = property;
			this.value = value;
			this.message = message;
		}

		public String getProperty() {
			return property;
		}

		public String getValue() {
			return value;
		}

		public String getMessage() {
			return message;
		}

		@Override
		public String toString() {
			return "Error [property=" + property + ", value=" + value + ", message=" + message + "]";
		}
	}

	protected static final Logger log = LoggerFactory.getLogger(RemoteServiceConfig.class);

	private static final boolean SHOW_PROPERTIES_AS_ENV_VARS = true;

	private List<Error> configErrors = new ArrayList<>();

	private Map<String, String> configProps = new HashMap<>();

	private List<String> userConfigProps;

	private Map<String, ?> propertiesSource;

	@Autowired
	protected Environment env;

	@Value("#{'${spring.profiles.active:}'.length() > 0 ? '${spring.profiles.active:}'.split(',') : \"default\"}")
	protected String springProfile;

	// Config properties

	private boolean remoteServiceCdr;

	private String remoteServiceCdrPath;

	private boolean remoteServiceRecording;

	private boolean remoteServiceRecordingDebug;

	private boolean remoteServiceRecordingPublicAccess;

	private Integer remoteServiceRecordingAutostopTimeout;

	private String remoteServiceRecordingPath;

	private RecordingNotification remoteServiceRecordingNotification;

	private String remoteServiceRecordingCustomLayout;

	private String remoteServiceRecordingVersion;

	private Integer remoteServiceStreamsVideoMaxRecvBandwidth;

	private Integer remoteServiceStreamsVideoMinRecvBandwidth;

	private Integer remoteServiceStreamsVideoMaxSendBandwidth;

	private Integer remoteServiceStreamsVideoMinSendBandwidth;

	private String coturnIp;

	private String coturnRedisIp;

	private boolean remoteServiceWebhookEnabled;

	private String remoteServiceWebhookEndpoint;

	private List<Header> webhookHeadersList;

	private List<CDREventName> webhookEventsList;

	private List<String> kmsUrisList;

	private String domainOrPublicIp;

	private String remoteServicePublicUrl;

	private Integer httpsPort;

	private String remoteServiceSecret;

	private String remoteServiceRecordingComposedUrl;

	private String coturnRedisDbname;

	private String coturnRedisPassword;

	private String coturnRedisConnectTimeout;

	private String certificateType;

	protected int remoteServiceSessionsGarbageInterval;

	protected int remoteServiceSessionsGarbageThreshold;

	private String dotenvPath;

	// Derived properties

	public static String finalUrl;

	private boolean isTurnadminAvailable = false;

	// Plain config properties getters

	public String getCoturnDatabaseDbname() {
		return this.coturnRedisDbname;
	}

	public String getCoturnDatabasePassword() {
		return this.coturnRedisPassword;
	}

	public List<String> getKmsUris() {
		return kmsUrisList;
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
		return this.remoteServiceSecret;
	}

	public boolean isCdrEnabled() {
		return this.remoteServiceCdr;
	}

	public String getRemoteServiceCdrPath() {
		return this.remoteServiceCdrPath;
	}

	public boolean isRecordingModuleEnabled() {
		return this.remoteServiceRecording;
	}

	public boolean isRemoteServiceRecordingDebug() {
		return remoteServiceRecordingDebug;
	}

	public String getRemoteServiceRecordingPath() {
		return this.remoteServiceRecordingPath;
	}

	public String getRemoteServiceRemoteRecordingPath() {
		return getRemoteServiceRecordingPath();
	}

	public boolean getRemoteServiceRecordingPublicAccess() {
		return this.remoteServiceRecordingPublicAccess;
	}

	public String getRemoteServiceRecordingCustomLayout() {
		return this.remoteServiceRecordingCustomLayout;
	}

	public String getRemoteServiceRecordingVersion() {
		return this.remoteServiceRecordingVersion;
	}

	public int getRemoteServiceRecordingAutostopTimeout() {
		return this.remoteServiceRecordingAutostopTimeout;
	}

	public int getVideoMaxRecvBandwidth() {
		return this.remoteServiceStreamsVideoMaxRecvBandwidth;
	}

	public int getVideoMinRecvBandwidth() {
		return this.remoteServiceStreamsVideoMinRecvBandwidth;
	}

	public int getVideoMaxSendBandwidth() {
		return this.remoteServiceStreamsVideoMaxSendBandwidth;
	}

	public int getVideoMinSendBandwidth() {
		return this.remoteServiceStreamsVideoMinSendBandwidth;
	}

	public String getCoturnIp() {
		return this.coturnIp;
	}

	public RecordingNotification getRemoteServiceRecordingNotification() {
		return this.remoteServiceRecordingNotification;
	}

	public String getRemoteServiceRecordingComposedUrl() {
		return this.remoteServiceRecordingComposedUrl;
	}

	public boolean isWebhookEnabled() {
		return this.remoteServiceWebhookEnabled;
	}

	public String getRemoteServiceWebhookEndpoint() {
		return this.remoteServiceWebhookEndpoint;
	}

	public List<Header> getRemoteServiceWebhookHeaders() {
		return webhookHeadersList;
	}

	public List<CDREventName> getRemoteServiceWebhookEvents() {
		return webhookEventsList;
	}

	public int getSessionGarbageInterval() {
		return remoteServiceSessionsGarbageInterval;
	}

	public int getSessionGarbageThreshold() {
		return remoteServiceSessionsGarbageThreshold;
	}

	public String getDotenvPath() {
		return dotenvPath;
	}

	// Derived properties methods

	public String getSpringProfile() {
		return springProfile;
	}

	public String getFinalUrl() {
		return finalUrl;
	}

	public void setFinalUrl(String finalUrlParam) {
		finalUrl = finalUrlParam.endsWith("/") ? (finalUrlParam) : (finalUrlParam + "/");
	}

	public boolean isTurnadminAvailable() {
		return this.isTurnadminAvailable;
	}

	public void setTurnadminAvailable(boolean available) {
		this.isTurnadminAvailable = available;
	}

	public RemoteServiceRole[] getRolesFromRecordingNotification() {
		RemoteServiceRole[] roles;
		switch (this.remoteServiceRecordingNotification) {
		case none:
			roles = new RemoteServiceRole[0];
			break;
		case moderator:
			roles = new RemoteServiceRole[] { RemoteServiceRole.MODERATOR };
			break;
		case publisher_moderator:
			roles = new RemoteServiceRole[] { RemoteServiceRole.PUBLISHER, RemoteServiceRole.MODERATOR };
			break;
		case all:
			roles = new RemoteServiceRole[] { RemoteServiceRole.SUBSCRIBER, RemoteServiceRole.PUBLISHER, RemoteServiceRole.MODERATOR };
			break;
		default:
			roles = new RemoteServiceRole[] { RemoteServiceRole.PUBLISHER, RemoteServiceRole.MODERATOR };
		}
		return roles;
	}

	public boolean isRemoteServiceSecret(String secret) {
		return secret.equals(this.getRemoteServiceSecret());
	}

	public String getCoturnDatabaseString() {
		return "\"ip=" + this.coturnRedisIp + " dbname=" + this.coturnRedisDbname + " password="
				+ this.coturnRedisPassword + " connect_timeout=" + this.coturnRedisConnectTimeout + "\"";
	}

	public boolean remoteServiceRecordingCustomLayoutChanged(String path) {
		return !"/opt/remoteService/custom-layout".equals(path);
	}

	public String getRemoteServiceFrontendDefaultPath() {
		return "dashboard";
	}

	// Properties management methods

	public RemoteServiceConfig deriveWithAdditionalPropertiesSource(Map<String, ?> propertiesSource) {
		RemoteServiceConfig config = newRemoteServiceConfig();
		config.propertiesSource = propertiesSource;
		config.env = env;
		return config;
	}

	protected RemoteServiceConfig newRemoteServiceConfig() {
		return new RemoteServiceConfig();
	}

	public List<Error> getConfigErrors() {
		return configErrors;
	}

	public Map<String, String> getConfigProps() {
		return configProps;
	}

	public List<String> getUserProperties() {
		return userConfigProps;
	}

	private String getValue(String property) {
		return this.getValue(property, true);
	}

	private String getValue(String property, boolean storeInConfigProps) {
		String value = null;
		if (propertiesSource != null) {
			Object valueObj = propertiesSource.get(property);
			if (valueObj != null) {
				value = valueObj.toString();
			}
		}
		if (value == null) {
			value = env.getProperty(property);
		}
		if (storeInConfigProps) {
			this.configProps.put(property, value);
		}
		return value;
	}

	public String getPropertyName(String propertyName) {
		if (SHOW_PROPERTIES_AS_ENV_VARS) {
			return propertyName.replace('.', '_').replace('-', '_').toUpperCase();
		} else {
			return propertyName;
		}
	}

	protected void addError(String property, String msg) {

		String value = null;

		if (property != null) {
			value = getValue(property);
		}

		this.configErrors.add(new Error(property, value, msg));
	}

	public void checkConfiguration(boolean loadDotenv) {
		try {
			this.checkConfigurationProperties(loadDotenv);
		} catch (Exception e) {
			log.error("Exception checking configuration", e);
			addError(null, "Exception checking configuration." + e.getClass().getName() + ":" + e.getMessage());
		}
		userConfigProps = new ArrayList<>(configProps.keySet());
		userConfigProps.removeAll(getNonUserProperties());
	}

	@PostConstruct
	public void checkConfiguration() {
		this.checkConfiguration(true);
	}

	protected List<String> getNonUserProperties() {
		return Arrays.asList("server.port", "SERVER_PORT", "DOTENV_PATH", "COTURN_IP", "COTURN_REDIS_IP",
				"COTURN_REDIS_DBNAME", "COTURN_REDIS_PASSWORD", "COTURN_REDIS_CONNECT_TIMEOUT");
	}

	// Properties

	protected void checkConfigurationProperties(boolean loadDotenv) {

		if (loadDotenv) {
			dotenvPath = getValue("DOTENV_PATH");
			this.populatePropertySourceFromDotenv();
		}

		checkHttpsPort();
		checkDomainOrPublicIp();
		populateSpringServerPort();

		coturnRedisDbname = getValue("service.coturn_redis_dbname");
		coturnRedisPassword = getValue("service.coturn_redis_password");
		coturnRedisConnectTimeout = getValue("service.coturn_redis_connect_timeout");

		remoteServiceSecret = asNonEmptyString("service.remote_secret");
		remoteServiceCdr = asBoolean("service.remote_cdr");
		remoteServiceCdrPath = remoteServiceCdr ? asWritableFileSystemPath("service.remote_cdr_path") : asFileSystemPath("service.remote_cdr_path");
		remoteServiceRecording = asBoolean("service.remote_recording");
		remoteServiceRecordingDebug = asBoolean("service.remote_recording_debug");
		remoteServiceRecordingPath = remoteServiceRecording ? asWritableFileSystemPath("service.remote_recording_path") : asFileSystemPath("service.remote_recording_path");
		remoteServiceRecordingPublicAccess = asBoolean("service.remote_recording_public_access");
		remoteServiceRecordingAutostopTimeout = asNonNegativeInteger("service.remote_recording_autostop_timeout");
		remoteServiceRecordingCustomLayout = asFileSystemPath("service.remote_recording_custom_layout");
		remoteServiceRecordingVersion = asNonEmptyString("service.remote_recording_version");
		remoteServiceRecordingComposedUrl = asOptionalURL("service.remote_recording_composed_url");
		checkRemoteServiceRecordingNotification();

		remoteServiceStreamsVideoMaxRecvBandwidth = asNonNegativeInteger("service.remote_streams_video_max_recv_bandwidth");
		remoteServiceStreamsVideoMinRecvBandwidth = asNonNegativeInteger("service.remote_streams_video_min_recv_bandwidth");

		remoteServiceStreamsVideoMaxSendBandwidth = asNonNegativeInteger("service.remote_streams_video_max_send_bandwidth");
		remoteServiceStreamsVideoMinSendBandwidth = asNonNegativeInteger("service.remote_streams_video_min_send_bandwidth");


		remoteServiceSessionsGarbageInterval = asNonNegativeInteger("service.remote_sessions_garbage_interval");
		remoteServiceSessionsGarbageThreshold = asNonNegativeInteger("service.remote_sessions_garbage_threshold");

		kmsUrisList = checkKmsUris();

		/**
		 * check later...
		 */
		checkCoturnIp();

		coturnRedisIp = asOptionalInetAddress("service.coturn_redis_ip");

		checkWebhook();

		checkCertificateType();

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
		String property = "COTURN_IP";
		coturnIp = asOptionalIPv4OrIPv6(property);

		if (coturnIp == null || this.coturnIp.isEmpty()) {
			try {
				this.coturnIp = new URL(this.getFinalUrl()).getHost();
			} catch (MalformedURLException e) {
				log.error("Can't get Domain name from RemoteService public Url: " + e.getMessage());
			}
		}
	}

	private void checkWebhook() {
		remoteServiceWebhookEnabled = asBoolean("service.remote_webhook");
		remoteServiceWebhookEndpoint = asOptionalURL("service.remote_webhook_endpoint");
		webhookHeadersList = checkWebhookHeaders();
		webhookEventsList = getWebhookEvents();

		if (remoteServiceWebhookEnabled && (remoteServiceWebhookEndpoint == null || remoteServiceWebhookEndpoint.isEmpty())) {
			addError("remoteService_WEBHOOK_ENDPOINT", "With remoteService_WEBHOOK=true, this property cannot be empty");
		}
	}

	private void checkRemoteServiceRecordingNotification() {
		String recordingNotif = asNonEmptyString("service.remote_recording_notification");
		try {
			remoteServiceRecordingNotification = RecordingNotification.valueOf(recordingNotif);
		} catch (IllegalArgumentException e) {
			//addError("OPENVIDU_RECORDING_NOTIFICATION", "Must be one of the values " + Arrays.asList(RecordingNotification.values()));
			addError("service.remote_recording_notification", "Must be one of the values " + Arrays.asList(RecordingNotification.values()));
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
			ServiceServerApplication.wsUrl = publicUrl.replace("https://", "wss://");
		} else if (publicUrl.startsWith("http://")) {
			ServiceServerApplication.wsUrl = publicUrl.replace("http://", "wss://");
		}
		if (ServiceServerApplication.wsUrl.endsWith("/")) {
			ServiceServerApplication.wsUrl = ServiceServerApplication.wsUrl.substring(0, ServiceServerApplication.wsUrl.length() - 1);
		}
		String finalUrl = ServiceServerApplication.wsUrl.replaceFirst("wss://", "https://").replaceFirst("ws://", "http://");
		this.setFinalUrl(finalUrl);
		ServiceServerApplication.httpUrl = this.getFinalUrl();
	}

	public List<String> checkKmsUris() {

		String property = "service.kms_uris";

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

	// -------------------------------------------------------
	// Format Checkers
	// -------------------------------------------------------

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

	protected String asNonEmptyString(String property) {
		String stringValue = getValue(property);
		if (stringValue != null && !stringValue.isEmpty()) {
			return stringValue;
		} else {
			addError(property, "Cannot be empty.");
			return null;
		}
	}

	protected String asOptionalString(String property) {
		return getValue(property);
	}

	protected boolean asBoolean(String property) {
		String value = getValue(property);
		if (value == null) {
			addError(property, "Cannot be empty");
			return false;
		}

		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
			return Boolean.parseBoolean(value);
		} else {
			addError(property, "Is not a boolean (true or false)");
			return false;
		}
	}

	protected Integer asNonNegativeInteger(String property) {
		try {
			Integer integerValue = Integer.parseInt(getValue(property));

			if (integerValue < 0) {
				addError(property, "Is not a non negative integer");
			}
			return integerValue;
		} catch (NumberFormatException e) {
			addError(property, "Is not a non negative integer");
			return 0;
		}
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

	protected String asFileSystemPath(String property) {
		try {
			String stringPath = this.asNonEmptyString(property);
			Paths.get(stringPath);
			File f = new File(stringPath);
			f.getCanonicalPath();
			f.toURI().toString();
			stringPath = stringPath.endsWith("/") ? stringPath : (stringPath + "/");
			return stringPath;
		} catch (Exception e) {
			addError(property, "Is not a valid file system path. " + e.getMessage());
			return null;
		}
	}

	protected String asWritableFileSystemPath(String property) {
		try {
			String stringPath = this.asNonEmptyString(property);
			Paths.get(stringPath);
			File f = new File(stringPath);
			f.getCanonicalPath();
			f.toURI().toString();
			if (!f.exists()) {
				if (!f.mkdirs()) {
					throw new Exception("The path does not exist and RemoteService Server does not have enough permissions to create it");
				}
			}
			if (!f.canWrite()) {
				throw new Exception("RemoteService Server does not have permissions to write on path " + f.getCanonicalPath());
			}
			stringPath = stringPath.endsWith("/") ? stringPath : (stringPath + "/");
			return stringPath;
		} catch (Exception e) {
			addError(property, "Is not a valid writable file system path. " + e.getMessage());
			return null;
		}
	}

	protected List<String> asJsonStringsArray(String property) {
		try {
			Gson gson = new Gson();
			JsonArray jsonArray = gson.fromJson(getValue(property), JsonArray.class);
			List<String> list = JsonUtils.toStringList(jsonArray);
			if (list.size() == 1 && list.get(0).isEmpty()) {
				list = new ArrayList<>();
			}
			return list;
		} catch (JsonSyntaxException e) {
			addError(property, "Is not a valid strings array in JSON format. " + e.getMessage());
			return Arrays.asList();
		}
	}

	protected <E extends Enum<E>> E asEnumValue(String property, Class<E> enumType) {
		String value = this.getValue(property);
		try {
			return Enum.valueOf(enumType, value);
		} catch (IllegalArgumentException e) {
			addError(property, "Must be one of " + Arrays.asList(enumType.getEnumConstants()));
			return null;
		}
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

	protected void checkUrl(String url) throws Exception {
		try {
			new URL(url).toURI();
		} catch (MalformedURLException | URISyntaxException e) {
			throw new Exception("String '" + url + "' has not a valid URL format: " + e.getMessage());
		}
	}

	protected void populatePropertySourceFromDotenv() {
		File dotenvFile = this.getDotenvFile();
		if (dotenvFile != null) {
			if (dotenvFile.canRead()) {
				Dotenv dotenv = new Dotenv();
				try {
					dotenv.read(dotenvFile.toPath());
					this.propertiesSource = dotenv.getAll();
					log.info("Configuration properties read from file {}", dotenvFile.getAbsolutePath());
				} catch (IOException | DotenvFormatException e) {
					log.error("Error reading properties from .env file: {}", e.getMessage());
					addError(null, e.getMessage());
				}
			} else {
				log.error("RemoteService does not have read permissions over .env file at {}", this.getDotenvPath());
			}
		}
	}

	public Path getDotenvFilePathFromDotenvPath(String dotenvPathProperty) {
		if (dotenvPathProperty.endsWith(".env")) {
			// Is file
			return Paths.get(dotenvPathProperty);
		} else if (dotenvPathProperty.endsWith("/")) {
			// Is folder
			return Paths.get(dotenvPathProperty + ".env");
		} else {
			// Is a folder not ending in "/"
			return Paths.get(dotenvPathProperty + "/.env");
		}
	}

	public File getDotenvFile() {
		if (getDotenvPath() != null && !getDotenvPath().isEmpty()) {

			Path path = getDotenvFilePathFromDotenvPath(getDotenvPath());
			String normalizePath = FilenameUtils.normalize(path.toAbsolutePath().toString());
			File file = new File(normalizePath);

			if (file.exists()) {
				return file;
			} else {
				log.error(".env file not found at {}", file.getAbsolutePath().toString());
			}

		} else {
			log.warn("DOTENV_PATH configuration property is not defined");
		}
		return null;
	}

}
