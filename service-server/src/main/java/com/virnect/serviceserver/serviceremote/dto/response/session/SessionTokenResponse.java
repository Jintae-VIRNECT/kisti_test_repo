package com.virnect.serviceserver.serviceremote.dto.response.session;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SessionTokenResponse implements Serializable {
    @JsonProperty("id")
    private String id;
    @JsonProperty("session")
    private String session;
    @JsonProperty("role")
    private String role;
    @JsonProperty("data")
    private String data;
    @JsonProperty("token")
    private String token;
    @JsonProperty("kurentoOptions")
    private KurentoOptions kurentoOptions;
    @JsonProperty("error")
    private String error;

    @Getter
    @Setter
    private static class KurentoOptions implements Serializable {
        @JsonProperty("videoMaxRecvBandwidth")
        private int videoMaxRecvBandwidth;
        @JsonProperty("videoMinRecvBandwidth")
        private int videoMinRecvBandwidth;
        @JsonProperty("videoMaxSendBandwidth")
        private int videoMaxSendBandwidth;
        @JsonProperty("videoMinSendBandwidth")
        private int videoMinSendBandwidth;
        @JsonProperty("allowedFilters")
        private List<String> allowedFilters;

    }


}
