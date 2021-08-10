package com.virnect.data.domain;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Getter
@Setter
@Audited
@Entity
@Table(name = "language")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Language extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "language_id", nullable = false)
    private Long id;

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

    @Column(name = "trans_th_th", nullable = false)
    private boolean transThTh;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Builder
    public Language(
        Company company,
        Boolean transKoKr,
        Boolean transEnUs,
        Boolean transJaJp,
        Boolean transZh,
        Boolean transFrFr,
        Boolean transEsEs,
        Boolean transRuRu,
        Boolean transUkUa,
        Boolean transPlPl,
        Boolean transThTh
    ) {
<<<<<<< HEAD

=======
>>>>>>> feature/develop-backup
        this.company = company;
        this.transKoKr = transKoKr;
        this.transEnUs = transEnUs;
        this.transJaJp = transJaJp;
        this.transZh = transZh;
        this.transFrFr = transFrFr;
        this.transEsEs = transEsEs;
        this.transRuRu = transRuRu;
        this.transUkUa = transUkUa;
        this.transPlPl = transPlPl;
        this.transThTh = transThTh;
    }

    @Override
    public String toString() {
<<<<<<< HEAD
        return "Language{" +
            "id=" + id +
            ", transKoKr=" + transKoKr +
            ", transEnUs=" + transEnUs +
            ", transJaJp=" + transJaJp +
            ", transZh=" + transZh +
            ", transFrFr=" + transFrFr +
            ", transEsEs=" + transEsEs +
            ", transRuRu=" + transRuRu +
            ", transUkUa=" + transUkUa +
            ", transPlPl=" + transPlPl +
            ", transThTh=" + transThTh +
            ", company=" + company +
=======
        return "Company{" +
            "companyCode=" + company.getCompanyCode() +
            ", transKoKr='" + transKoKr + '\'' +
            ", transEnUs='" + transEnUs + '\'' +
            ", transJaJp='" + transJaJp + '\'' +
            ", transZh='" + transZh + '\'' +
            ", transFrFr='" + transFrFr + '\'' +
            ", transEsEs='" + transEsEs + '\'' +
            ", transRuRu='" + transRuRu + '\'' +
            ", transUkUa='" + transUkUa + '\'' +
            ", transPlPl='" + transPlPl + '\'' +
            ", transThTh='" + transThTh + '\'' +
>>>>>>> feature/develop-backup
            '}';
    }
}
