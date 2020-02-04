package com.virnect.workspace.global.constant;

import lombok.Getter;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-04
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public enum Permission {
    //workspace, group
    ALL(1L),
    MEMBER_MANAGEMENT(2L),
    GROUP_MANAGEMENT(3L),
    CONTENTS_MANAGEMENT(4L),
    PROCESS_MANAGEMENT(5L);

    @Getter
    private long value;

    Permission(long value) {
        this.value = value;
    }
}
