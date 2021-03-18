package com.virnect.serviceserver.global.config.property;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.apache.http.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.virnect.mediaserver.cdr.CDREventName;
import com.virnect.mediaserver.config.MediaServerProperties;
import com.virnect.serviceserver.infra.utils.ConfigValidation;

@Slf4j
@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "service", ignoreInvalidFields = true)
//public class RemoteServiceProperties extends PropertyService {
public class RemoteServiceProperties {

	public final static String WS_PATH = "/remote/websocket";

	public final MediaServerProperties mediaServerProperties;

	public static String wsUrl;
	public static String wssUrl;
	//public static String httpUrl;

	@Value("#{'${spring.profiles.active:}'.length() > 0 ? '${spring.profiles.active:}'.split(',') : \"default\"}")
	protected String springProfile;

	// yml property value
	@NotNull
	private boolean dotenv;
	private String dotenvPath;

	private String certificateType;
	private String coturnCredential;
	private int coturnMysqlConnectTimeout;
	private String coturnMysqlDbname;
	private String coturnMysqlIp;
	private String coturnMysqlPassword;
	private String coturnMysqlUsername;
	private String coturnName;

	private String coturnRedisDbname;
	private String coturnRedisPassword;
	private String coturnRedisConnectTimeout;

	private List<String> coturnUrisConference;
	private List<String> coturnUrisStreaming;

	@NotEmpty
	private String domainOrPublicIp;

	private Integer httpsPort;
	private List<String> kmsUrisConference;
	private List<String> kmsUrisStreaming;
	private String policyLocation;
	private String wss;

	@NotNull
	private boolean remoteCdr;
	private String remoteCdrPath;
	private String remoteLogPath;

	@NotNull
	private boolean remoteRecording;

	@PositiveOrZero
	private Integer remoteRecordingAutostopTimeout;

	private String remoteRecordingComposedUrl;
	private String remoteRecordingCustomLayout;

	@NotNull
	private boolean remoteRecordingDebug;
	private String remoteRecordingNotification;
	private String remoteRecordingPath;

	@NotNull
	private boolean remoteRecordingPublicAccess;

	@NotEmpty
	private String remoteRecordingVersion;

	private String remoteSecret;

	@PositiveOrZero
	private Integer remoteSessionsGarbageInterval;

	@PositiveOrZero
	private Integer remoteSessionsGarbageThreshold;

	@PositiveOrZero
	private Integer remoteStreamsVideoMaxRecvBandwidth;

	@PositiveOrZero
	private Integer remoteStreamsVideoMaxSendBandwidth;

	@PositiveOrZero
	private Integer remoteStreamsVideoMinRecvBandwidth;

	@PositiveOrZero
	private Integer remoteStreamsVideoMinSendBandwidth;

	private String remoteServicePublicUrl;
	private String remoteWebsocketUrl;

	protected Environment env;

