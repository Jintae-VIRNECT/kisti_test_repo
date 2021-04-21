package com.virnect.uaa.infra.rest.workspace.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Project: PF-User
 * DATE: 2020-02-17
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@Setter
@NoArgsConstructor
public class WorkspaceInfoListResponse {
	private List<WorkspaceInfoResponse> workspaceList;
}
