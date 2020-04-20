package com.virnect.workspace.domain;

import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
@Table(name = "workspace_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Audited
public class WorkspaceUser extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_user_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId; // user uuid

    @ManyToOne(targetEntity = Workspace.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @NotAudited
    @OneToMany(mappedBy = "workspaceUser")
    private List<GroupUser> groupUserList = new ArrayList<>();

    @Builder
    public WorkspaceUser(String userId, Workspace workspace) {
        this.userId = userId;
        this.workspace = workspace;
    }
}
