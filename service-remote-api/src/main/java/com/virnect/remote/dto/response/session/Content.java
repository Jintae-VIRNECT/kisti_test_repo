package com.virnect.remote.dto.response.session;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Content implements Serializable {
    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("createdAt")
    private long createdAt;

    @JsonProperty("mediaMode")
    private String mediaMode;

    @JsonProperty("recordingMode")
    private String recordingMode;

    @JsonProperty("defaultOutputMode")
    private String defaultOutputMode;

    @JsonProperty("defaultRecordingLayout")
    private String defaultRecordingLayout;

    @JsonProperty("customSessionId")
    private String customSessionId;

    @JsonProperty("connections")
    private Connections connections;

    @JsonProperty("recording")
    private boolean recording;

    @Getter
    @Setter
    private static class Connections implements Serializable {
        @JsonProperty("numberOfElements")
        private int numberOfElements;

        @JsonProperty("content")
        private List<ConnectionContent> content;

        @Getter
        @Setter
        private static class ConnectionContent {

        }
    }

    @Override
    public String toString() {
        return "Content{" +
                "sessionId='" + sessionId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", mediaMode='" + mediaMode + '\'' +
                ", recordingMode='" + recordingMode + '\'' +
                ", defaultOutputMode='" + defaultOutputMode + '\'' +
                ", defaultRecordingLayout='" + defaultRecordingLayout + '\'' +
                ", customSessionId='" + customSessionId + '\'' +
                ", connections='" + connections.toString() + '\'' +
                ", recording='" + recording + '\'' +
                '}';
    }
}
