package com.virnect.uaa.domain.user.dto.request;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class UserSecessionRequest {
	@NotBlank(message = "사용자 이메일 정보는 반드시 입력되어야합니다.")
	@ApiModelProperty(value = "탈퇴 시도 회원의 이메일 정보", required = true, example = "test@test.com")
	private String email;
	@NotBlank(message = "사용자 식별자 정보는 반드시 입력되어야합니다.")
	@ApiModelProperty(value = "탈퇴 시도 회원의 식별자 정보", position = 1, required = true, example = "asdknlk12n3")
	private String uuid;
	@ApiModelProperty(value = "탈퇴 시도 회원의 비밀번호 정보", position = 2, required = true, example = "1234")
	@NotBlank(message = "비밀번호는 반드시 입력되어야합니다.")
	private String password;
	@NotBlank(message = "탈퇴 사유는 반드시 입력되어야합니다.")
	@ApiModelProperty(value = "탈퇴 시도 회원의 탈퇴 사유", position = 3, required = true, example = "마우스가 미끄러졌습니다 ㅜ")
	private String reason;
	@NotNull(message = "탈퇴 정책 동의 여부 정보는 반드시 있어야합니다.")
	@ApiModelProperty(value = "탈퇴 정책 동의 여부 (true or false)", position = 4, required = true, example = "true")
	@AssertTrue(message = "탈퇴 정책은 반드시 동의되어야합니다.")
	private boolean isPolicyAssigned;

	@Override
	public String toString() {
		return "UserSecessionRequest{" +
			"email='" + email + '\'' +
			", uuid='" + uuid + '\'' +
			", password='" + "**********" + '\'' +
			", reason='" + reason + '\'' +
			", isPolicyAssigned=" + isPolicyAssigned +
			'}';
	}
}
