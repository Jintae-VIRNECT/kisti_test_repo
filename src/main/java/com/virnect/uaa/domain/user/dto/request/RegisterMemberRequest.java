package com.virnect.uaa.domain.user.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class RegisterMemberRequest {
	@Pattern(regexp = "[a-zA-Z0-9]{4,20}")
	@NotBlank(message = "로그인 이메일 정보는 반드시 입력되어야 합니다.")
	private String email;
	@NotBlank(message = "로그인 비밀번호 정보는 반드시 입력되어야 합니다.")
	@Length(min = 8, max = 20)
	private String password;

	@Override
	public String toString() {
		return "RegisterMemberRequest{" +
			", email='" + email + '\'' +
			", password='" + password + '\'' +
			'}';
	}
}
