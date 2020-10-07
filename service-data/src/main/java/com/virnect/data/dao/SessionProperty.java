package com.virnect.data.dao;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Audited
@Table(name = "session_property")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionProperty extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_property_id", nullable = false)
    private Long id;

    @Column(name = "media_mode", nullable = false)
    private String mediaMode;

    @Column(name = "recording_mode", nullable = false)
    private String recordingMode;

    @Column(name = "default_output_mode", nullable = false)
    private String defaultOutputMode;

    @Column(name = "default_recording_layout", nullable = false)
    private String defaultRecordingLayout;

    /*@Column(name = "custom_session_id", unique = true)
    private String customSessionId;*/

    @Column(name = "recording", nullable = false)
    private boolean recording;

    @Column(name = "translation", nullable = false)
    private boolean translation;

    @Column(name = "keepalive", nullable = false)
    private boolean keepalive;

    @Column(name = "sessionType", nullable = false)
    private SessionType sessionType;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Builder
    public SessionProperty(String mediaMode,
                           String recordingMode,
                           String defaultOutputMode,
                           String defaultRecordingLayout,
                           boolean recording,
                           boolean translation,
                           boolean keepalive,
                           SessionType sessionType,
                           Room room


    ) {
        this.mediaMode = mediaMode;
        this.recordingMode = recordingMode;
        this.defaultOutputMode = defaultOutputMode;
        this.defaultRecordingLayout = defaultRecordingLayout;
        this.recording = recording;
        this.translation = translation;
        this.keepalive = keepalive;
        this.sessionType = sessionType;
        this.room = room;
    }

    @Override
    public String toString() {
        return "SessionProperty{" +
                "id=" + id +
                ", mediaMode='" + mediaMode + '\'' +
                ", recordingMode='" + recordingMode + '\'' +
                ", defaultOutputMode='" + defaultOutputMode + '\'' +
                ", defaultRecordingLayout='" + defaultRecordingLayout + '\'' +
                ", recording='" + recording + '\'' +
                ", translation='" + translation + '\'' +
                ", keepalive='" + keepalive + '\'' +
                ", sessionType='" + sessionType + '\'' +
                '}';
    }
}
