package com.virnect.download.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-18
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class WorkspaceInfoListResponse {
	private List<WorkspaceInfoResponse> workspaceList;
}
