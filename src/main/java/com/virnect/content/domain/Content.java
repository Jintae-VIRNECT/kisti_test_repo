package com.virnect.content.domain;

import com.virnect.content.model.BaseTimeEntity;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-21
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Content Data Domain Entity Class
 */
@Entity
@Getter
@Setter
@Audited
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "content")
public class Content extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "aruco", nullable = false)
    private Integer aruco;

    @Column(name = "user_uuid")
    private String userUUID;

    @Lob
    @Column(name = "metadata", nullable = false)
    private String metadata;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentStatus status = ContentStatus.WAIT;

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SceneGroup> sceneGroupList = new ArrayList<>();

    public void addSceneGroup(SceneGroup sceneGroup) {
        sceneGroup.setContent(this);
        this.sceneGroupList.add(sceneGroup);
    }

    @Builder
    public Content(final String name, final String path, final long size, final int aruco, final String userUUID, final String metadata, final String contentUUID) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.aruco = aruco;
        this.userUUID = userUUID;
        this.metadata = metadata;
        this.uuid = contentUUID;
    }
}
