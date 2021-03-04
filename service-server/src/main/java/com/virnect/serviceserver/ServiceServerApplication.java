package com.virnect.serviceserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.bouncycastle.util.Arrays;
import org.kurento.jsonrpc.internal.server.config.JsonRpcConfiguration;
import org.kurento.jsonrpc.server.JsonRpcConfigurer;
import org.kurento.jsonrpc.server.JsonRpcHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.virnect.mediaserver.cdr.CDRLogger;
import com.virnect.mediaserver.cdr.CDRLoggerFile;
import com.virnect.mediaserver.cdr.CallDetailRecord;
import com.virnect.mediaserver.config.MediaServerProperties;
import com.virnect.mediaserver.core.SessionEventsHandler;
import com.virnect.mediaserver.core.SessionManager;
import com.virnect.mediaserver.core.TokenGenerator;
import com.virnect.mediaserver.coturn.CoturnCredentialsService;
import com.virnect.mediaserver.coturn.CoturnCredentialsServiceFactory;
import com.virnect.mediaserver.kurento.core.KurentoParticipantEndpointConfig;
import com.virnect.mediaserver.kurento.core.KurentoSessionEventsHandler;
import com.virnect.mediaserver.kurento.core.KurentoSessionManager;
import com.virnect.mediaserver.kurento.kms.DummyLoadManager;
import com.virnect.mediaserver.kurento.kms.FixedKmsManager;
import com.virnect.mediaserver.kurento.kms.KmsManager;
import com.virnect.mediaserver.kurento.kms.LoadManager;
import com.virnect.mediaserver.recording.DummyRecordingDownloader;
import com.virnect.mediaserver.recording.RecordingDownloader;
import com.virnect.mediaserver.recording.service.RecordingManager;
import com.virnect.mediaserver.rpc.RpcHandler;
import com.virnect.mediaserver.rpc.RpcNotificationService;
import com.virnect.mediaserver.utils.CommandExecutor;
import com.virnect.mediaserver.utils.GeoLocationByIp;
import com.virnect.mediaserver.utils.GeoLocationByIpDummy;
import com.virnect.mediaserver.utils.MediaNodeStatusManager;
import com.virnect.mediaserver.utils.MediaNodeStatusManagerDummy;
import com.virnect.mediaserver.utils.QuarantineKiller;
import com.virnect.mediaserver.utils.QuarantineKillerDummy;
import com.virnect.mediaserver.webhook.CDRLoggerWebhook;
import com.virnect.serviceserver.global.config.HttpHandshakeInterceptor;
import com.virnect.serviceserver.global.config.RemoteServiceConfig;
import com.virnect.serviceserver.global.config.UrlConstants;
import com.virnect.serviceserver.infra.token.TokenGeneratorDefault;

