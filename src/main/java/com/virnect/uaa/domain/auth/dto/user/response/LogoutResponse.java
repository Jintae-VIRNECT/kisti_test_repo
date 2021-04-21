package com.virnect.uaa.domain.auth.dto.user.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description
 * @since 2020.03.16
 */
@Getter
@RequiredArgsConstructor
@ApiModel
public class LogoutResponse {
	@ApiModelProperty(value = "로그아웃 처리 결과", notes = "true: 성공, false: 실패", example = "true")
	private final boolean result;
	@ApiModelProperty(value = "로그아웃 처리된 사용자 이메일", notes = "로그아웃 처리된 사용자 이메일", position = 1, example = "test@test.com")
	private final String email;
}
