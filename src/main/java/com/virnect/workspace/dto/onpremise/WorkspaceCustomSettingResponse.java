package com.virnect.workspace.dto.onpremise;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-Workspace
 * DATE: 2020-10-15
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@ApiModel
public class WorkspaceCustomSettingResponse {
	@ApiModelProperty(value = "워크스페이스 식별자", example = "4d6eab0860969a50acbfa4599fbb5ae8", position = 0)
	private String worksapceId;

	@ApiModelProperty(value = "워크스페이스 고객사명", example = "VIRNECT", position = 1)
	private String workspaceTitle = "";

	@ApiModelProperty(value = "워크스페이스 기본 로고", example = "", position = 2)
	private String defaultLogo = "";

	@ApiModelProperty(value = "워크스페이스 기본 로고", example = "", position = 3)
	private String greyLogo = "";

	@ApiModelProperty(value = "워크스페이스 기본 로고", example = "", position = 4)
	private String whiteLogo = "";

	@ApiModelProperty(value = "워크스페이스 파비콘", example = "", position = 4)
	private String pavicon = "";
}

