package com.virnect.data.domain;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

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

    @Column(name = "restricted_mode", nullable = false)
    private boolean restrictedMode;

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
                   Boolean restrictedMode,
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
        this.restrictedMode = restrictedMode;

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
