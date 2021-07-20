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
 * @since 2020.04.07
 */

@Getter
@RequiredArgsConstructor
@ApiModel
public class UserInfoAccessCheckResponse {
	@ApiModelProperty(value = "접근 권한 확인 결과", notes = "(true : 허용, false: 비허용)", example = "true")
	private final boolean accessCheckResult;
	@ApiModelProperty(value = "사용자 정보", notes = "accessCheckResult 가 true 인 경우에만 반환됨", position = 1)
	private final UserInfoResponse userInfo;

}
