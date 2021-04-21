package com.virnect.uaa.domain.user.dto.response;

import java.time.LocalDateTime;

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
public class UserPasswordChangeResponse {
	@ApiModelProperty(value = "비밀번호 변경 여부", notes = "true 면 성공적으로 비밀번호가 변경된것입니다.", example = "true")
	private final boolean isChanged;
	@ApiModelProperty(value = "비밀번호가 변경된 이메일 정보", notes = "새 비밀번호가 설정된 계정의 이메일 정보입니다.", example = "test@test.com")
	private final String email;
	@ApiModelProperty(value = "비밀번호가 새로 설정된 날짜입니다.", notes = "비밀번호가 변경된 날짜 정보입니다.", example = "2020-01-20T14:05:30")
	private final LocalDateTime passwordChangedDate;
}
