package com.virnect.process.dto.request;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-09-11
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@ToString
public class ProcessDeleteRequest {
	@NotNull
	@ApiModelProperty(value = "작업 식별자", notes = "작업 식별자", required = true, example = "1")
	private Long taskId;

	@NotNull
	@ApiModelProperty(value = "워크스페이스 식별자", notes = "", required = true, position = 2, example = "4d6eab0860969a50acbfa4599fbb5ae8")
	private String workspaceUUID = "";
}
