package com.virnect.message.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Project: PF-Message
 * DATE: 2020-02-14
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class InviteWorkspaceRequest {
    @ApiModelProperty(value = "초대한 사용자 이름")
    @NotBlank(message = "초대한 마스터 또는 매니저 유저의 이름은 필수 값입니다.")
    private String requestUserName;

    @ApiModelProperty(value = "초대한 사용자 uuid")
    @NotBlank(message = "초대한 마스터 또는 매니저 유저의 UUID는 필수 값입니다.")
    private String requestUserId;

    @ApiModelProperty(value = "초대 수락 링크")
    @NotBlank(message = "초대 수락링크는 필수 값입니다.")
    private String acceptUrl;

    @ApiModelProperty(value = "초대 코드")
    @NotBlank(message = "초대 코드는 필수 값입니다.")
    private String inviteCode;

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
