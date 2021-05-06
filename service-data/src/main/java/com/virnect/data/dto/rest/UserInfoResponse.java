package com.virnect.data.dto.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoResponse {
    @ApiModelProperty(value = "사용자 식별자", example = "498b1839dc29ed7bb2ee90ad6985c608")
    private String uuid;
    @ApiModelProperty(value = "사용자 이메일", position = 1, example = "smic1")
    private String email;
    @ApiModelProperty(value = "사용자 이름", notes = "사라질 필드 정보입니다", position = 2, example = "SMIC Master")
    private String name;
    @ApiModelProperty(value = "사용자 이름", position = 3, example = "길동")
    private String firstName;
    @ApiModelProperty(value = "사용자 이름 성", position = 4, example = "홍")
    private String lastName;
    @ApiModelProperty(value = "사용자 닉네임", position = 5, example = "마스터는 나다.")
    private String nickname;
    @ApiModelProperty(value = "사용자 소개", position = 6, example = "smic 워크스페이스 유저")
    private String description;
    @ApiModelProperty(value = "사용자 프로필 이미지 URL", position = 7, example = "smic 워크스페이스 유저")
    private String profile;
    @ApiModelProperty(value = "생년월일", position = 8, example = "2020-01-02")
    private LocalDate birth;
    @ApiModelProperty(value = "휴대폰 번호", position = 9, example = "+82-010-1234-1234")
    private String mobile;
    @ApiModelProperty(value = "복구 이메일", position = 10, example = "test@test.com")
    private String recoveryEmail;
    @ApiModelProperty(value = "계정 잠금 여부 ( ACTIVE(잠금), INACTIVE(해제) )", position = 11, example = "smic 워크스페이스 유저")
    private String loginLock; //enum from user-server
    @ApiModelProperty(value = "사용자 타입 ( USER(일반), SUB_USER(서브유저), VIRNECT_USER(사내 유저) )", position = 12, example = "smic 워크스페이스 유저")
    private String userType; //enum from user-server
    @ApiModelProperty(value = "마케팅 정보 수신 동의 여부", position = 13, example = "ACCEPT")
    private String marketInfoReceive; //enum from user-server
    @ApiModelProperty(value = "계정 생성 일자", position = 14, example = "2020-01-20T14:05:30")
    private LocalDateTime createdDate;
    @ApiModelProperty(value = "최종 계정 정보 수정 일자", position = 15, example = "2020-01-20T14:05:30")
    private LocalDateTime updatedDate;

    @Override
    public String toString() {
        return "UserInfoResponse{" +
                "uuid='" + uuid + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", nickname='" + nickname + '\'' +
                ", description='" + description + '\'' +
                ", profile='" + profile + '\'' +
                ", birth=" + birth +
                ", mobile='" + mobile + '\'' +
                ", recoveryEmail='" + recoveryEmail + '\'' +
                ", loginLock=" + loginLock +
                ", userType=" + userType +
                ", marketInfoReceive=" + marketInfoReceive +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }
}
