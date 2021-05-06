package com.virnect.mediaserver.config.property;

import com.virnect.mediaserver.cdr.CDREventName;
import org.apache.http.Header;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServerProperty {

    // Service Secret
    private String serviceSecret;

    // Service CDR properties
    private boolean serviceCdr;
    private String serviceCdrPath;

    // Service Webhook properties
    private boolean webhookEnabled;
    private String webhookEndpoint;
    private List<Header> webhookHeadersList;
    private List<CDREventName> webhookEventsList;

    // Service KMS properties
    private List<String> kmsUrisConference;
    private List<String> kmsUrisStreaming;

    // Service Session Garbage properties
    private int sessionsGarbageInterval;
    private int sessionsGarbageThreshold;

    public String getServiceSecret() {
        return serviceSecret;
    }

    public void setServiceSecret(String serviceSecret) {
        this.serviceSecret = serviceSecret;
    }

    public boolean isServiceCdr() {
        return serviceCdr;
    }

    public void setServiceCdr(boolean serviceCdr) {
        this.serviceCdr = serviceCdr;
    }

    public String getServiceCdrPath() {
        return serviceCdrPath;
    }

    public void setServiceCdrPath(String serviceCdrPath) {
        this.serviceCdrPath = serviceCdrPath;
    }

    public boolean isWebhookEnabled() {
        return webhookEnabled;
    }

    public void setWebhookEnabled(boolean webhookEnabled) {
        this.webhookEnabled = webhookEnabled;
    }

    public String getWebhookEndpoint() {
        return webhookEndpoint;
    }

    public void setWebhookEndpoint(String webhookEndpoint) {
        this.webhookEndpoint = webhookEndpoint;
    }

    public List<Header> getWebhookHeadersList() {
        return webhookHeadersList;
    }

    public void setWebhookHeadersList(List<Header> webhookHeadersList) {
        this.webhookHeadersList = webhookHeadersList;
    }

    public List<CDREventName> getWebhookEventsList() {
        return webhookEventsList;
    }

    public void setWebhookEventsList(List<CDREventName> webhookEventsList) {
        this.webhookEventsList = webhookEventsList;
    }

    public List<String> getKmsUrisConference() {
        return kmsUrisConference;
    }

    public void setKmsUrisConference(List<String> kmsUrisConference) {
        this.kmsUrisConference = kmsUrisConference;
    }

    public List<String> getKmsUrisStreaming() {
        return kmsUrisStreaming;
    }

    public void setKmsUrisStreaming(List<String> kmsUrisStreaming) {
        this.kmsUrisStreaming = kmsUrisStreaming;
    }

    public int getSessionsGarbageInterval() {
        return sessionsGarbageInterval;
    }

    public void setSessionsGarbageInterval(int sessionsGarbageInterval) {
        this.sessionsGarbageInterval = sessionsGarbageInterval;
    }

    public int getSessionsGarbageThreshold() {
        return sessionsGarbageThreshold;
    }

    public void setSessionsGarbageThreshold(int sessionsGarbageThreshold) {
        this.sessionsGarbageThreshold = sessionsGarbageThreshold;
    }

    public boolean isServiceSecret(String secret) {
        return secret.equals(this.serviceSecret);
    }
}
