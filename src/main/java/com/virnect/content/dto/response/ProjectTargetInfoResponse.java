package com.virnect.content.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.content.domain.TargetType;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-21
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class ProjectTargetInfoResponse {
	@ApiModelProperty(value = "프로젝트 타겟 타입", position = 0, example = "")
	private TargetType type;
	@ApiModelProperty(value = "프로젝트 타겟 가로 사이즈", position = 1, example = "")
	private long width = 0L;
	@ApiModelProperty(value = "프로젝트 타겟 세로 사이즈", position = 2, example = "")
	private long length = 0L;
	@ApiModelProperty(value = "프로젝트 타겟 경로", position = 3, example = "")
	private String path = "";
}
