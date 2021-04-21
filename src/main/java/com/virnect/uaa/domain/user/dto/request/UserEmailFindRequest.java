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
 * @since 2020.04.10
 */

@Getter
@Setter
@ApiModel
public class UserEmailFindRequest {
	@NotBlank
	@ApiModelProperty(value = "계정 이름", example = "길동")
	private String firstName;
	@NotBlank
	@ApiModelProperty(value = "계정 성", position = 1, example = "홍")
	private String lastName;
	@ApiModelProperty(value = "전화 번호", position = 2, example = "+82-01012341234")
	private String mobile;
	@ApiModelProperty(value = "복구 이메일", position = 3, example = "test@test.com")
	private String recoveryEmail;
}
