package com.virnect.uaa.domain.user.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public class MemberDeleteRequest {
	@NotBlank(message = "마스터 사용자의 식별자 정보는 반드시 있어야 합니다.")
	@ApiModelProperty(value = "마스터 사용자의 식별자 정보", example = "498b1839dc29ed7bb2ee90ad6985c608")
	private String masterUUID;
	@NotBlank(message = "워크스페이스 전용 사용자의 식별자 정보는 반드시 있어야 합니다.")
	@ApiModelProperty(value = "시트 사용자의 식별자 정보", position = 1)
	private String memberUserUUID;

	@Override
	public String toString() {
		return "MemberDeleteRequest{" +
			"masterUUID='" + masterUUID + '\'' +
			", memberUserUUID='" + memberUserUUID + '\'' +
			'}';
	}
}
