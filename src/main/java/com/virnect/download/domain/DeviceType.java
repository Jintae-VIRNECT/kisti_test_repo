package com.virnect.download.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Project: PF-Download
 * DATE: 2020-07-23
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Entity
@Getter
@Setter
@Table(name = "device_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceType extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_type_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
}
