package com.virnect.data.dto.rpc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class RpcParamsRequest implements Serializable {
    @JsonProperty("sessionId")
    private String sessionId;
    @JsonProperty("to")
    private ArrayList<String> to;
    @JsonProperty("type")
    private String type;
    @JsonProperty("data")
    private String data;
}
