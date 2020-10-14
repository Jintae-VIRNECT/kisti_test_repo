package com.virnect.workspace.dto.rest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-Workspace
 * DATE: 2020-10-14
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@ApiModel
public class UserInfoAccessCheckRequest {
	@ApiModelProperty(value = "계정 이메일 아이디", example = "smic1")
	private String email;
	@ApiModelProperty(value = "계정 비밀번호", position = 1, example = "smic1234")
	private String password;
}
