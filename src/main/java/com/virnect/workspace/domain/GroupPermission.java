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
@Table(name = "group_permission")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupPermission extends TimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_permission_id")
    private Long id;

    @Column(name = "permission", nullable = false)
    private String permission;

    @Lob
    @Column(name = "description")
    private String description;

    @Builder
    public GroupPermission(String permission){
        this.permission = permission;
    }
}
