package com.virnect.content.domain;

import com.virnect.content.model.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

/**
 * Project: PF-SMIC_CUSTOM
 * DATE: 2020-02-04
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@Setter
@Entity
@Table(name = "device")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Device extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Builder
    public Device(String name) {
        this.name = name;
    }
}
