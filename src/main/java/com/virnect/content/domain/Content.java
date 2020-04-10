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

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "size", nullable = false)
    private Long size;

    // 공유여부
    @Column(name = "shared", nullable = false)
    @Enumerated(EnumType.STRING)
    private YesOrNo shared;

    @Column(name = "user_uuid", nullable = false)
    private String userUUID;

    // 귀속된 워크스페이스
    @Column(name = "workspace_uuid", nullable = false)
    private String workspaceUUID;

    // 타겟
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private Target target;

    // 타입 - 증강현실, 보조현실, 크로스플랫폼 등
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ContentType type;

    @Lob
    @Column(name = "metadata", nullable = false)
    private String metadata;

    @Column(name = "converted", nullable = false)
    @Enumerated(EnumType.STRING)
    private YesOrNo converted = YesOrNo.NO;

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SceneGroup> sceneGroupList = new ArrayList<>();

    public void addSceneGroup(SceneGroup productSceneGroup) {
        productSceneGroup.setContent(this);
        if (sceneGroupList == null) sceneGroupList = new ArrayList<>();
        this.sceneGroupList.add(productSceneGroup);
    }

    @Builder
    public Content(String uuid, String name, String path, Long size, YesOrNo shared, String userUUID, String workspaceUUID, Target target, ContentType type, String metadata, YesOrNo converted, List<SceneGroup> sceneGroupList) {
        this.uuid = uuid;
        this.name = name;
        this.path = path;
        this.size = size;
        this.shared = shared;
        this.userUUID = userUUID;
        this.workspaceUUID = workspaceUUID;
        this.target = target;
        this.type = type;
        this.metadata = metadata;
        this.converted = converted;
        this.sceneGroupList = sceneGroupList;
    }
}
