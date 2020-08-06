package com.virnect.serviceserver.config;

import com.virnect.serviceserver.ServiceServerApplication;
import com.virnect.serviceserver.cdr.CDREventName;
import com.virnect.serviceserver.recording.RecordingNotification;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


//@Configuration
@Slf4j
@Component
@ConfigurationProperties(prefix = "service", ignoreInvalidFields = true)
public class RemoteServiceProperties extends PropertyService {

    //private Map<String, ?> propertiesSource;
    //private List<RemoteServiceConfig.Error> propertiesErrors = new ArrayList<>();

    @Autowired
    protected Environment env;

    //@Autowired
    //private RemoteServiceProperties remoteServiceProperties;

    /*@Autowired
    public RemoteServiceProperties(Map<String, ?> propertiesSource) {
        super(propertiesSource);
    }*/

   /* @Autowired
    public RemoteServiceProperties() {
        super();
    }*/

    public RemoteServiceProperties deriveWithAdditionalPropertiesSource(Map<String, ?> propertiesSource) {
        RemoteServiceProperties prop = newRemoteServiceProperties();
        prop.env = env;
        prop.propertiesSource = propertiesSource;
        return prop;
    }

    protected RemoteServiceProperties newRemoteServiceProperties() {
        return new RemoteServiceProperties();
    }

    /*@Value("#{'${spring.profiles.active:}'.length() > 0 ? '${spring.profiles.active:}'.split(',') : \"default\"}")
    protected String springProfile;*/

    // Config properties
    private String dotenvPath;
    private String domainOrPublicIp;
    private String remoteServicePublicUrl;
    private String remoteWebsockUrl;
    private Integer httpsPort;
    private String remoteServiceSecret;
    private String certificateType;
    // Service CDR properties
    private boolean remoteServiceCdr;
    private String remoteServiceCdrPath;

    // Service Webhook properties
    private boolean remoteServiceWebhookEnabled;
    private String remoteServiceWebhookEndpoint;
    private List<Header> webhookHeadersList;
    private List<CDREventName> webhookEventsList;

    // Service Recording properties
    private boolean remoteServiceRecording;
    private boolean remoteServiceRecordingDebug;
    private boolean remoteServiceRecordingPublicAccess;
    private Integer remoteServiceRecordingAutostopTimeout;
    private String remoteServiceRecordingPath;
    private RecordingNotification remoteServiceRecordingNotification;
    private String remoteServiceRecordingCustomLayout;
    private String remoteServiceRecordingVersion;
    private String remoteServiceRecordingComposedUrl;

    // Service Bandwidth properties
    private Integer remoteServiceStreamsVideoMaxRecvBandwidth;
    private Integer remoteServiceStreamsVideoMinRecvBandwidth;
    private Integer remoteServiceStreamsVideoMaxSendBandwidth;
    private Integer remoteServiceStreamsVideoMinSendBandwidth;

    // Service KMS properties
    //private List<String> kmsUrisList;
    @Value("service.kms_uris")
    private List<String> kmsUris;

    // Service Coturn properties
    private String coturnUsername;
    private String coturnCredential;
    private List<String> coturnUrisList;
    private String coturnIp;

    // Service Coturn Redis database properties
    private String coturnRedisIp;
    private String coturnRedisDbname;
    private String coturnRedisPassword;
    private String coturnRedisConnectTimeout;

    // Service Session Garbage properties
    protected int remoteServiceSessionsGarbageInterval;
    protected int remoteServiceSessionsGarbageThreshold;

    public void setDotenvPath() {
        dotenvPath = getValue("DOTENV_PATH");
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
        return this.remoteServiceSecret;
    }

    public boolean isCdrEnabled() {
        return this.remoteServiceCdr;
    }

    public String getRemoteServiceCdrPath() {
        return this.remoteServiceCdrPath;
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

    public RecordingNotification getRemoteServiceRecordingNotification() {
        return this.remoteServiceRecordingNotification;
    }

    public String getRemoteServiceRecordingComposedUrl() {
        return this.remoteServiceRecordingComposedUrl;
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

    public List<String> getKmsUris() {
        //return kmsUrisList;
        return kmsUris;
    }

    public String getCoturnUsername() {
        return this.coturnUsername;
    }

    public String getCoturnCredential() {
        return this.coturnCredential;
    }

    public List<String> getCoturnUrisList() {
        return this.coturnUrisList;
    }

    public String getCoturnDatabaseIp() { return this.coturnRedisIp; }

    public String getCoturnDatabaseDbname() {
        return this.coturnRedisDbname;
    }

    public String getCoturnDatabasePassword() {
        return this.coturnRedisPassword;
    }

    public String getCoturnDatabaseConnectTimeout() { return this.coturnRedisConnectTimeout; }

    public String getCoturnIp() {
        return this.coturnIp;
    }

    public int getSessionGarbageInterval() {
        return remoteServiceSessionsGarbageInterval;
    }

    public int getSessionGarbageThreshold() {
        return remoteServiceSessionsGarbageThreshold;
    }

    // Derived properties
    private static String finalUrl;
    private boolean isTurnadminAvailable = false;

    public void setFinalUrl(String finalUrlParam) {
        finalUrl = finalUrlParam.endsWith("/") ? (finalUrlParam) : (finalUrlParam + "/");
    }

    public String getFinalUrl() {
        return finalUrl;
    }

    public void setTurnadminAvailable(boolean available) {
        this.isTurnadminAvailable = available;
    }

    public boolean isTurnadminAvailable() {
        return this.isTurnadminAvailable;
    }

    protected void checkConfigurationProperties(boolean loadDotenv) {
        /*if (loadDotenv) {
            dotenvPath = getValue("DOTENV_PATH");
            this.populatePropertySourceFromDotenv();
        }*/

        checkHttpsPort();
        checkDomainOrPublicIp();
        populateSpringServerPort();

        coturnUsername = getValue("service.coturn_name");
        coturnCredential = getValue("service.coturn_credential");
        coturnUrisList = checkCoturnUris();

        coturnRedisDbname = getValue("service.coturn_redis_dbname");
        coturnRedisPassword = getValue("service.coturn_redis_password");
        coturnRedisConnectTimeout = getValue("service.coturn_redis_connect_timeout");

        //remoteServiceSecret = asNonEmptyString("service.remote_secret");
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

        //kmsUrisList = checkKmsUris();
        kmsUris = checkKmsUris();

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
                //this.coturnIp = new URL(this.getFinalUrl()).getHost();
                this.coturnIp = new URL(finalUrl).getHost();
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
            //this.remoteServicePublicwss = "wss://" + getValue("service.gateway");
            this.remoteWebsockUrl = getValue("service.wss");
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
        log.info("calculatePublicUrl : {}", finalUrl);
        this.setFinalUrl(finalUrl);
        //ServiceServerApplication.httpUrl = this.getFinalUrl();
        ServiceServerApplication.httpUrl = finalUrl;
        //
        ServiceServerApplication.wssUrl = this.remoteWebsockUrl + ServiceServerApplication.WS_PATH;
    }

    public List<String> checkCoturnUris() {

        String property = "service.coturn_uris";

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
