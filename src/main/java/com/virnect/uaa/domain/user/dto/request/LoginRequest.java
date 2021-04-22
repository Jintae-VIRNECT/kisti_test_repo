package com.virnect.uaa.domain.user.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: user
 * DATE: 2020-01-08
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */

@Getter
@Setter
public class LoginRequest {
	@NotBlank(message = "로그인 아이디 정보는 반드시 입력되어야 합니다.")
	@ApiModelProperty(value = "로그인 아이디", required = true, example = "smic1")
	private String email;
	@NotBlank(message = "로그인 패스워드 정보는 반드시 입력되어야 합니다.")
	@ApiModelProperty(value = "로그인 비밀번호", required = true, example = "smic1234")
	private String password;
}
