package com.virnect.data.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@Setter
@ApiModel
public class InviteContents implements Serializable {
    @ApiModelProperty(value = "세션 ID", required = true)
    @NotBlank
    private String sessionId;

    @ApiModelProperty(value = "프로필", example = "4ff0606102fbe", required = true, position = 1)
    @NotBlank
    private String profile;

    @ApiModelProperty(value = "룸 타이", example = "ROOM_INVITE", required = true, position = 4)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "초대자 닉네임", example = "4ff0606102fbe", required = true, position = 2)
    @NotBlank
    private String nickName;

}
