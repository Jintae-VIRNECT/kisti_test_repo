package com.virnect.content.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.10
 */
@Slf4j
@Getter
@Setter
@Entity
@Audited
@Table(name = "target")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Target extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "target_id")
    private Long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TargetType type;

    @Column(name = "data", unique = true)
    private String data;

    @Column(name = "img_path")
    private String imgPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    @Column(name = "size")
    private Float size;

    @Builder
    public Target(TargetType type, String data, String imgPath, Content content, Float size) {
        this.type = type;
        this.data = data;
        this.imgPath = imgPath;
        this.content = content;
        this.size = size;
    }

    @Override
    public String toString() {
        return "Target{" +
                "id=" + id +
                ", type=" + type +
                ", data='" + data + '\'' +
                ", imgPath=" + imgPath + '\'' +
                ", size=" + size + '\'' +
                //                ", content=" + content +
                '}';
    }
}
