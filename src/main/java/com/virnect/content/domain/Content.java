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

    @Column(name = "uuid", nullable = false, unique = true)
    private String uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "path", nullable = false, unique = true)
    private String path;

    @Column(name = "size", nullable = false)
    private Long size;

    // 공유여부 - 작업전환이 되어 있다면 항상 YES, 작업전환이 NO이지만 YES일 수 있고, 컨텐츠를 매뉴얼처럼 사용함.
    @Column(name = "shared", nullable = false)
    @Enumerated(EnumType.STRING)
    private YesOrNo shared;

    @Column(name = "user_uuid", nullable = false)
    private String userUUID;

    // 귀속된 워크스페이스
    @Column(name = "workspace_uuid", nullable = false)
    private String workspaceUUID;

    @Lob
    @Column(name = "metadata", nullable = false)
    private String metadata;

    @Lob
    @Column(name = "properties", nullable = false)
    private String properties;

    @Column(name = "deleted", nullable = false)
    @Enumerated(EnumType.STRING)
    private YesOrNo deleted = YesOrNo.NO;

    @Column(name = "converted", nullable = false)
    @Enumerated(EnumType.STRING)
    private YesOrNo converted = YesOrNo.NO;

    @Column(name = "download_hits")
    private Integer downloadHits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private Type type;

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Target> targetList = new ArrayList<>();

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SceneGroup> sceneGroupList = new ArrayList<>();

    public void addTarget(Target target) {
        target.setContent(this);
        this.targetList.add(target);
    }

    public void addSceneGroup(SceneGroup sceneGroup) {
        sceneGroup.setContent(this);
        this.sceneGroupList.add(sceneGroup);
    }

    @Builder
    public Content(String uuid, String name, String path, Long size, YesOrNo shared, String userUUID, String workspaceUUID, String metadata, String properties, YesOrNo deleted, YesOrNo converted, Type type, List<Target> targetList, List<SceneGroup> sceneGroupList) {
        this.uuid = uuid;
        this.name = name;
        this.path = path;
        this.size = size;
        this.shared = shared;
        this.userUUID = userUUID;
        this.workspaceUUID = workspaceUUID;
        this.metadata = metadata;
        this.properties = properties;
        this.deleted = deleted;
        this.converted = converted;
        this.type = type;
        this.targetList = new ArrayList<>();
        this.sceneGroupList = new ArrayList<>();
        this.downloadHits = 0;
    }
}