	public void setMediaServerProperties() {

		mediaServerProperties.setSpringProfile(springProfile);

		// set Media coturn property
		mediaServerProperties.coturnProperty.setCoturnUsername(coturnName);
		mediaServerProperties.coturnProperty.setCoturnCredential(coturnCredential);
		mediaServerProperties.coturnProperty.setCoturnUrisConference(coturnUrisConference);
		mediaServerProperties.coturnProperty.setCoturnUrisSteaming(coturnUrisStreaming);
		mediaServerProperties.coturnProperty.setCoturnRedisDbname(coturnRedisDbname);
		mediaServerProperties.coturnProperty.setCoturnRedisPassword(coturnRedisPassword);
		mediaServerProperties.coturnProperty.setCoturnRedisConnectTimeout(coturnRedisConnectTimeout);

		// set Media server property
		mediaServerProperties.serverProperty.setSessionsGarbageInterval(remoteSessionsGarbageInterval);
		mediaServerProperties.serverProperty.setSessionsGarbageThreshold(remoteSessionsGarbageThreshold);
		mediaServerProperties.serverProperty.setKmsUrisConference(kmsUrisConference);
		mediaServerProperties.serverProperty.setKmsUrisStreaming(kmsUrisStreaming);
		mediaServerProperties.serverProperty.setServiceSecret(remoteSecret);
		mediaServerProperties.serverProperty.setServiceCdr(remoteCdr);
		mediaServerProperties.serverProperty.setServiceCdrPath(
			remoteCdr ? ConfigValidation.asWritableFileSystemPath(remoteCdrPath) :
				ConfigValidation.asFileSystemPath(remoteCdrPath)
		);

		// set Media recording property
		mediaServerProperties.recordingProperty.setRecording(remoteRecording);
		mediaServerProperties.recordingProperty.setRecordingDebug(remoteRecordingDebug);
		mediaServerProperties.recordingProperty.setRecordingPath(
			mediaServerProperties.recordingProperty.isRecording() ?
				ConfigValidation.asWritableFileSystemPath(remoteRecordingPath) :
				ConfigValidation.asFileSystemPath(remoteRecordingPath)
		);
		mediaServerProperties.recordingProperty.setRecordingPublicAccess(remoteRecordingPublicAccess);
		mediaServerProperties.recordingProperty.setRecordingAutoStopTimeout(remoteRecordingAutostopTimeout);
		mediaServerProperties.recordingProperty.setRecordingCustomLayout(remoteRecordingCustomLayout);
		mediaServerProperties.recordingProperty.setRecordingVersion(remoteRecordingVersion);
		mediaServerProperties.recordingProperty.setRecordingComposedUrl(remoteRecordingComposedUrl);

		// set Bandwidth property
		mediaServerProperties.bandwidthProperty.setStreamsVideoMaxRecvBandwidth(remoteStreamsVideoMaxRecvBandwidth);
		mediaServerProperties.bandwidthProperty.setStreamsVideoMinRecvBandwidth(remoteStreamsVideoMinRecvBandwidth);
		mediaServerProperties.bandwidthProperty.setStreamsVideoMaxSendBandwidth(remoteStreamsVideoMaxSendBandwidth);
		mediaServerProperties.bandwidthProperty.setStreamsVideoMinSendBandwidth(remoteStreamsVideoMinSendBandwidth);
	}

	@Autowired
	public RemoteServiceProperties(Environment env, MediaServerProperties mediaServerProperties) {
		this.env = env;
		this.mediaServerProperties = mediaServerProperties;
	}

	public void checkConfigurationProperties(boolean loadDotenv) {
		checkDomainOrPublicIp();
	}

	private void checkDomainOrPublicIp() {
		final String property = "service.domain_or_public_ip";
		String domain = domainOrPublicIp;

		// TODO: remove when possible deprecated OPENVIDU_DOMAIN_OR_PUBLIC_IP
		if (domain == null || domain.isEmpty()) {
			//this.configProps.put("service.domain_or_public_ip", domain);
			//this.configProps.remove("service.domain_or_public_ip");
		}
		if (domain != null && !domain.isEmpty()) {
			this.domainOrPublicIp = domain;
			this.remoteServicePublicUrl = "https://" + domain;
			//this.remoteServicePublicwss = "wss://" + getValue("service.gateway");
			this.remoteWebsocketUrl = wss;
			if (this.httpsPort != null && this.httpsPort != 443) {
				this.remoteServicePublicUrl += (":" + this.httpsPort);
			}
			calculatePublicUrl();
		} else {
			//addError(property, "Cannot be empty");
		}
	}

	private void calculatePublicUrl() {
		final String publicUrl = this.getRemoteServicePublicUrl();
		if (publicUrl.startsWith("https://")) {
			wsUrl = publicUrl.replace("https://", "wss://");
		} else if (publicUrl.startsWith("http://")) {
			wsUrl = publicUrl.replace("http://", "wss://");
		}
		if (wsUrl.endsWith("/")) {
			wsUrl = wsUrl.substring(
				0, wsUrl.length() - 1);
		}
		String finalUrl = wsUrl.replaceFirst("wss://", "https://")
			.replaceFirst("ws://", "http://");
		log.info("calculatePublicUrl : {}", finalUrl);

		this.mediaServerProperties.setFinalUrl(finalUrl.endsWith("/") ? (finalUrl) : (finalUrl + "/"));

		wssUrl = this.remoteWebsocketUrl + WS_PATH;
		log.info("calculateWssUrl : {}", wssUrl);
	}

