package com.virnect.uaa.domain.user.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.uaa.infra.rest.workspace.dto.WorkspaceInfoResponse;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description
 * @since 2020.03.16
 */

@Getter
@RequiredArgsConstructor
public class UserDetailsInfoResponse {
	private final UserInfoResponse userInfo;
	private final List<WorkspaceInfoResponse> workspaceInfoList;
}
