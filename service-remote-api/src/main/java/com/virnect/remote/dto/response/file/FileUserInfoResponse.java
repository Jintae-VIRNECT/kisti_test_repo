package com.virnect.remote.dto.response.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class FileUserInfoResponse {
    @ApiModelProperty(value = "User e-mail", position = 2, example = "example@remote.com")
    private String email;

    @ApiModelProperty(value = "User name", position = 3, example = "유저 이름")
    private String name;

    @ApiModelProperty(value = "User Nick Name", position = 4, example = "유저 닉네임")
    private String nickname;

    @ApiModelProperty(value = "User Profile image URL", position = 5, example = "url")
    private String profile;

    @Override
    public String toString() {
        return "FileUserInfoResponse{" +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", profile='" + profile + '\'' +
                '}';
    }
}
