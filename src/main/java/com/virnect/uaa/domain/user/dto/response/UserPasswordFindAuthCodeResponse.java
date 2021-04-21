package com.virnect.uaa.domain.user.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.13
 */

@Getter
@ApiModel
@RequiredArgsConstructor
public class UserPasswordFindAuthCodeResponse {
	@ApiModelProperty(value = "비밀번호 재설정 코드 전송 결과", notes = "비밀번호 재설정 코드가 성공적으로 전송되었는지에 대한 값입니다.", example = "true")
	private final boolean isAuthenticated;
	@ApiModelProperty(value = "비밀번호 재설정 코드가 전송된 이메일 정보 입니다.", position = 1, example = "test@test.com")
	private final String email;

	@Override
	public String toString() {
		return "UserPasswordFindAuthCodeResponse{" +
			"isAuthenticated=" + isAuthenticated +
			", email='" + email + '\'' +
			'}';
	}
}
