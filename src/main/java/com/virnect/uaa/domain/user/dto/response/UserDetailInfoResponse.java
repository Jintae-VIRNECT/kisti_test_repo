package com.virnect.uaa.domain.user.dto.response;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
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
@RequiredArgsConstructor
@Getter
public class UserDetailInfoResponse {
	@ApiModelProperty(value = "사용자 계정 정보")
	private final UserInfoResponse userInfo;
	@ApiModelProperty(value = "사용자 소속 워크스페이스 정보 리스트")
	private final List<WorkspaceInfoResponse> workspaceInfoList;
}
