package com.virnect.data.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.data.domain.session.SessionType;

@Entity
@Getter
@Setter
@Audited
@Table(name = "companies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id", nullable = false)
    private Long id;

    @Column(name = "company_code", unique = true)
    private int companyCode;

    @Column(name = "workspace_id", nullable = false)
    private String workspaceId;

    @Column(name = "license_name", nullable = false)
    private String licenseName;

    @Column(name = "session_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SessionType sessionType;

    @Column(name = "recording", nullable = false)
    private boolean recording;

    @Column(name = "storage", nullable = false)
    private boolean storage;

    @Column(name = "translation", nullable = false)
    private boolean translation;

    @Column(name = "stt_sync", nullable = false)
    private boolean sttSync;

    @Column(name = "stt_streaming", nullable = false)
    private boolean sttStreaming;

    @Column(name = "tts", nullable = false)
    private boolean tts;

    @Column(name = "video_restricted_mode", nullable = false)
    private boolean videoRestrictedMode;

    @Column(name = "audio_restricted_mode", nullable = false)
    private boolean audioRestrictedMode;

    @Column(name = "local_recording", nullable = false)
    private boolean localRecording;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Language language;

    @Builder
    public Company(int companyCode,
        String workspaceId,
        String licenseName,
        SessionType sessionType,
        Boolean recording,
        Boolean storage,
        Boolean translation,
        Boolean sttSync,
        Boolean sttStreaming,
        Boolean tts,
        Boolean videoRestrictedMode,
        Boolean audioRestrictedMode,
        Boolean localRecording,
        Language language
    ) {
        this.companyCode = companyCode;
        this.workspaceId = workspaceId;
        this.licenseName = licenseName;
        this.sessionType = sessionType;
        this.recording = recording;
        this.storage = storage;
        this.translation = translation;
        this.sttSync = sttSync;
        this.sttStreaming = sttStreaming;
        this.tts = tts;
        this.videoRestrictedMode = videoRestrictedMode;
        this.audioRestrictedMode = audioRestrictedMode;
        this.localRecording = localRecording;
        this.language = language;
    }

    @Override
    public String toString() {
        return "Company{" +
            "companyCode=" + companyCode +
            ", workspaceId='" + workspaceId + '\'' +
            ", licenseName='" + licenseName + '\'' +
            ", sessionType='" + sessionType + '\'' +
            ", recording='" + recording + '\'' +
            ", storage='" + storage + '\'' +
            ", sttSync='" + sttSync + '\'' +
            ", sttStreaming='" + sttStreaming + '\'' +
            ", tts='" + tts + '\'' +
            '}';
    }
}