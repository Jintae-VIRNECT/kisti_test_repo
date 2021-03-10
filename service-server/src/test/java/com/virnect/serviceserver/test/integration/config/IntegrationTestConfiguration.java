package com.virnect.serviceserver.test.integration.config;

import com.virnect.mediaserver.core.SessionManager;
import com.virnect.mediaserver.kurento.core.KurentoSessionManager;
import com.virnect.mediaserver.kurento.kms.FixedOneKmsManager;
import com.virnect.mediaserver.kurento.kms.Kms;
import com.virnect.mediaserver.kurento.kms.KmsManager;
import com.virnect.mediaserver.kurento.kms.KmsProperties;
import org.kurento.client.Continuation;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.kurento.client.ServerManager;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestConfiguration
public class IntegrationTestConfiguration {

    @Bean
    public KurentoSessionManager kurentoSessionManager() {
        return new KurentoSessionManager();
    }

    @Bean
    public KmsManager kmsManager() throws Exception {
        final KmsManager spy = Mockito.spy(new FixedOneKmsManager());
        doAnswer(invocation -> {
            List<Kms> successfullyConnectedKmss = new ArrayList<>();
            List<KmsProperties> kmsProperties = invocation.getArgument(0);
            for (KmsProperties kmsProp : kmsProperties) {
                Kms kms = new Kms(kmsProp, spy.getLoadManager());
                KurentoClient kClient = mock(KurentoClient.class);

                doAnswer(i -> {
                    Thread.sleep((long) (Math.random() * 1000));
                    ((Continuation<MediaPipeline>) i.getArgument(0)).onSuccess(mock(MediaPipeline.class));
                    return null;
                }).when(kClient).createMediaPipeline((Continuation<MediaPipeline>) any());

                ServerManager serverManagerMock = mock(ServerManager.class);
                when(serverManagerMock.getCpuCount()).thenReturn(new Random().nextInt(32) + 1);
                when(kClient.getServerManager()).thenReturn(serverManagerMock);

                kms.setKurentoClient(kClient);
                kms.setKurentoClientConnected(true);
                kms.setTimeOfKurentoClientConnection(System.currentTimeMillis());

                spy.addKms(kms);
                successfullyConnectedKmss.add(kms);
            }
            return successfullyConnectedKmss;
        }).when(spy).initializeKurentoClients(any(List.class), any(Boolean.class));
        return spy;
    }
}
