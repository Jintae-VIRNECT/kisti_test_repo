package com.virnect.data.dto.response;

import com.virnect.data.dao.DeviceType;
import com.virnect.data.dao.MemberStatus;
import com.virnect.data.dao.MemberType;
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

    @ApiModelProperty(value = "사용자 이메일", position = 2, example = "example@remote.com")
    private String email;

    @ApiModelProperty(value = "사용자 이름", position = 3, example = "홍길동")
    private String name;

    @ApiModelProperty(value = "사용자 닉네임", position = 4, example = "리모트 데모")
    private String nickName;

    @ApiModelProperty(value = "사용자 프로필 이미지 URL", position = 5, example = "url")
    private String profile;

    @ApiModelProperty(
            value = "멤버 타입 " +
                    "( UNKNOWN(알 수 없음), LEADER(방장), EXPERT(전문가), WORKER(작업자) )",
            position = 6)
    private MemberType memberType = MemberType.UNKNOWN;

    @ApiModelProperty(value = "사용자 등급 ( MASTER(마스터), MANAGER(매니저), MEMBER(멤버) )", position = 7)
    private String role;

    @ApiModelProperty(value = "사용자 등급 아이디 ( MASTER(1), MANAGER(2), MEMBER(3) )", position = 8)
    private Long roleId = 0L;

    @ApiModelProperty(
            value = "장치 타입 " +
                    "( UNKNOWN(알 수 없음), DESKTOP(PC), MOBILE(모바일), SMART_GLASSES(스마트 글라스), HOLOLENS(홀로렌즈) )",
            position = 9)
    private DeviceType deviceType = DeviceType.UNKNOWN;

    @ApiModelProperty(
            value = "멤버 상태 타입 " +
                    "( ONLINE(로그인), OFFLINE(로그아웃), LOAD(참여 중), UNLOAD(미 참여 중), LOADING(접속 중) )",
            position = 10)
    private MemberStatus memberStatus = MemberStatus.UNLOAD;

    @Override
    public String toString() {
        return "MemberInfoResponse{" +
                "uuid='" + uuid + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", roleId='" + roleId + '\'' +
                ", name='" + name + '\'' +
                ", nickname='" + nickName + '\'' +
                ", profile='" + profile + '\'' +
                ", memberType=" + memberType +
                '}';
    }
}
