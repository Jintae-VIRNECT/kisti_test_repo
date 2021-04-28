package com.virnect.data.domain.session;

import lombok.*;

import javax.persistence.*;

import com.virnect.data.domain.BaseTimeEntity;
import com.virnect.data.domain.room.Room;

@Entity
@Getter
@Setter
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

    @Column(name = "recording", nullable = false)
    private boolean recording;

    @Column(name = "keepalive", nullable = false)
    private boolean keepalive;

    @Column(name = "session_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SessionType sessionType;

    @Column(name = "publisher_id", nullable = false)
    private String publisherId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Override
    public String toString() {
        return "SessionProperty{" +
                "id=" + id +
                ", mediaMode='" + mediaMode + '\'' +
                ", recordingMode='" + recordingMode + '\'' +
                ", defaultOutputMode='" + defaultOutputMode + '\'' +
                ", defaultRecordingLayout='" + defaultRecordingLayout + '\'' +
                ", recording='" + recording + '\'' +
                ", keepalive='" + keepalive + '\'' +
                ", sessionType='" + sessionType + '\'' +
                ", publisherId='" + publisherId + '\'' +
                '}';
    }
}
