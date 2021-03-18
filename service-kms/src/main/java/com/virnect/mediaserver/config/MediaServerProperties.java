package com.virnect.mediaserver.config;

import com.virnect.java.client.RemoteServiceRole;
import com.virnect.mediaserver.config.property.BandwidthProperty;
import com.virnect.mediaserver.config.property.CoturnProperty;
import com.virnect.mediaserver.config.property.RecordingProperty;
import com.virnect.mediaserver.config.property.ServerProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MediaServerProperties {
    public final ServerProperty serverProperty;
    public final CoturnProperty coturnProperty;
    public final RecordingProperty recordingProperty;
    public final BandwidthProperty bandwidthProperty;

    private String springProfile;
    private static String finalUrl;

    @Autowired
    public MediaServerProperties(
            ServerProperty serverProperty,
            CoturnProperty coturnProperty,
            RecordingProperty recordingProperty,
            BandwidthProperty bandwidthProperty) {
        this.serverProperty = serverProperty;
        this.coturnProperty = coturnProperty;
        this.recordingProperty = recordingProperty;
        this.bandwidthProperty = bandwidthProperty;
    }

    public String getSpringProfile() {
        return springProfile;
    }

    public void setSpringProfile(String profile) {
        this.springProfile = profile;
    }


    public void setFinalUrl(String finalUrlParam) {
        finalUrl = finalUrlParam.endsWith("/") ? (finalUrlParam) : (finalUrlParam + "/");
    }

    public String getFinalUrl() {
        return finalUrl;
    }

    public List<String> getKmsUris() {
        // add all kms uris
        List<String> kmsUris = new ArrayList<>();
        kmsUris.addAll(this.serverProperty.getKmsUrisConference());
        kmsUris.addAll(this.serverProperty.getKmsUrisStreaming());
        return kmsUris;
    }

    public String getServiceFrontendDefaultPath() {
        return "dashboard";
    }


    public String getCoturnDatabaseInfo() {
        return "\"ip=" + this.coturnProperty.getCoturnRedisIp() +
                " dbname=" + this.coturnProperty.getCoturnRedisDbname() +
                " password=" + this.coturnProperty.getCoturnRedisPassword() +
                " connect_timeout=" + this.coturnProperty.getCoturnRedisConnectTimeout() +
                "\"";
    }

    public boolean recordingCustomLayoutChanged(String path) {
        return !"/opt/remoteService/custom-layout".equals(path);
    }

    public RemoteServiceRole[] getRolesFromRecordingNotification() {
        RemoteServiceRole[] roles;
        switch (this.recordingProperty.getRecordingNotification()) {
            case none:
                roles = new RemoteServiceRole[0];
                break;
            case moderator:
                roles = new RemoteServiceRole[]{RemoteServiceRole.MODERATOR};
                break;
            case all:
                roles = new RemoteServiceRole[]{RemoteServiceRole.SUBSCRIBER, RemoteServiceRole.PUBLISHER, RemoteServiceRole.MODERATOR};
                break;
            case publisher_moderator:
            default:
                roles = new RemoteServiceRole[]{RemoteServiceRole.PUBLISHER, RemoteServiceRole.MODERATOR};
        }
        return roles;
    }
}
