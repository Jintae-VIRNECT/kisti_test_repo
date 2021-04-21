package com.virnect.uaa.domain.user.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.13
 */

@Getter
@Setter
@ApiModel
public class UserPasswordFindAuthCodeRequest {
	@NotBlank(message = "이메일 정보는 반드시 입력되어야 합니다.")
	@ApiModelProperty(value = "회원가입시 입력된 이메일 주소", notes = "비밀번호 재설정 코드가 전송될 이메일 정보 입니다.", example = "test@test.com")
	private String email;

	@Override
	public String toString() {
		return "UserPasswordFindAuthCodeRequest{" +
			"email='" + email + '\'' +
			'}';
	}
}
