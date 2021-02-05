package com.virnect.serviceserver.dto.response.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

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
