package com.virnect.workspace.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * Project: PF-Workspace
 * DATE: 2020-04-20
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class MemberKickOutRequest {
    @NotBlank
    private String userId;
    @NotBlank
    private String kickedUserId;
}
