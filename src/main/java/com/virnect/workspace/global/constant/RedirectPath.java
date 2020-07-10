package com.virnect.workspace.global.constant;

import lombok.Getter;

/**
 * Project: PF-Workspace
 * DATE: 2020-07-10
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public enum RedirectPath {
    WORKSPACE_OVER_JOIN_FAIL("/?message=members.add.message.workspaceOverflow"),
    WORKSPACE_OVER_MAX_USER_FAIL("/?message=members.add.message.memberOverflow"),
    WORKSPACE_OVER_PLAN_FAIL("/?message=members.add.message.enoughPlan");

    @Getter
    private String value;

    RedirectPath(String value) {
        this.value = value;
    }
}
