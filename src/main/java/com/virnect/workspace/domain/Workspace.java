package com.virnect.workspace.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "description")
    private String description;

    @Column(name = "user_id", nullable = false)
    private String userId; // user uuid

    @OneToMany(mappedBy = "workspace")
    List<Group> groupList = new ArrayList<>();

    @OneToMany(mappedBy = "workspace")
    List<WorkspaceUser> workspaceUserList = new ArrayList<>();
}
