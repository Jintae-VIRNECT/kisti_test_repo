package com.virnect.mediaserver.config.property;

import com.virnect.mediaserver.recording.RecordingNotification;
import org.springframework.stereotype.Component;

@Component
public class RecordingProperty {
    // Service Recording properties
    private boolean recording;
    private boolean recordingDebug;
    private boolean recordingPublicAccess;
    private Integer recordingAutoStopTimeout;
    private String recordingPath;
    private RecordingNotification recordingNotification;
    private String recordingCustomLayout;
    private String recordingVersion;
    private String recordingComposedUrl;

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }

    public boolean isRecordingDebug() {
        return recordingDebug;
    }

    public void setRecordingDebug(boolean recordingDebug) {
        this.recordingDebug = recordingDebug;
    }

    public boolean isRecordingPublicAccess() {
        return recordingPublicAccess;
    }

    public void setRecordingPublicAccess(boolean recordingPublicAccess) {
        this.recordingPublicAccess = recordingPublicAccess;
    }

    public Integer getRecordingAutoStopTimeout() {
        return recordingAutoStopTimeout;
    }

    public void setRecordingAutoStopTimeout(Integer recordingAutoStopTimeout) {
        this.recordingAutoStopTimeout = recordingAutoStopTimeout;
    }

    public String getRecordingPath() {
        return recordingPath;
    }

    public void setRecordingPath(String recordingPath) {
        this.recordingPath = recordingPath;
    }

    public RecordingNotification getRecordingNotification() {
        return recordingNotification;
    }

    public void setRecordingNotification(RecordingNotification recordingNotification) {
        this.recordingNotification = recordingNotification;
    }

    public String getRecordingCustomLayout() {
        return recordingCustomLayout;
    }

    public void setRecordingCustomLayout(String recordingCustomLayout) {
        this.recordingCustomLayout = recordingCustomLayout;
    }

    public String getRecordingVersion() {
        return recordingVersion;
    }

    public void setRecordingVersion(String recordingVersion) {
        this.recordingVersion = recordingVersion;
    }

    public String getRecordingComposedUrl() {
        return recordingComposedUrl;
    }

    public void setRecordingComposedUrl(String recordingComposedUrl) {
        this.recordingComposedUrl = recordingComposedUrl;
    }
}
