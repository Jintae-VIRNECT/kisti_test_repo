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
@Table(name = "`groups`")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "color")
    private String color;

    @Column(name = "profile")
    private String profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @OneToMany(mappedBy = "group")
    private List<GroupUser> groupUsers = new ArrayList<>();

    @Builder
    public Group(String name, String description, String color, String profile) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.profile = profile;
    }
}
