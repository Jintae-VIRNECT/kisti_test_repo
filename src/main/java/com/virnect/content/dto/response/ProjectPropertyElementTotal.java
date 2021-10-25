package com.virnect.content.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-08-21
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class ProjectPropertyElementTotal {
	@ApiModelProperty(value = "씬 그룹 수", position = 2, example = "")
	private int sceneGroupTotal;
	@ApiModelProperty(value = "씬 수", position = 2, example = "")
	private int sceneTotal;
	@ApiModelProperty(value = "오브젝트 수", position = 2, example = "")
	private int objectTotal;
}
