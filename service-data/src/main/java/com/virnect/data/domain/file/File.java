package com.virnect.data.domain.file;


import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;

import com.virnect.data.domain.BaseTimeEntity;

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

    @Column(name = "object_name", nullable = false, unique = true)
    private String objectName;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    /*@Column(name = "path", nullable = false, unique = true)
    private String path;*/

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "expiration_at")
    private LocalDateTime expirationDate;

    //@Lob
    //@Enumerated(EnumType.STRING)
    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "expired", nullable = false)
    private boolean expired;

    @Column(name = "download_hits")
    private Long downloadHits;

    @Column(name = "file_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "file_convert_status")
    @Enumerated(EnumType.STRING)
    private FileConvertStatus fileConvertStatus;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private Type type;*/


    @Builder
    public File(
        String workspaceId,
        String sessionId,
        String uuid,
        String name,
        String objectName,
        String contentType,
        Long size,
        FileType fileType,
        Integer width,
        Integer height,
        FileConvertStatus fileConvertStatus
    ) {
        this.workspaceId = workspaceId;
        this.sessionId = sessionId;
        this.uuid = uuid;
        this.name = name;
        this.objectName = objectName;
        this.contentType = contentType;
        this.size = size;
        this.fileType = fileType;
        this.width = width;
        this.height = height;
        // default setting
        this.downloadHits = 0L;
        this.deleted = false;
        this.expired = false;
        this.fileConvertStatus = fileConvertStatus;
        this.expirationDate = LocalDateTime.now().plusDays(7);
    }
}
