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
@RequiredArgsConstructor
@ApiModel
public class UserPasswordFindCodeCheckResponse {
	@ApiModelProperty(value = "사용자 식별 번호입니다.", example = "498b1839dc29ed7bb2ee90ad6985c608")
	private final String uuid;
	@ApiModelProperty(value = "사용자 이메일 정보입니다.", position = 1, example = "test@test.com")
	private final String email;
	@ApiModelProperty(value = "인증 결과 정보입니다.", position = 2, notes = "true 면 인증 완료", example = "true")
	private final boolean isAuthenticated;

	@Override
	public String toString() {
		return "UserPasswordFindCodeCheckResponse{" +
			"uuid='" + uuid + '\'' +
			", email='" + email + '\'' +
			", isAuthenticated=" + isAuthenticated +
			'}';
	}
}
