package com.virnect.process.domain;

import com.virnect.process.model.BaseTimeEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author hangkee.min (henry)
 * @project PF-ProcessManagement
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
    @JoinColumn(name = "process_id")
    private Process process;

    @Builder
    public Target(TargetType type, String data, Process process, String imgPath) {
        this.type = type;
        this.data = data;
        this.imgPath = imgPath;
        this.process = process;
    }

    @Override
    public String toString() {
        return "Target{" +
                "id=" + id +
                ", type=" + type +
                ", data='" + data + '\'' +
                ", imgPath='" + imgPath + '\'' +
//                ", process=" + process +  // 무한 toString 방지
                '}';
    }
}
