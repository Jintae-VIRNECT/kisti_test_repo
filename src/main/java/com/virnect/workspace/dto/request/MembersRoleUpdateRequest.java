package com.virnect.workspace.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Project: PF-Workspace
 * DATE: 2020-03-02
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class MembersRoleUpdateRequest {
    @NotNull
    private String masterUserId;
    @NotNull
    private String uuid;
    @NotNull
    private String role;

}
