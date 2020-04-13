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

    @Column(name = "data")
    private String data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    @Builder
    public Target(TargetType type, String data, Content content) {
        this.type = type;
        this.data = data;
        this.content = content;
    }
}
