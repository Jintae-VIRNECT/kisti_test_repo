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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Audited
@Table(name = "workspace")
public class Workspace extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workspace_id")
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true)
    private String uuid;

    @Column(name = "pin_number", length = 6, nullable = false, unique = true)
    private String pinNumber;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "profile")
    private String profile;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "user_id", nullable = false)
    private String userId; // user uuid

    @NotAudited
    @OneToMany(mappedBy = "workspace")
    List<Group> groupList = new ArrayList<>();

    @OneToMany(mappedBy = "workspace")
    List<WorkspaceUser> workspaceUserList = new ArrayList<>();

    @Builder
    public Workspace(String uuid, String pinNumber, String name, String profile, String description, String userId) {
        this.uuid = uuid;
        this.name = name;
        this.profile = profile;
        this.pinNumber = pinNumber;
        this.description = description;
        this.userId = userId;
    }
}
