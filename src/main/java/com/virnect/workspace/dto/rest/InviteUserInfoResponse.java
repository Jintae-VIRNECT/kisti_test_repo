package com.virnect.workspace.dto.rest;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

/**
 * Project: PF-Workspace
 * DATE: 2020-11-18
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
public class InviteUserInfoResponse {
    @ApiModelProperty(value = "회원 유저 여부")
    private boolean isMemberUser;
    @ApiModelProperty(value = "초대될 사용자들의 정보가 담긴 배열")
    private InviteUserDetailInfoResponse inviteUserDetailInfo;
}
