package com.virnect.workspace.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkspacePermission extends TimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_permission_id")
    private Long id;

    @Column(name = "permission", nullable = false)
    private String permission;

    @Lob
    @Column(name = "description")
    private String description;
}
