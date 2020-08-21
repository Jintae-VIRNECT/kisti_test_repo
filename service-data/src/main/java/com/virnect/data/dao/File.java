package com.virnect.data.dao;


import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

//@DynamicUpdate
@Entity
@Getter
@Setter
@Audited
@Table(name = "files")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(name = "workspace_id", nullable = false)
    private String workspaceId;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "path", nullable = false, unique = true)
    private String path;

    @Column(name = "size", nullable = false)
    private Long size;

    //@Lob
    //@Enumerated(EnumType.STRING)
    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "download_hits")
    private Long downloadHits;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private Type type;*/


    @Builder
    public File(
            String workspaceId,
            String sessionId,
            String uuid,
            String name,
            String path,
            Long size,
            boolean deleted
            ) {
        this.workspaceId = workspaceId;
        this.sessionId = sessionId;
        this.uuid = uuid;
        this.name = name;
        this.path = path;
        this.size = size;
        this.deleted = deleted;

        //
        this.downloadHits = 0L;
    }
}
