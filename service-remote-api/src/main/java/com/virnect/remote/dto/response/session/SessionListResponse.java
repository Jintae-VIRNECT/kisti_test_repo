package com.virnect.remote.dto.response.session;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SessionListResponse implements Serializable {
    @JsonProperty("numberOfElements")
    private int numberOfElements;
    @JsonProperty("content")
    private List<Content> content;
    //private Content[] content;
    //private JsonObject[] content;
    //private List<Content> contentData;
    //private List<Json> content;
}
