package com.virnect.workspace.dto.rest;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
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
public class RegisterMemberRequest {
	@NotBlank(message = "로그인 이메일 정보는 반드시 입력되어야 합니다.")
	private String email;
	@NotBlank(message = "로그인 비밀번호 정보는 반드시 입력되어야 합니다.")
	private String password;

	@Override
	public String toString() {
		return "RegisterMemberRequest{" +
			", email='" + email + '\'' +
			", password='" + password + '\'' +
			'}';
	}
}
