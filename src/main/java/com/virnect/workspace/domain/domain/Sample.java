package com.virnect.workspace.domain.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

/**
 * Project: base
 * DATE: 2020-01-07
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Entity
@Getter
public class Sample extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sample_id")
    private Long id;

    @Column(name = "name", length = 30)
    private String name;

    @Builder
    public Sample(final String name) {
        this.name = name;
    }
}
