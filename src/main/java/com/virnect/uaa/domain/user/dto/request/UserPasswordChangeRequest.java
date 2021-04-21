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
public class UserPasswordChangeRequest {
	@NotBlank
	@ApiModelProperty(value = "새 비밀번호가 설정될 사용자의 식별 번호입니다.", notes = "인증코드 인증 결과로 받은 사용자 식별번호를 사용합니다.", example = "498b1839dc29ed7bb2ee90ad6985c608")
	private String uuid;

	@NotBlank
	@ApiModelProperty(value = "새 비밀번호가 설정될 사용자의 이메일 정보입니다.", notes = "인증코드 인증 결과로 받은 사용자 이메일 정보를 사용합니다.", position = 1, example = "test")
	private String email;

	@NotBlank
	@ApiModelProperty(value = "새 비밀번호", notes = "새로 설정할 비밀번호르 입력합니다.", position = 2, example = "test123456")
	private String password;

	@Override
	public String toString() {
		return "UserPasswordChangeRequest{" +
			"uuid='" + uuid + '\'' +
			", email='" + email + '\'' +
			", password='" + "*********" + '\'' +
			'}';
	}
}
