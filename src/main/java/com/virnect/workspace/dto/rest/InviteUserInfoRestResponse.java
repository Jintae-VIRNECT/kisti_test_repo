package com.virnect.workspace.dto.rest;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-25
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class InviteUserInfoRestResponse {
    private final List<InviteUserInfoResponse> inviteUserInfoList;

    @Getter
    @RequiredArgsConstructor
    public class InviteUserInfoResponse {
        @ApiModelProperty(value = "사용자 식별자", position = 0, example = "498b1839dc29ed7bb2ee90ad6985c608")
        private final String userUUID;

        @ApiModelProperty(value = "사용자 이메일", position = 1, example = "test@test.com")
        private final String email;

        @ApiModelProperty(value = "사용자 이름", position = 2, example = "홍길동")
        private final String name;
    }
}
