package com.virnect.uaa.domain.user.dto.request;

import javax.validation.constraints.NotBlank;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description Register Request With Details Info
 * @since 2020.04.09
 */
@Getter
@Setter
@ApiModel
public class RegisterDetailsRequest {
	@ApiModelProperty(value = "사용자 식별 번호")
	@NotBlank(message = "사용자 식별번호는 반드시 입력되어야 합니다.")
	private String uuid;

	@ApiModelProperty(value = "프로필 이미지", position = 1)
	private MultipartFile profile;

	@ApiModelProperty(value = "사용자 닉네임", position = 2, example = "욜라뽕따이!")
	private String nickname;

	@ApiModelProperty(value = "휴대전화번호", position = 3, example = "+82-010-1234-1234")
	private String mobile;

	@ApiModelProperty(value = "복구 이메일", position = 4, example = "recovery@test.com")
	private String recoveryEmail;

	@Override
	public String toString() {
		return "RegisterDetailsRequest{" +
			"uuid='" + uuid + '\'' +
			", profile=" + profile +
			", nickname='" + nickname + '\'' +
			", mobile='" + mobile + '\'' +
			", recoveryEmail='" + recoveryEmail + '\'' +
			'}';
	}
}
