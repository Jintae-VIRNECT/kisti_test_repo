package com.virnect.content.domain;

import com.virnect.content.model.BaseTimeEntity;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-22
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Scene Group Data Domain Class
 */
@Entity
@Getter
@Setter
@Audited
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "scene_group")
public class SceneGroup extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scene_group_id")
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "name")
    private String name;

    @Column(name = "job_total")
    private Integer jobTotal;

    @ManyToOne
    @JoinColumn(name = "content_id")
    private Content content;

    @Builder
    public SceneGroup(String uuid, int priority, String name, int jobTotal) {
        this.uuid = uuid;
        this.priority = priority;
        this.name = name;
        this.jobTotal = jobTotal;
    }
}
