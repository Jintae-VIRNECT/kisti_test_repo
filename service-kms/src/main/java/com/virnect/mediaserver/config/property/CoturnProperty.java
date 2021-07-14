package com.virnect.mediaserver.config.property;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CoturnProperty {

    private boolean isTurnadminAvailable = false;

    // Service Coturn properties
    private String coturnUsername;
    private String coturnCredential;
    private List<String> coturnUrisConference;
    private List<String> coturnUrisStreaming;
    private String coturnIp;

    // Service Coturn Redis database properties
    private String coturnRedisIp;
    private String coturnRedisDbname;
    private String coturnRedisPassword;
    private String coturnRedisConnectTimeout;

    public boolean isTurnadminAvailable() {
        return isTurnadminAvailable;
    }

    public void setTurnadminAvailable(boolean turnadminAvailable) {
        isTurnadminAvailable = turnadminAvailable;
    }

    public String getCoturnUsername() {
        return coturnUsername;
    }

    public void setCoturnUsername(String coturnUsername) {
        this.coturnUsername = coturnUsername;
    }

    public String getCoturnCredential() {
        return coturnCredential;
    }

    public void setCoturnCredential(String coturnCredential) {
        this.coturnCredential = coturnCredential;
    }

    public List<String> getCoturnUrisConference() {
        return coturnUrisConference;
    }

    public void setCoturnUrisConference(List<String> coturnUrisConference) {
        this.coturnUrisConference = coturnUrisConference;
    }

    public List<String> getCoturnUrisStreaming() {
        return coturnUrisStreaming;
    }

    public void setCoturnUrisSteaming(List<String> coturnUrisStreaming) {
        this.coturnUrisStreaming = coturnUrisStreaming;
    }

    public String getCoturnIp() {
        return coturnIp;
    }

    public void setCoturnIp(String coturnIp) {
        this.coturnIp = coturnIp;
    }

    public String getCoturnRedisIp() {
        return coturnRedisIp;
    }

    public void setCoturnRedisIp(String coturnRedisIp) {
        this.coturnRedisIp = coturnRedisIp;
    }

    public String getCoturnRedisDbname() {
        return coturnRedisDbname;
    }

    public void setCoturnRedisDbname(String coturnRedisDbname) {
        this.coturnRedisDbname = coturnRedisDbname;
    }

    public String getCoturnRedisPassword() {
        return coturnRedisPassword;
    }

    public void setCoturnRedisPassword(String coturnRedisPassword) {
        this.coturnRedisPassword = coturnRedisPassword;
    }

    public String getCoturnRedisConnectTimeout() {
        return coturnRedisConnectTimeout;
    }

    public void setCoturnRedisConnectTimeout(String coturnRedisConnectTimeout) {
        this.coturnRedisConnectTimeout = coturnRedisConnectTimeout;
    }
}
