package com.virnect.workspace.domain;

import lombok.*;

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
@Table(name = "group_User")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupUser extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_user_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "favorite")
    private Favorite favorite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_user_id")
    private WorkspaceUser workspaceUser;

    @OneToMany(mappedBy = "groupUser")
    List<GroupUserPermission> permissions = new ArrayList<>();

    @Builder
    public GroupUser(Favorite favorite, Group group, WorkspaceUser workspaceUser){
        this.favorite = favorite;
        this.group = group;
        this.workspaceUser = workspaceUser;
    }
}
