package com.virnect.uaa.domain.user.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class GuestMemberDeleteRequest {
	@NotBlank(message = "마스터 사용자의 식별자 정보는 반드시 있어야 합니다.")
	@ApiModelProperty(value = "마스터 사용자의 식별자 정보", example = "498b1839dc29ed7bb2ee90ad6985c608")
	private String masterUUID;
	@NotBlank(message = "Guest 사용자의 식별자 정보는 반드시 있어야 합니다.")
	@ApiModelProperty(value = "Guest 사용자의 식별자 정보", position = 1)
	private String guestUserUUID;


	@Override
	public String toString() {
		return "GuestMemberDeleteRequest{" +
				"masterUUID='" + masterUUID + '\'' +
				", guestUserUUID='" + guestUserUUID + '\'' +
				'}';
	}
}
