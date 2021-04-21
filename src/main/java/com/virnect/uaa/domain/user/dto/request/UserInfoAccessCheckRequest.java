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
 * @since 2020.04.07
 */

@Getter
@Setter
@ApiModel
public class UserInfoAccessCheckRequest {
	@NotBlank
	@ApiModelProperty(value = "계정 이메일 아이디", example = "smic1")
	private String email;
	@NotBlank
	@ApiModelProperty(value = "계정 비밀번호", position = 1, example = "smic1234")
	private String password;
}
