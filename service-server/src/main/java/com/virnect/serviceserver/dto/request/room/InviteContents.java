package com.virnect.serviceserver.dto.request.room;

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
    @ApiModelProperty(value = "Remote Session Identifier", required = true)
    @NotBlank
    private String sessionId;

    @ApiModelProperty(value = "Invitee User Profile Url", example = "4ff0606102fbe", required = true, position = 1)
    @NotBlank
    private String profile;

    @ApiModelProperty(value = "Remote Session Title", example = "ROOM_INVITE", required = true, position = 2)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "Invitee Nickname", example = "4ff0606102fbe", required = true, position = 3)
    @NotBlank
    private String nickName;
}
