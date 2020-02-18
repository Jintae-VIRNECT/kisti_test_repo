package com.virnect.message.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Project: PF-Message
 * DATE: 2020-02-17
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class InviteWorkspaceResponse {
    @ApiModelProperty(value = "워크스페이스 초대 메일 발송 결과")
    private final boolean result;
}
