package com.virnect.content.domain;

import com.virnect.content.model.BaseTimeEntity;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

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
@Table(name = "target_qr")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TargetQRCode extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "target_qr_id")
    private Long id;

    @Column(name = "data")
    private String data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private Target target;

    @Builder
    public TargetQRCode(String data, Target target) {
        this.data = data;
        this.target = target;
    }
}
