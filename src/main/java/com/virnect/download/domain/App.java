package com.virnect.download.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class App extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "url", nullable = false, unique = true)
    private String url;

    @Column(name = "version", nullable = false)
    private String version;

    @Column(name = "download_count",precision = 0)
    private Long downloadCount;

}
