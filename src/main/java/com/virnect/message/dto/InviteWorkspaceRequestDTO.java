package com.virnect.message.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * Project: PF-Message
 * DATE: 2020-02-14
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class InviteWorkspaceRequestDTO {
    @NotBlank
    private String requestUserName; //초대 한 사람 이름

    @NotBlank
    private String requestUserId; //초대 한 사람 uuid

    @NotBlank
    private String acceptUrl; //초대 수락 링크

    @NotBlank
    private String inviteCode; //초대 코드

    private List<Map<String, String>> inviteInfos; //초대 된 사람 이름, 이메일

}
