package com.virnect.uaa.domain.user.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Project: PF-User
 * DATE: 2020-02-25
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */

@Getter
@RequiredArgsConstructor
public class InviteUserInfoResponse {
	@ApiModelProperty(value = "회원 유저 여부")
	private final boolean isMemberUser;
	@ApiModelProperty(value = "초대될 사용자들의 정보가 담긴 배열")
	private final InviteUserDetailInfoResponse inviteUserDetailInfo;
}
