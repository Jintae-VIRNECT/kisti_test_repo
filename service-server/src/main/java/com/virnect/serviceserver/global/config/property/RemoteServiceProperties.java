package com.virnect.serviceserver.global.config.property;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.virnect.mediaserver.config.MediaServerProperties;
import com.virnect.serviceserver.infra.utils.ConfigValidation;

@Slf4j
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "service", ignoreInvalidFields = true)
public class RemoteServiceProperties extends PropertyService {
//public class RemoteServiceProperties {

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

	//@NotEmpty
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

	@Autowired
	public RemoteServiceProperties(Environment env, MediaServerProperties mediaServerProperties) {
		this.env = env;
		this.mediaServerProperties = mediaServerProperties;
	}

	public void setMediaServerProperties() {

		mediaServerProperties.setSpringProfile(springProfile);

		// set Media coturn property
		mediaServerProperties.coturnProperty.setCoturnUsername(
			getValue("service.coturn-name", coturnName).toString());
		mediaServerProperties.coturnProperty.setCoturnCredential(
			getValue("service.coturn-credential", coturnName).toString());
		mediaServerProperties.coturnProperty.setCoturnUrisConference(
			(List<String>)getValue("service.coturn-uris-conference", coturnUrisConference));
		mediaServerProperties.coturnProperty.setCoturnUrisSteaming(
			(List<String>)getValue("service.coturn-uris-streaming", coturnUrisStreaming));

		/*mediaServerProperties.coturnProperty.setCoturnRedisDbname(
			getValue("service.coturn-redis-dbname",coturnRedisDbname).toString());
		mediaServerProperties.coturnProperty.setCoturnRedisPassword(
			getValue("service.coturn-redis-password",coturnRedisPassword).toString());
		mediaServerProperties.coturnProperty.setCoturnRedisConnectTimeout(
			getValue("service.coturn-redis-connect-timeout",coturnRedisConnectTimeout).toString());*/

		// set Media server property
		mediaServerProperties.serverProperty.setSessionsGarbageInterval(
			(Integer)getValue("service.remote_sessions_garbage_interval",remoteSessionsGarbageInterval));
		mediaServerProperties.serverProperty.setSessionsGarbageThreshold(
			(Integer)getValue("service.remote_sessions_garbage_threshold",remoteSessionsGarbageThreshold));
		mediaServerProperties.serverProperty.setKmsUrisConference(
			(List<String>)getValue("service.kms-uris-conference", kmsUrisConference));
		mediaServerProperties.serverProperty.setKmsUrisStreaming(
			(List<String>)getValue("service.kms-uris-streaming", kmsUrisStreaming));
		mediaServerProperties.serverProperty.setServiceSecret(
			getValue("service.remote_secret", remoteSecret).toString());
		mediaServerProperties.serverProperty.setServiceCdr(
			(Boolean)getValue("service.remote_cdr", remoteCdr));
		mediaServerProperties.serverProperty.setServiceCdrPath(
			(Boolean)getValue("service.remote_cdr", remoteCdr)
				? ConfigValidation.asWritableFileSystemPath(getValue("service.remote_cdr_path",remoteCdrPath).toString())
				: ConfigValidation.asFileSystemPath(getValue("service.remote_cdr_path",remoteCdrPath).toString())
		);

		// set Media recording property
		mediaServerProperties.recordingProperty.setRecording(
			(Boolean)getValue("service.remote_recording_debug",remoteRecording));
		mediaServerProperties.recordingProperty.setRecordingDebug(
			(Boolean)getValue("service.remote_recording_debug",remoteRecordingDebug));
		mediaServerProperties.recordingProperty.setRecordingPath(
			mediaServerProperties.recordingProperty.isRecording() ?
				ConfigValidation.asWritableFileSystemPath(getValue("service.remote_cdr_path", remoteRecordingPath).toString()) :
				ConfigValidation.asFileSystemPath(getValue("service.remote_cdr_path", remoteRecordingPath).toString())
		);
		mediaServerProperties.recordingProperty.setRecordingPublicAccess(
			(Boolean)getValue("service.remote_recording_public_access", remoteRecordingPublicAccess));
		mediaServerProperties.recordingProperty.setRecordingAutoStopTimeout(
			(Integer)getValue("service.remote_recording_autostop_timeout", remoteRecordingAutostopTimeout));
		mediaServerProperties.recordingProperty.setRecordingCustomLayout(
			getValue("service.remote_recording_custom_layout", remoteRecordingCustomLayout).toString());
		mediaServerProperties.recordingProperty.setRecordingVersion(
			getValue("service.remote_recording_version", remoteRecordingVersion).toString());
		mediaServerProperties.recordingProperty.setRecordingComposedUrl(
			getValue("service.remote_recording_composed_url",remoteRecordingComposedUrl).toString());

		// set Bandwidth property
		mediaServerProperties.bandwidthProperty.setStreamsVideoMaxRecvBandwidth(
			(Integer)getValue("service.remote_streams_video_max_recv_bandwidth", remoteStreamsVideoMaxRecvBandwidth));
		mediaServerProperties.bandwidthProperty.setStreamsVideoMinRecvBandwidth(
			(Integer)getValue("service.remote_streams_video_min_recv_bandwidth", remoteStreamsVideoMinRecvBandwidth));
		mediaServerProperties.bandwidthProperty.setStreamsVideoMaxSendBandwidth(
			(Integer)getValue("service.remote_streams_video_max_send_bandwidth", remoteStreamsVideoMaxSendBandwidth));
		mediaServerProperties.bandwidthProperty.setStreamsVideoMinSendBandwidth(
			(Integer)getValue("service.remote_streams_video_min_send_bandwidth", remoteStreamsVideoMinSendBandwidth));
	}