	@Override
	public String toString() {
		return
			"\n\t" +
			" CONFIGURATION PROPERTIES" + "\n\t" +
			" ------------------------\n" + "\n\t" +
			"* SERVICE_CERTIFICATE_TYPE=" + certificateType + "\n\t" +
			"* SERVICE_COTURN_CREDENTIAL=" + coturnCredential + "\n\t" +
			"* SERVICE_COTURN_NAME=" + coturnName + "\n\t" +
			"* SERVICE_COTURN_REDIS_CONNECT_TIMEOUT=" + coturnRedisConnectTimeout + "\n\t" +
			"* SERVICE_COTURN_REDIS_DBNAME=" + coturnRedisDbname + "\n\t" +
			"* SERVICE_COTURN_REDIS_PASSWORD=" + coturnRedisPassword + "\n\t" +
			"* SERVICE_COTURN_URIS_CONFERENCE=" + coturnUrisConference + "\n\t" +
			"* SERVICE_COTURN_URIS_STREAMING=" + coturnUrisStreaming+ "\n\t" +
			"* SERVICE_DOMAIN_OR_PUBLIC_IP=" + domainOrPublicIp + "\n\t" +
			"* SERVICE_DOTENV=" + dotenv + "\n\t" +
			"* SERVICE_HTTPS_PORT=" + httpsPort + "\n\t" +
			"* SERVICE_KMS_URIS_CONFERENCE=" + kmsUrisConference + "\n\t" +
			"* SERVICE_KMS_URIS_STREAMING=" + kmsUrisStreaming + "\n\t" +
			"* SERVICE_POLICY_LOCATION=" + policyLocation + "\n\t" +
			"* SERVICE_REMOTE_CDR=" + remoteCdr +
			"* SERVICE_REMOTE_CDR_PATH=" + remoteCdrPath + "\n\t" +
			"* SERVICE_REMOTE_RECORDING=" + remoteRecording + "\n\t" +
			"* SERVICE_REMOTE_RECORDING_AUTOSTOP_TIMEOUT=" + remoteRecordingAutostopTimeout + "\n\t" +
			"* SERVICE_REMOTE_RECORDING_COMPOSED_URL=" + remoteRecordingComposedUrl + "\n\t" +
			"* SERVICE_REMOTE_RECORDING_CUSTOM_LAYOUT=" + remoteRecordingCustomLayout + "\n\t" +
			"* SERVICE_REMOTE_RECORDING_DEBUG=" + remoteRecordingDebug + "\n\t" +
			"* SERVICE_REMOTE_RECORDING_NOTIFICATION=" + remoteRecordingNotification + "\n\t" +
			"* SERVICE_REMOTE_RECORDING_PATH=" + remoteRecordingPath + "\n\t" +
			"* SERVICE_REMOTE_RECORDING_PUBLIC_ACCESS=" + remoteRecordingPublicAccess + "\n\t" +
			"* SERVICE_REMOTE_RECORDING_VERSION=" + remoteRecordingVersion + "\n\t" +
			"* SERVICE_REMOTE_SECRET=" + remoteSecret + "\n\t" +
			"* SERVICE_REMOTE_SESSIONS_GARBAGE_INTERVAL=" + remoteSessionsGarbageInterval + "\n\t" +
			"* SERVICE_REMOTE_SESSIONS_GARBAGE_THRESHOLD=" + remoteSessionsGarbageThreshold + "\n\t" +
			"* SERVICE_REMOTE_STREAMS_VIDEO_MAX_RECV_BANDWIDTH=" + remoteStreamsVideoMaxRecvBandwidth + "\n\t" +
			"* SERVICE_REMOTE_STREAMS_VIDEO_MAX_SEND_BANDWIDTH=" + remoteStreamsVideoMaxSendBandwidth + "\n\t" +
			"* SERVICE_REMOTE_STREAMS_VIDEO_MIN_RECV_BANDWIDTH=" + remoteStreamsVideoMinRecvBandwidth + "\n\t" +
			"* SERVICE_REMOTE_STREAMS_VIDEO_MIN_SEND_BANDWIDTH=" + remoteStreamsVideoMinSendBandwidth + "\n\t" +
			"* SERVICE_WSS=" + wss + "\n";
	}
}
