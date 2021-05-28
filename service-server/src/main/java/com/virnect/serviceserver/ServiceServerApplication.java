package com.virnect.serviceserver;

import java.util.ArrayList;
import java.util.List;

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
import com.virnect.mediaserver.utils.GeoLocationByIp;
import com.virnect.mediaserver.utils.GeoLocationByIpDummy;
import com.virnect.mediaserver.utils.MediaNodeStatusManager;
import com.virnect.mediaserver.utils.MediaNodeStatusManagerDummy;
import com.virnect.mediaserver.utils.QuarantineKiller;
import com.virnect.mediaserver.utils.QuarantineKillerDummy;
import com.virnect.mediaserver.webhook.CDRLoggerWebhook;
import com.virnect.serviceserver.global.config.HttpHandshakeInterceptor;
import com.virnect.serviceserver.global.config.RemoteServiceConfig;
import com.virnect.serviceserver.global.config.property.RemoteServiceProperties;
import com.virnect.serviceserver.global.config.property.RemoteStorageProperties;
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

    public final static String WS_PATH = "/remote/websocket";
    @Bean
    @ConditionalOnMissingBean
    @DependsOn("remoteServiceConfig")
    public KmsManager kmsManager(RemoteServiceConfig remoteServiceConfig) {
        if (remoteServiceConfig.getKmsUris().isEmpty()) {
            throw new IllegalArgumentException("'KMS_URIS' should contain at least one KMS url");
        }

        for (String kmsWsUri: remoteServiceConfig.getKmsUris()) {
            log.info("RemoteService Server using one KMS: {}", kmsWsUri);
        }
        return new FixedKmsManager();
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

    @Bean
    @ConditionalOnMissingBean
    @DependsOn("remoteServiceConfig")
    public CoturnCredentialsService coturnCredentialsService(RemoteServiceConfig remoteServiceConfig) {
        return new CoturnCredentialsServiceFactory().getCoturnCredentialsService(remoteServiceConfig.remoteServiceProperties.getSpringProfile());
    }

    @Bean
    @ConditionalOnMissingBean
    @DependsOn("remoteServiceConfig")
    public SessionManager sessionManager() {
        return new KurentoSessionManager();
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
            WS_PATH
        );
    }

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext app = SpringApplication.run(RemoteServiceConfig.class,
            "--spring.main.web-application-type=none"
        );

        RemoteServiceProperties remoteServiceProperties = app.getBean(RemoteServiceProperties.class);
        RemoteStorageProperties remoteStorageProperties = app.getBean(RemoteStorageProperties.class);

        System.out.println(remoteServiceProperties.toString());
        System.out.println(remoteStorageProperties.toString());

        app.close();

        log.info("Using /dev/urandom for secure random generation");
        System.setProperty("java.security.egd", "file:/dev/./urandom");
        SpringApplication.run(ServiceServerApplication.class, Arrays.append(args, "--spring.main.banner-mode=off"));
    }

}
