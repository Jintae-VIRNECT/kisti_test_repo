package com.virnect.workspace.global.constant;

import lombok.Getter;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-04
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public enum Role {
    //workspace, group
    MASTER(1L),
    MANAGER(2L),
    MEMBER(3L);

    @Getter
    private long value;

    Role(long value) {
        this.value = value;
    }
}
