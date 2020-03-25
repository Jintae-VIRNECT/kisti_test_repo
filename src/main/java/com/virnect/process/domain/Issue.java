package com.virnect.process.domain;

import com.virnect.process.model.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

/**
 * Project: PF-SMIC_CUSTOM
 * DATE: 2020-02-04
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@Setter
@Entity
@Table(name = "issue")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Issue extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id")
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "photo_file_path")
    private String path;

    @Column(name = "worker_uuid")
    private String workerUUID;

    @Builder
    public Issue(String content) {
        this.content = content;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @Builder
    public Issue(String content, String path, String workerUUID, Job job) {
        this.content = content;
        this.path = path;
        this.workerUUID = workerUUID;
        this.job = job;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", path='" + path + '\'' +
                ", workerUUID='" + workerUUID + '\'' +
                '}';
    }
}
