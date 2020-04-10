package com.virnect.content.domain;

import com.virnect.content.model.BaseTimeEntity;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
@Audited
@Table(name = "target")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Target extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "target_id")
    private Long id;

    @Column(name = "type")
    private TargetType type;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "target")
    private List<TargetQRCode> targetQRCodeList;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "target")
    private List<Content> contentList;

    public void addTargetQRCode(TargetQRCode targetQRCode) {
        targetQRCode.setTarget(this);
        if (targetQRCodeList == null) targetQRCodeList = new ArrayList<>();
        targetQRCodeList.add(targetQRCode);
    }

    public void addContent(Content content) {
        content.setTarget(this);
        if (contentList == null) contentList = new ArrayList<>();
        contentList.add(content);
    }

    @Builder
    public Target(TargetType type, List<TargetQRCode> targetQRCodeList, List<Content> contentList) {
        this.type = type;
        this.targetQRCodeList = targetQRCodeList;
        this.contentList = contentList;
    }
}
