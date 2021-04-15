package com.virnect.download.domain;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Project: PF-Download
 * DATE: 2020-05-04
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Entity
@Getter
@Setter
@Table(name = "device")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Device extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Long id;

    @Column(name = "type", nullable = false)
    private String type; //MOBILE

    @Column(name = "type_description", nullable = false)
    private String typeDescription; //Google Play

    @Column(name = "model", nullable = false)
    private String model; //스마트폰/태블릿 PC

    @Column(name = "model_description", nullable = false)
    private String modelDescription; //<span style="color: #1468e2">스마트폰/타블릿</span>

    @Column(name = "model_description_eng", nullable = true)
    private String modelDescriptionEng;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
