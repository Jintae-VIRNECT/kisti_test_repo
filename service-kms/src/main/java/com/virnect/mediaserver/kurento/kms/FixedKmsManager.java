package com.virnect.mediaserver.kurento.kms;

import org.kurento.client.KurentoClient;
import org.kurento.commons.exception.KurentoException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FixedKmsManager extends KmsManager{
    //private StopWatch stopWatch = new StopWatch("kms");
    private Timer timer;


    @Override
    public List<Kms> initializeKurentoClients(List<KmsProperties> kmsProperties, boolean disconnectUponFailure) throws Exception {

        for (KmsProperties kmsProperty: kmsProperties) {
            KurentoClient kClient = null;
            Kms kms = new Kms(kmsProperty, loadManager);

            try {
                kClient = KurentoClient.create(kmsProperty.getUri(), this.generateKurentoConnectionListener(kms.getId()));
                this.addKms(kms);
                kms.setKurentoClient(kClient);

                // TODO: This should be done in KurentoClient connected event
                kms.setKurentoClientConnected(true);
                kms.setTimeOfKurentoClientConnection(System.currentTimeMillis());

            } catch (KurentoException e) {
                log.error("KMS in {} is not reachable by Remote Service Server", kmsProperty.getUri());
                if (kClient != null) {
                    kClient.destroy();
                }
                throw new Exception();
            }
        }
        return new ArrayList<>(this.getKmss());
    }

    @Override
    @PostConstruct
    protected void postConstructInitKurentoClients() {
        try {
            List<KmsProperties> kmsProps = new ArrayList<>();
            for (String kmsUri : this.mediaServerProperties.getKmsUris()) {
                String kmsId = KmsManager.generateKmsId();
                kmsProps.add(new KmsProperties(kmsId, kmsUri));
            }
            this.initializeKurentoClients(kmsProps, true);
        } catch (Exception e) {
            // Some KMS wasn't reachable
            //log.error("Shutting down Remote Service Server");
            System.exit(1);
            //timer = new Timer();
            //timer.schedule(new RemindTask(), 0, 1000);

        }
    }

    class RemindTask extends TimerTask {
        int count = 5;
        @Override
        public void run() {
            if (count > 0) {
                log.info("Shutting down Remote Service Server.... in {}", count);
                count --;
            } else {
                log.error("Shutting down Remote Service Server");
                //cancel(); //not necessary, because calls System.exit
                System.exit(1);
            }
        }
    }
}