	public void checkProperties() {
		checkDomainOrPublicIp(domainOrPublicIp);
		checkPublicUrl(domainOrPublicIp);
	}

	private void checkDomainOrPublicIp(String domain) {
		if (domain != null && !domain.isEmpty()) {
			this.remoteServicePublicUrl = "https://" + domainOrPublicIp;
			this.remoteWebsocketUrl = wss;
			if (this.httpsPort != null && this.httpsPort != 443) {
				this.remoteServicePublicUrl += (":" + this.httpsPort);
			}
		}
	}

	private void checkPublicUrl(String domain) {
		if (domain != null && !domain.isEmpty()) {
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
			String finalUrl =
				wsUrl.replaceFirst("wss://", "https://")
					.replaceFirst("ws://", "http://");

			log.info("calculatePublicUrl : {}", finalUrl);

			this.mediaServerProperties.setFinalUrl(finalUrl.endsWith("/") ? (finalUrl) : (finalUrl + "/"));
			wssUrl = this.remoteWebsocketUrl;
			log.info("calculateWssUrl : {}", wssUrl);
		}
	}

	public boolean isTurnadminAvailable() {
		return this.mediaServerProperties.coturnProperty.isTurnadminAvailable();
	}

	public String getCoturnIp() {
		String coturnIp = mediaServerProperties.coturnProperty.getCoturnIp();
		log.info("getCoturnIp : {}", coturnIp);
		return coturnIp;
	}

	@Override
	public String toString() {
		return
			"\n\t" +
			" CONFIGURATION PROPERTIES" + "\n\t" +
			" ------------------------\n" + "\n\t" +
			"* SERVICE_COTURN_CREDENTIAL=" + this.coturnCredential + "\n\t" +
			"* SERVICE_COTURN_NAME=" + this.coturnName + "\n\t" +
			"* SERVICE_COTURN_REDIS_CONNECT_TIMEOUT=" + this.coturnRedisConnectTimeout + "\n\t" +
			"* SERVICE_COTURN_REDIS_DBNAME=" + this.coturnRedisDbname + "\n\t" +
			"* SERVICE_COTURN_REDIS_PASSWORD=" + this.coturnRedisPassword + "\n\t" +
			"* SERVICE_COTURN_URIS_CONFERENCE=" + this.coturnUrisConference + "\n\t" +
			"* SERVICE_COTURN_URIS_STREAMING=" + this.coturnUrisStreaming+ "\n\t" +
			"* SERVICE_DOMAIN_OR_PUBLIC_IP=" + this.domainOrPublicIp + "\n\t" +
			"* SERVICE_DOTENV=" + this.dotenv + "\n\t" +
			"* SERVICE_HTTPS_PORT=" + this.httpsPort + "\n\t" +
			"* SERVICE_KMS_URIS_CONFERENCE=" + this.kmsUrisConference + "\n\t" +
			"* SERVICE_KMS_URIS_STREAMING=" + this.kmsUrisStreaming + "\n\t" +
			"* SERVICE_POLICY_LOCATION=" + this.policyLocation + "\n\t" +
			"* SERVICE_REMOTE_CDR=" + this.remoteCdr + "\n\t" +
			"* SERVICE_REMOTE_CDR_PATH=" + this.remoteCdrPath + "\n\t" +
			"* SERVICE_REMOTE_RECORDING=" + this.remoteRecording + "\n\t" +
			"* SERVICE_REMOTE_RECORDING_AUTOSTOP_TIMEOUT=" + this.remoteRecordingAutostopTimeout + "\n\t" +
			"* SERVICE_REMOTE_RECORDING_COMPOSED_URL=" + this.remoteRecordingComposedUrl + "\n\t" +
			"* SERVICE_REMOTE_RECORDING_CUSTOM_LAYOUT=" + this.remoteRecordingCustomLayout + "\n\t" +
			"* SERVICE_REMOTE_RECORDING_DEBUG=" + this.remoteRecordingDebug + "\n\t" +
			"* SERVICE_REMOTE_RECORDING_NOTIFICATION=" + this.remoteRecordingNotification + "\n\t" +
			"* SERVICE_REMOTE_RECORDING_PATH=" + this.remoteRecordingPath + "\n\t" +
			"* SERVICE_REMOTE_RECORDING_PUBLIC_ACCESS=" + this.remoteRecordingPublicAccess + "\n\t" +
			"* SERVICE_REMOTE_RECORDING_VERSION=" + this.remoteRecordingVersion + "\n\t" +
			"* SERVICE_REMOTE_SECRET=" + this.remoteSecret + "\n\t" +
			"* SERVICE_REMOTE_SESSIONS_GARBAGE_INTERVAL=" + this.remoteSessionsGarbageInterval + "\n\t" +
			"* SERVICE_REMOTE_SESSIONS_GARBAGE_THRESHOLD=" + this.remoteSessionsGarbageThreshold + "\n\t" +
			"* SERVICE_REMOTE_STREAMS_VIDEO_MAX_RECV_BANDWIDTH=" + this.remoteStreamsVideoMaxRecvBandwidth + "\n\t" +
			"* SERVICE_REMOTE_STREAMS_VIDEO_MAX_SEND_BANDWIDTH=" + this.remoteStreamsVideoMaxSendBandwidth + "\n\t" +
			"* SERVICE_REMOTE_STREAMS_VIDEO_MIN_RECV_BANDWIDTH=" + this.remoteStreamsVideoMinRecvBandwidth + "\n\t" +
			"* SERVICE_REMOTE_STREAMS_VIDEO_MIN_SEND_BANDWIDTH=" + this.remoteStreamsVideoMinSendBandwidth + "\n\t" +
			"* SERVICE_WSS=" + this.wss + "\n";
	}
}
