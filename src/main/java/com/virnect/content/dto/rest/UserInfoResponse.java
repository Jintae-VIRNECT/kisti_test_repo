package com.virnect.content.dto.rest;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-08
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */

@Getter
@Setter
@ToString
public class UserInfoResponse {
    @ApiModelProperty(value = "사용자 식별자", example = "498b1839dc29ed7bb2ee90ad6985c608")
    private String uuid;
    @ApiModelProperty(value = "사용자 이메일", position = 1, example = "virnect1")
    private String email;
    @ApiModelProperty(value = "사용자 이름", position = 2, example = "VIRNECT Master")
    private String name;
    @ApiModelProperty(value = "사용자 닉네임", position = 3, example = "지렁이")
    private String nickname;
    @ApiModelProperty(value = "사용자 소개", position = 3, example = "VIRNECT 워크스페이스 유저")
    private String description;
    @ApiModelProperty(value = "사용자 프로필 이미지 URL", position = 4, example = "VIRNECT 워크스페이스 유저")
    private String profile;
    @ApiModelProperty(value = "계정 잠금 여부 ( ACTIVE(잠금), INACTIVE(해제) )", position = 5, example = "VIRNECT 워크스페이스 유저")
    private String loginLock;
    @ApiModelProperty(value = "사용자 타입 ( USER(일반), SUB_USER(서브유저), VIRNECT_USER(사내 유저) )", position = 6, example = "VIRNECT 워크스페이스 유저")
    private String userType;
    @ApiModelProperty(value = "계정 생성 일자", position = 7, example = "2020-01-20T14:05:30")
    private LocalDateTime createdDate;
    @ApiModelProperty(value = "최종 계정 정보 수정 일자", position = 8, example = "2020-01-20T14:05:30")
    private LocalDateTime updatedDate;
    @ApiModelProperty(value = "로그인 QR 코드 (임시)", position = 9)
    private String qrCode;
}
