package com.virnect.content.domain;

import com.virnect.content.model.BaseTimeEntity;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.10
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
    @Enumerated(EnumType.STRING)
    private TargetType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "target")
    private List<TargetQRCode> targetQRCodeList;

    public void addTargetQRCode(TargetQRCode targetQRCode) {
        targetQRCode.setTarget(this);
        targetQRCodeList.add(targetQRCode);
    }

    @Builder
    public Target(TargetType type, Content content) {
        this.type = type;
        this.content = content;
        this.targetQRCodeList = new ArrayList<>();
    }
}
