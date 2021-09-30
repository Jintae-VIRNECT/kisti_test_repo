package com.virnect.uaa.infra.rest.user.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.virnect.uaa.infra.rest.workspace.dto.WorkspaceInfoResponse;

@Getter
@Setter
public class UserDetailsInfoResponse {
	private UserInfoResponse userInfo;
	private List<WorkspaceInfoResponse> workspaceInfoList;
}
