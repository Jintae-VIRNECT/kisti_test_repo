package com.virnect.api.dto.rpc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

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
