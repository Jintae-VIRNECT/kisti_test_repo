package com.virnect.process.dto.rest.response.workspace;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-ProcessManagement
 * DATE: 2021-06-15
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Setter
@Getter
public class WorkspaceSettingInfoListResponse {
	@ApiModelProperty(value = "워크스페이스 설정 정보 목록", required = true, example = "")
	private List<WorkspaceSettingInfoResponse> workspaceSettingInfoList;
}
