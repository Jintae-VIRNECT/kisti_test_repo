package com.virnect.workspace.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class WorkspaceInviteMailRequest {
    @ApiModelProperty(value = "초대한 사용자 이름")
    @NotBlank(message = "초대한 마스터 또는 매니저 유저의 이름은 필수 값입니다.")
    private String requestUserNickname;//워스크페이스 마스터 닉네임

    @ApiModelProperty(value = "초대한 사용자 email")
    @NotBlank(message = "초대한 마스터 또는 매니저 유저의 UUID는 필수 값입니다.")
    private String requestUserEmail;//마스터 이메일 계정

    @ApiModelProperty(value = "초대받은 사용자 이름")
    @NotBlank(message = "초대한 마스터 또는 매니저 유저의 이름은 필수 값입니다.")
    private String responseUserNickname;//초대된 사용자 닉네임

    @ApiModelProperty(value = "초대한 사용자 email")
    @NotBlank(message = "초대한 마스터 또는 매니저 유저의 UUID는 필수 값입니다.")
    private String responseUserEmail;//초대된 사용자 이메일 계정

    @ApiModelProperty(value = "초대 수락 링크")
    @NotBlank(message = "초대 수락링크는 필수 값입니다.")
    private String acceptUrl;//초대 코드 담아서

    @ApiModelProperty(value = "초대 거절 링크")
    @NotBlank(message = "초대 거절링크는 필수 값입니다.")
    private String rejectUrl;

    @Valid
    private List<InviteInfo> inviteInfos;

    @Getter
    @Setter
    public static class InviteInfo {
        @ApiModelProperty(value = "초대된 사용자 이름")
        @NotBlank(message = "초대된 사람의 이름은 필수 값입니다.")
        private String inviteUserName;

        @ApiModelProperty(value = "초대된 사용자 이메일")
        @NotBlank(message = "초대된 사람의 이메일은 필수 값입니다.")
        private String inviteUserEmail;
    }

}
