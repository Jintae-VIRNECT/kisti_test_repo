package com.virnect.workspace.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Setter
@Getter
@RequiredArgsConstructor
public class WorkspaceInfoResponse {
    private final long countUsers;
}
