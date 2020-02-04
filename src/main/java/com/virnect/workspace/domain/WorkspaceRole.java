package com.virnect.workspace.domain;

import lombok.*;

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
@Table(name = "workspace_role")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkspaceRole extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_role_id")
    private Long id;

    @Column(name = "role", nullable = false)
    private String role;

    @Lob
    @Column(name = "description")
    private String description;

    @Builder
    public WorkspaceRole(Long id) {
        this.id = id;
    }
}
