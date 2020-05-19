package com.virnect.download.domain;

import lombok.*;

import javax.persistence.*;

/**
 * Project: PF-Download
 * DATE: 2020-04-20
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Entity
@Getter
@Setter
@Table(name = "app")
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class App extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_id")
    private Long id;

    @Column(name = "app_url", nullable = false, unique = true)
    private String appUrl;

    @Column(name = "guide_url", nullable = false, unique = true)
    private String guideUrl;

    @Column(name = "product", nullable = false)
    @Enumerated(EnumType.STRING)
    private Product product;

    @Column(name = "version", nullable = false)
    private String version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "os_id", nullable = false)
    private Os os;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "app_download_count", columnDefinition  = "BIGINT default 0")
    private Long appDownloadCount;

    @Column(name = "guide_download_count", columnDefinition  = "BIGINT default 0")
    private Long guideDownloadCount;
}
