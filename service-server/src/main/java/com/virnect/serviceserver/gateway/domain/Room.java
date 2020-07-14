package com.virnect.serviceserver.gateway.domain;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Room Domain Model Class
 * DATE:
 * AUTHOR:
 * EMAIL:
 * DESCRIPTION:
 *
 */
@Entity
@Getter
@Setter
@Audited
@Table(name = "rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id", nullable = false)
    private Long id;

    @Column(name = "session_id", unique = true)
    private String sessionId;

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

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "profile")
    private String profile;

    @Column(name = "leader_id", nullable = false)
    private String leaderId;

    @Column(name = "workspace_id", nullable = false)
    private String workspaceId;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> Members = new ArrayList<>();
    //private Collection<Member> Member;

    @Builder
    public Room(String sessionId,
                String mediaMode,
                String recordingMode,
                String defaultOutputMode,
                String defaultRecordingLayout,
                boolean recording,
                String title,
                String description,
                String profile,
                String leaderId,
                String workspaceId
                      ) {
        this.sessionId = sessionId;
        this.mediaMode = mediaMode;
        this.recordingMode = recordingMode;
        this.defaultOutputMode = defaultOutputMode;
        this.defaultRecordingLayout = defaultRecordingLayout;
        this.recording = recording;
        this.title = title;
        this.description = description;
        this.profile = profile;
        this.leaderId = leaderId;
        this.workspaceId = workspaceId;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", sessionId='" + sessionId + '\'' +
                ", mediaMode='" + mediaMode + '\'' +
                ", recordingMode='" + recordingMode + '\'' +
                ", defaultOutputMode='" + defaultOutputMode + '\'' +
                ", defaultRecordingLayout='" + defaultRecordingLayout + '\'' +
                ", recording='" + recording + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", leaderId='" + leaderId + '\'' +
                ", workspaceId='" + workspaceId + '\'' +
                ", profile='" + profile + '\'' +
                '}';
    }
}
