package com.virnect.uaa.domain.user.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.uaa.domain.user.domain.User;

/**
 * Project: PF-User
 * DATE: 2020-02-25
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class InviteUserDetailInfoResponse {
	@ApiModelProperty(value = "사용자 식별자", position = 0, example = "498b1839dc29ed7bb2ee90ad6985c608")
	private final String userUUID;
	@ApiModelProperty(value = "사용자 이메일", position = 1, example = "test@test.com")
	private final String email;
	@ApiModelProperty(value = "사용자 이름", position = 2, example = "홍길동")
	private final String name;
	@ApiModelProperty(value = "사용자 이름", position = 3, example = "길동")
	private final String firstName;
	@ApiModelProperty(value = "사용자 성", position = 4, example = "홍")
	private final String lastName;
	@ApiModelProperty(value = "사용자 닉네임", position = 5, example = "화려한 조명이 나를 감싸네")
	private final String nickname;

	public static InviteUserDetailInfoResponse ofUser(User user) {
		return new InviteUserDetailInfoResponse(
			user.getUuid(), user.getEmail(), user.getName(), user.getFirstName(),
			user.getLastName(), user.getNickname()
		);
	}

	public static InviteUserDetailInfoResponse getDummy() {
		return new InviteUserDetailInfoResponse("", "", "", "", "", "");
	}
}
