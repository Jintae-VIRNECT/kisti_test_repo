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
@ApiModel
@RequiredArgsConstructor
public class UserProfileUpdateResponse {
	@ApiModelProperty(value = "사용자 식별자", example = "498b1839dc29ed7bb2ee90ad6985c608")
	private final String uuid;
	@ApiModelProperty(value = "프로필 이미지 url", example = "http://localhost:8081/users/upload/2020-04-07_ilzUZjnHMZqhoRpkqMUn.jpg")
	private final String profile;
}
