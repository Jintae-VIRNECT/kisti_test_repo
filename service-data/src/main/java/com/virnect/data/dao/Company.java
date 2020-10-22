package com.virnect.data.dao;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

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

    @Column(name = "translation", nullable = false)
    private boolean translation;

    @Column(name = "stt_sync", nullable = false)
    private boolean sttSync;

    @Column(name = "stt_streaming", nullable = false)
    private boolean sttStreaming;

    @Column(name = "trans_ko_kr", nullable = false)
    private boolean transKoKr;

    @Column(name = "trans_en_us", nullable = false)
    private boolean transEnUs;

    @Column(name = "trans_ja_jp", nullable = false)
    private boolean transJaJp;

    @Column(name = "trans_zh", nullable = false)
    private boolean transZh;

    @Column(name = "trans_fr_fr", nullable = false)
    private boolean transFrFr;

    @Column(name = "trans_es_es", nullable = false)
    private boolean transEsEs;

    @Column(name = "trans_ru_ru", nullable = false)
    private boolean transRuRu;

    @Column(name = "trans_uk_ua", nullable = false)
    private boolean transUkUa;

    @Column(name = "trans_pl_pl", nullable = false)
    private boolean transPlPl;


    @Builder
    public Company(int companyCode,
                   String workspaceId,
                   String licenseName,
                   SessionType sessionType,
                   Boolean translation,
                   Boolean sttSync,
                   Boolean sttStreaming,
                   Boolean transKoKr,
                   Boolean transEnUs,
                   Boolean transJaJp,
                   Boolean transZh,
                   Boolean transFrFr,
                   Boolean transEsEs,
                   Boolean transRuRu,
                   Boolean transUkUa,
                   Boolean transPlPl
                   ) {
        this.companyCode = companyCode;
        this.workspaceId = workspaceId;
        this.licenseName = licenseName;
        this.sessionType = sessionType;
        this.translation = translation;
        this.sttSync = sttSync;
        this.sttStreaming = sttStreaming;

        this.transKoKr = transKoKr;
        this.transEnUs = transEnUs;
        this.transJaJp = transJaJp;
        this.transZh = transZh;
        this.transFrFr = transFrFr;
        this.transEsEs = transEsEs;
        this.transRuRu = transRuRu;
        this.transUkUa = transUkUa;
        this.transPlPl = transPlPl;

    }

    @Override
    public String toString() {
        return "Company{" +
                "companyCode=" + companyCode +
                ", workspaceId='" + workspaceId + '\'' +
                ", licenseName='" + licenseName + '\'' +
                ", sessionType='" + sessionType + '\'' +
                ", sttSync='" + sttSync + '\'' +
                ", sttStreaming='" + sttStreaming + '\'' +
                ", transKoKr='" + transKoKr + '\'' +
                ", transEnUs='" + transEnUs + '\'' +
                ", transJaJp='" + transJaJp + '\'' +
                ", transZh='" + transZh + '\'' +
                ", transFrFr='" + transFrFr + '\'' +
                ", transEsEs='" + transEsEs + '\'' +
                ", transRuRu='" + transRuRu + '\'' +
                ", transUkUa='" + transUkUa + '\'' +
                ", transPlPl='" + transPlPl + '\'' +
                '}';
    }
}
