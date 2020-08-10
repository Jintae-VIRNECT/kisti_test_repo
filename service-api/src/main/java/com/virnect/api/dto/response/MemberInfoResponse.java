package com.virnect.api.dto.response;

import com.virnect.api.dao.DeviceType;
import com.virnect.api.dao.MemberStatus;
import com.virnect.api.dao.MemberType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class MemberInfoResponse {
    @ApiModelProperty(value = "사용자 식별자", example = "498b1839dc29ed7bb2ee90ad6985c608")
    private String uuid;

    @ApiModelProperty(value = "사용자 이메일", position = 1, example = "example@remote.com")
    private String email;

    @ApiModelProperty(value = "사용자 이름", position = 3, example = "길동")
    private String firstName;

    @ApiModelProperty(value = "사용자 이름 성", position = 4, example = "홍")
    private String lastName;

    @ApiModelProperty(value = "사용자 닉네임", position = 5, example = "리모트 데모")
    private String nickname;

    @ApiModelProperty(value = "사용자 프로필 이미지 URL", position = 6, example = "url")
    private String profile;

    /*@ApiModelProperty(value = "휴대폰 번호", position = 9, example = "+82-010-1234-1234")
    private String mobile;*/

    @ApiModelProperty(value = "멤버 타입 ( LEADER(방장), EXPERT(전문가), WORKER(작업자) )", position = 8)
    private MemberType memberType;

    @ApiModelProperty(
            value = "장치 타입 " +
                    "( UNKNOWN(알 수 없음), DESKTOP(PC), MOBILE(모바일), SMART_GLASSES(스마트 글라스), HOLOLENS(홀로렌즈) )",
            position = 9)
    private DeviceType deviceType;

    @ApiModelProperty(
            value = "멤버 상태 타입 " +
                    "( ONLINE(로그인), OFFLINE(로그아웃), LOAD(참여 중), UNLOAD(미 참여 중), LOADING(접속 중) )",
            position = 10)
    private MemberStatus memberStatus;

    /*@ApiModelProperty(value = "계정 생성 일자", position = 11, example = "2020-01-20T14:05:30")
    private LocalDateTime createdDate;

    @ApiModelProperty(value = "최종 계정 정보 수정 일자", position = 12, example = "2020-01-20T14:05:30")
    private LocalDateTime updatedDate;*/

    @Override
    public String toString() {
        return "MemberInfoResponse{" +
                "uuid='" + uuid + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", nickname='" + nickname + '\'' +
                ", profile='" + profile + '\'' +
                ", memberType=" + memberType +
                '}';
    }

    /*@Override
    public String toString() {
        return "UserInfoResponse{" +
                "uuid='" + uuid + '\'' +
                ", email='" + email + '\'' +
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
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }*/
}
