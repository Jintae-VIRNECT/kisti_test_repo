package com.virnect.uaa.domain.user.dto.request;

import javax.validation.constraints.NotBlank;

import org.springframework.util.StringUtils;

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
public class EmailFindRequest {
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

	public String getFullName() {
		return lastName + firstName;
	}

	public boolean hasValidFindInformation() {
		if (StringUtils.isEmpty(mobile) && StringUtils.isEmpty(recoveryEmail)) {
			return false;
		}

		if (StringUtils.hasText(mobile) && mobile.split("-").length >= 3) {
			return false;
		}

		return !StringUtils.hasText(recoveryEmail) ||
			recoveryEmail.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
	}

	@Override
	public String toString() {
		return "EmailFindRequest{" +
			"firstName='" + firstName + '\'' +
			", lastName='" + lastName + '\'' +
			", mobile='" + mobile + '\'' +
			", recoveryEmail='" + recoveryEmail + '\'' +
			'}';
	}
}