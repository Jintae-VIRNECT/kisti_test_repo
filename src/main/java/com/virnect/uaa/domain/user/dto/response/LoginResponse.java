package com.virnect.uaa.domain.user.dto.response;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import com.virnect.uaa.infra.rest.workspace.dto.WorkspaceInfoResponse;

/**
 * Project: PF-User
 * DATE: 2020-02-17
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@Setter
@RequiredArgsConstructor
public class LoginResponse {
	@ApiModelProperty(value = "사용자 계정 정보")
	private final UserInfoResponse userInfo;
	@ApiModelProperty(value = "사용자 소속 워크스페이스 정보 리스트")
	private final List<WorkspaceInfoResponse> workspaceInfoList;
}
