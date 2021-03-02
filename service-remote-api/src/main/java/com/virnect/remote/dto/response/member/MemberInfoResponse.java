package com.virnect.remote.dto.response.member;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.data.domain.DeviceType;
import com.virnect.data.domain.member.MemberStatus;
import com.virnect.data.domain.member.MemberType;

@Getter
@Setter
@ApiModel
public class MemberInfoResponse {
    @ApiModelProperty(value = "User Unique Identifier", example = "498b1839dc29ed7bb2ee90ad6985c608")
    private String uuid = "";

    @ApiModelProperty(value = "User e-mail", position = 2, example = "example@remote.com")
    private String email = "";

    @ApiModelProperty(value = "User name", position = 3, example = "홍길동")
    private String name = "";

    @ApiModelProperty(value = "User Nick Name", position = 4, example = "리모트 데모")
    private String nickName = "";

    @ApiModelProperty(value = "User Profile image URL", position = 5, example = "url")
    private String profile = "";

    @ApiModelProperty(
            value = "Member Type ( UNKNOWN, LEADER, EXPERT )",
            position = 6)
    private MemberType memberType = MemberType.UNKNOWN;

    @ApiModelProperty(
            value = "User Permission ( MASTER, MANAGER, MEMBER )",
            position = 7)
    private String role = "MEMBER";

    /*@ApiModelProperty(
            value = "User Permission Id ( MASTER(1), MANAGER(2), MEMBER(3) )",
            position = 8)
    private Long roleId = 0L;*/

    @ApiModelProperty(
            value = "장치 타입 " +
                    "( UNKNOWN(알 수 없음), DESKTOP(PC), MOBILE(모바일), SMART_GLASSES(스마트 글라스), HOLOLENS(홀로렌즈) )",
            position = 9)
    private DeviceType deviceType = DeviceType.UNKNOWN;

    @ApiModelProperty(
            value = "Member Status ( LOAD(참여 중), UNLOAD(미 참여 중), EVICTED(퇴출 됨) )",
            position = 10)
    private MemberStatus memberStatus = MemberStatus.UNLOAD;

    @Override
    public String toString() {
        return "MemberInfoResponse{" +
                "uuid='" + uuid + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                //", roleId='" + roleId + '\'' +
                ", name='" + name + '\'' +
                ", nickname='" + nickName + '\'' +
                ", profile='" + profile + '\'' +
                ", memberType=" + memberType +
                '}';
    }
}
