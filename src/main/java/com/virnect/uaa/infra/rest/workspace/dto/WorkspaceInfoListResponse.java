package com.virnect.uaa.infra.rest.workspace.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

	public static WorkspaceInfoListResponse emptyResponse() {
		WorkspaceInfoListResponse workspaceInfoListResponse = new WorkspaceInfoListResponse();
		workspaceInfoListResponse.setWorkspaceList(Collections.emptyList());
		return workspaceInfoListResponse;
	}

	public boolean isEmpty() {
		return workspaceList == null || workspaceList.isEmpty();
	}

	public List<String> getMasterPermissionWorkspaceUUIDList() {
		if (isEmpty()) {
			return new ArrayList<>();
		}
		return workspaceList.stream()
			.filter(w -> w.getRole().equals("MASTER"))
			.map(WorkspaceInfoResponse::getUuid)
			.collect(Collectors.toList());
	}

}