@Import({ JsonRpcConfiguration.class })
@ComponentScan(value = {
    "com.virnect.data",
    "com.virnect.serviceserver"
})
@EntityScan(value = {
    "com.virnect.data.domain"
})
@EnableJpaRepositories(value = {
    "com.virnect.data.dao"
})
@SpringBootApplication
public class ServiceServerApplication extends SpringBootServletInitializer implements JsonRpcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(ServiceServerApplication.class);

    //public static final String WS_PATH = "/remote/websocket";

    /*public static String wsUrl;
    public static String wssUrl;
    public static String httpUrl;
    public static String storageUrl;
    public static List<String> mediaConferenceUris;
    public static List<String> mediaStreamingUris;
    public static List<String> coturnConferenceUris;
    public static List<String> coturnStreamingUris;*/

    /*@Autowired
    RemoteServiceConfig config;
*/
    /*@Autowired
    MediaServerProperties mediaServerProperties;*/

    /*@Autowired
    ServiceSessionManager serviceSessionManager;*/

   /* @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }*/

    /*@Autowired
    SessionDataRepository sessionDataRepository;*/

    /*@Bean
    @DependsOn("remoteServiceConfig")
    public MediaServerConfig mediaServerConfig(RemoteServiceConfig remoteServiceConfig) {
        log.info("RemoteService Server using mediaServerConfig");
        return new MediaServerConfig();
    }*/

    /*@Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }*/

    /*@Bean
    @ConditionalOnMissingBean
    @DependsOn("remoteServiceProperties")
    public RemoteServiceConfig remoteServiceConfig(RemoteServiceProperties remoteServiceProperties) {
        return new RemoteServiceConfig();
    }*/

    /*@Bean
    @ConditionalOnMissingBean
    @DependsOn("remoteServiceConfig")
    public RemoteServiceProperties remoteServiceProperties() {
        return new RemoteServiceProperties();
    }*/


    /*@Bean
    @ConditionalOnMissingBean
    public FileService fileService() {
        return new FileService();
    }

    @Bean
    @ConditionalOnMissingBean
    public SessionService sessionService() {
        return new SessionService();
    }*/

    @Bean
    @ConditionalOnMissingBean
    @DependsOn("remoteServiceConfig")
    public KmsManager kmsManager(RemoteServiceConfig remoteServiceConfig) {
        /*if (remoteServiceConfig.getKmsUris().isEmpty()) {
            throw new IllegalArgumentException("'KMS_URIS' should contain at least one KMS url");
        }
        String firstKmsWsUri = remoteServiceConfig.getKmsUris().get(0);
        log.info("RemoteService Server using one KMS: {}", firstKmsWsUri);
        return new FixedOneKmsManager();*/
        if (remoteServiceConfig.getKmsUris().isEmpty()) {
            throw new IllegalArgumentException("'KMS_URIS' should contain at least one KMS url");
        }

        for (String kmsWsUri: remoteServiceConfig.getKmsUris()) {
            log.info("RemoteService Server using one KMS: {}", kmsWsUri);
        }
        return new FixedKmsManager();
        //return new FixedOneKmsManager();
    }

    @Bean
    @ConditionalOnMissingBean
    @DependsOn("remoteServiceConfig")
    public CallDetailRecord cdr(MediaServerProperties mediaServerProperties) {
        List<CDRLogger> loggers = new ArrayList<>();
        if (mediaServerProperties.serverProperty.isServiceCdr()) {
            log.info("RemoteService CDR service is enabled");
            loggers.add(new CDRLoggerFile());
        } else {
            log.info("RemoteService CDR service is disabled (may be enable with 'remote_cdr=true')");
        }
        if (mediaServerProperties.serverProperty.isWebhookEnabled()) {
            log.info("RemoteService Webhook service is enabled");
            loggers.add(new CDRLoggerWebhook(mediaServerProperties));
        } else {
            log.info("RemoteService Webhook service is disabled (may be enabled with 'remote_webhook=true')");
        }
        return new CallDetailRecord(loggers);
    }

    /*public CallDetailRecord cdr(RemoteServiceConfig remoteServiceConfig) {
        List<CDRLogger> loggers = new ArrayList<>();
        if (remoteServiceConfig.isCdrEnabled()) {
            log.info("RemoteService CDR service is enabled");
            loggers.add(new CDRLoggerFile());
        } else {
            log.info("RemoteService CDR service is disabled (may be enable with 'remote_cdr=true')");
        }
        if (remoteServiceConfig.isWebhookEnabled()) {
            log.info("RemoteService Webhook service is enabled");
            loggers.add(new CDRLoggerWebhook(remoteServiceConfig));
        } else {
            log.info("RemoteService Webhook service is disabled (may be enabled with 'remote_webhook=true')");
        }
        return new CallDetailRecord(loggers);
    }*/

    @Bean
    @ConditionalOnMissingBean
    @DependsOn("remoteServiceConfig")
    public CoturnCredentialsService coturnCredentialsService(RemoteServiceConfig remoteServiceConfig) {
        return new CoturnCredentialsServiceFactory().getCoturnCredentialsService(remoteServiceConfig.getSpringProfile());
    }

    @Bean
    @ConditionalOnMissingBean
    @DependsOn("remoteServiceConfig")
    public SessionManager sessionManager() {
        KurentoSessionManager kurentoSessionManager = new KurentoSessionManager();
        return kurentoSessionManager;
    }

    @Bean
    @ConditionalOnMissingBean
    @DependsOn("remoteServiceConfig")
    public RpcHandler rpcHandler() {
        return new RpcHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    @DependsOn("remoteServiceConfig")
    public SessionEventsHandler sessionEventsHandler() {
        return new KurentoSessionEventsHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    @DependsOn("remoteServiceConfig")
    public TokenGenerator tokenGenerator() {
        return new TokenGeneratorDefault();
    }

    @Bean
    @ConditionalOnMissingBean
    @DependsOn("remoteServiceConfig")
    public RecordingManager recordingManager() {
        return new RecordingManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public LoadManager loadManager() {
        return new DummyLoadManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public RpcNotificationService notificationService() {
        return new RpcNotificationService();
    }

    @Bean
    @ConditionalOnMissingBean
    public KurentoParticipantEndpointConfig kurentoEndpointConfig() {
        return new KurentoParticipantEndpointConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    public RecordingDownloader recordingDownload() {
        return new DummyRecordingDownloader();
    }

    @Bean
    @ConditionalOnMissingBean
    public GeoLocationByIp geoLocationByIp() {
        return new GeoLocationByIpDummy();
    }

    @Bean
    @ConditionalOnMissingBean
    public QuarantineKiller quarantineKiller() {
        return new QuarantineKillerDummy();
    }

    @Bean
    @ConditionalOnMissingBean
    public MediaNodeStatusManager mediaNodeStatusManager() {
        return new MediaNodeStatusManagerDummy();
    }


    @Override
    public void registerJsonRpcHandlers(JsonRpcHandlerRegistry registry) {
        registry.addHandler(rpcHandler().withPingWatchdog(true).withInterceptors(new HttpHandshakeInterceptor()),
            UrlConstants.WS_PATH
        );
    }

    public static String getContainerIp() throws IOException, InterruptedException {
        return CommandExecutor.execCommand(5000, "/bin/sh", "-c", "hostname -i | awk '{print $1}'");
    }

    public static void main(String[] args) throws Exception {
        // Set System property spring.profiles.active value
        // Must be local, develop, staging or production.
        // if no profiles set, falling back to default profiles
        /*String profiles = System.getProperty("spring.profiles");
        log.info("Using spring profile active is " + profiles);
        if(profiles != null) {
            log.info("Using spring profile active is " + profiles);
            System.setProperty("spring.profiles.active", profiles);
        }*/

        Map<String, String> CONFIG_PROPS = checkConfigProperties(RemoteServiceConfig.class);

        if (CONFIG_PROPS.get("SERVER_PORT") != null) {

            // Configuration property SERVER_PORT has been explicitly defined.
            // Must initialize the application in that port on the host regardless of what
            // HTTPS_PORT says. HTTPS_PORT does get used in the public URL.

            System.setProperty("server.port", CONFIG_PROPS.get("SERVER_PORT"));

            log.warn(
                    "You have set property server.port (or SERVER_PORT). This will serve RemoteService Server on your host at port "
                            + CONFIG_PROPS.get("SERVER_PORT") + ". But property HTTPS_PORT ("
                            + CONFIG_PROPS.get("service.https_port")
                            + ") still configures the port that should be used to connect to RemoteService Server from outside. "
                            + "Bear this in mind when configuring a proxy in front of RemoteService Server");

        } else if (CONFIG_PROPS.get("service.https_port") != null) {

            // Configuration property SERVER_PORT has NOT been explicitly defined.
            // Must initialize the application in port HTTPS_PORT on the host. HTTPS_PORT
            // does get used in the public URL as well.
            System.setProperty("server.port", CONFIG_PROPS.get("service.https_port"));

        }

        log.info("Using /dev/urandom for secure random generation");
        System.setProperty("java.security.egd", "file:/dev/./urandom");
        SpringApplication.run(ServiceServerApplication.class, Arrays.append(args, "--spring.main.banner-mode=off"));
        //disableSslVerification();
    }

    public static <T> Map<String, String> checkConfigProperties(Class<T> configClass) throws InterruptedException {
        ConfigurableApplicationContext app = SpringApplication.run(configClass, new String[] { "--spring.main.web-application-type=none" });
        RemoteServiceConfig config = app.getBean(RemoteServiceConfig.class);
        List<com.virnect.serviceserver.global.config.RemoteServiceConfig.Error> errors = config.getConfigErrors();

        if (!errors.isEmpty()) {
            // @formatter:off
            String msg = "\n\n\n" + "   Configuration errors\n" + "   --------------------\n" + "\n";
            for (com.virnect.serviceserver.global.config.RemoteServiceConfig.Error error : config.getConfigErrors()) {
                msg += "   * ";
                if (error.getProperty() != null) {
                    msg += "Property " + config.getPropertyName(error.getProperty());
                    if (error.getValue() == null || error.getValue().equals("")) {
                        msg += " is not set. ";
                    } else {
                        msg += "=" + error.getValue() + ". ";
                    }
                }
                msg += error.getMessage() + "\n";
            }
            msg += "\n" + "\n" + "   Fix config errors\n" + "   ---------------\n" + "\n"
                    + "   1) Return to shell pressing Ctrl+C\n"
                    + "   2) Set correct values in '.env' configuration file\n" + "   3) Restart RemoteService with:\n"
                    + "\n" + "      $ ./remoteservice restart\n" + "\n";
            // @formatter:on
            log.info(msg);
            // Wait forever
            new Semaphore(0).acquire();
        } else {
            String msg = "\n\n\n" + "   Configuration properties\n" + "   ------------------------\n" + "\n";

            final Map<String, String> configProps = config.getConfigProps();
            List<String> configPropNames = new ArrayList<>(config.getUserProperties());
            Collections.sort(configPropNames);

            for (String property : configPropNames) {
                String value = configProps.get(property);
                msg += "   * " + config.getPropertyName(property) + "=" + (value == null ? "" : value) + "\n";
            }
            msg += "\n\n";
            log.info(msg);
            // Close the auxiliary ApplicationContext
            app.close();
            return configProps;
        }
        return null;
    }

    /*private static void disableSslVerification() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }*/

    /*@EventListener(ApplicationReadyEvent.class)
    public void whenReady() {
        String websocket = wsUrl + WS_PATH + "/";

        // @formatter:off
        String msg = "\n\n----------------------------------------------------\n" + "\n"
                + "   RemoteService is ready!\n"
                + "   ---------------------------\n" + "\n"
                + "   * RemoteService Server: " + httpUrl + "\n" + "\n"
                + "   * RemoteService Media Server Conference list: " + mediaConferenceUris.toString() + "\n" + "\n"
                + "   * RemoteService Media Server Streaming list: " + mediaStreamingUris.toString() + "\n" + "\n"
                + "   * RemoteService Coturn Server Conference list: " + coturnConferenceUris.toString() + "\n" + "\n"
                + "   * RemoteService Coturn Server Streaming list: " + coturnStreamingUris.toString() + "\n" + "\n"
                + "   * RemoteService Websocket: " + websocket + "\n" + "\n"
                + "   * RemoteService Storage Server: " + storageUrl + "\n" + "\n"
                + "   * RemoteService Temp Directory: " + System.getProperty("java.io.tmpdir") + "\n" + "\n"
                + "----------------------------------------------------\n";
        // @formatter:on
        log.info(msg);

        //
        //sessionDataRepository.removeAllRoom();
    }*/
}
