package com.virnect.workspace.domain;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-08
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Entity
@Getter
@Setter
@Table(name = "workspace_permission")
@Audited
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkspacePermission extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_permission_id")
    private Long id;

    @Column(name = "permission", nullable = false)
    private String permission;

    @Lob
    @Column(name = "description")
    private String description;

    @Builder
    public WorkspacePermission(Long id) {
        this.id = id;
    }
}
