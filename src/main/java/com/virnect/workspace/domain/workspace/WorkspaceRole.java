package com.virnect.workspace.domain.workspace;

import com.virnect.workspace.domain.TimeEntity;
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
@Table(name = "workspace_role")
@Audited
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
}
