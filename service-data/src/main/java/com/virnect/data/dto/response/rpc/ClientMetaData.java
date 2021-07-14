package com.virnect.data.dto.response.rpc;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClientMetaData implements Serializable {
    @JsonProperty("clientData")
    private String clientData;
    @JsonProperty("roleType")
    private String roleType;
    @JsonProperty("deviceType")
    private String deviceType;
}
