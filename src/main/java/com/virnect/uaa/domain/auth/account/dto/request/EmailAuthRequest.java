package com.virnect.uaa.domain.auth.account.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description
 * @since 2020.03.17
 */
@Getter
@Setter
@ApiModel
public class EmailAuthRequest {
	@NotBlank
	@Email
	@ApiModelProperty(value = "회원가입 인증용 이메일", example = "test@test.com", notes = "회원가입 시 입력한 이메일 정보로, 해당 이메일로 인증코드 메일이 발송됩니다.")
	private String email;

	@Override
	public String toString() {
		return "EmailAuthRequest{" +
			"email='" + email + '\'' +
			'}';
	}
}
