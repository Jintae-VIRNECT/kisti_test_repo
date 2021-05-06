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
@Table(name = "record_files")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecordFile extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_file_id")
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

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "expiration_at")
    private LocalDateTime expirationDate;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "expired", nullable = false)
    private boolean expired;

    @Column(name = "download_hits")
    private Long downloadHits;

    @Column(name = "duration_sec", nullable = false)
    private Long durationSec;

    @Builder
    public RecordFile(
            String workspaceId,
            String sessionId,
            String uuid,
            String name,
            String objectName,
            String contentType,
            Long size,
            Long durationSec
    ) {
        init();
        this.workspaceId = workspaceId;
        this.sessionId = sessionId;
        this.uuid = uuid;
        this.name = name;
        this.objectName = objectName;
        this.contentType = contentType;
        this.size = size;
        this.durationSec = durationSec;
    }

    private void init() {
        // default setting
        this.downloadHits = 0L;
        this.deleted = false;
        this.expired = false;
        this.expirationDate = LocalDateTime.now().plusDays(7);
    }
}
