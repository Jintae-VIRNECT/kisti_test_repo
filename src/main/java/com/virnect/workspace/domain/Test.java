package com.virnect.workspace.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-04
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Embeddable
public class Test {

    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;
}
