package com.virnect.serviceserver.serviceremote.dto.response.session;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SessionResponse implements Serializable {
    @JsonProperty("id")
    private String id;
    @JsonProperty("createdAt")
    private String createdAt;
    @JsonProperty("error")
    private String error;
}
