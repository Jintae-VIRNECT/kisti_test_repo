package com.virnect.uaa.domain.auth.account.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @project: PF-Auth
 * @author: jeonghyeon.chang (johnmark)
 * @email: practice1356@gmail.com
 * @since: 2020.03.09
 * @description: User Login Request Dto
 */

@Getter
@Setter
@ApiModel
public class LoginRequest {
	@ApiModelProperty(value = "로그인 아이디", name = "email", example = "smic1")
	private String email;
	@ApiModelProperty(value = "로그인 비밀번호", position = 1, name = "password", example = "smic1234")
	private String password;
	@ApiModelProperty(value = "자동 로그인 여부", position = 2, name = "remeberMe", example = "false")
	private boolean rememberMe;

	@Override
	public String toString() {
		return "LoginRequest{" +
			"email='" + email + '\'' +
			", password='*******'" +
			", rememberMe=" + rememberMe +
			'}';
	}
}
