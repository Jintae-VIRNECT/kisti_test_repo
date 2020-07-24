package com.virnect.process.dto.rest.response.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ProcessManagement
 * @email practice1356@gmail.com
 * @description User Server Info Response Dto Class
 * @since 2020.03.13
 */
@Getter
@Setter
public class UserInfoResponse {
    @ApiModelProperty(value = "사용자 식별자", example = "498b1839dc29ed7bb2ee90ad6985c608")
    private String uuid;
    @ApiModelProperty(value = "사용자 이메일", position = 1, example = "smic1")
    private String email;
    @ApiModelProperty(value = "사용자 이름", position = 2, example = "SMIC Master")
    private String name;
    @ApiModelProperty(value = "닉네임",  position = 3, example = "지렁이")
    private String nickname;
    @ApiModelProperty(value = "사용자 소개", position = 4, example = "smic 워크스페이스 유저")
    private String description;
    @ApiModelProperty(value = "사용자 프로필 이미지 URL", position = 5, example = "smic 워크스페이스 유저")
    private String profile;
    @ApiModelProperty(value = "계정 잠금 여부 ( ACTIVE(잠금), INACTIVE(해제) )", position = 6, example = "smic 워크스페이스 유저")
    private String loginLock;
    @ApiModelProperty(value = "사용자 타입 ( USER(일반), SUB_USER(서브유저), VIRNECT_USER(사내 유저) )", position = 7, example = "smic 워크스페이스 유저")
    private String userType;
    @ApiModelProperty(value = "계정 생성 일자", position = 8, example = "2020-01-20T14:05:30")
    private LocalDateTime createdDate;
    @ApiModelProperty(value = "최종 계정 정보 수정 일자", position = 9, example = "2020-01-20T14:05:30")
    private LocalDateTime updatedDate;
    @ApiModelProperty(value = "로그인 QR 코드 (임시)", position = 10)
    private String qrCode;

    @Override
    public String toString() {
        return "UserInfoResponseDto{" +
                "uuid='" + uuid + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", nickName='" + nickname + '\'' +
                ", description='" + description + '\'' +
                ", profile='" + profile + '\'' +
                ", loginLock=" + loginLock +
                ", userType=" + userType +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }
}