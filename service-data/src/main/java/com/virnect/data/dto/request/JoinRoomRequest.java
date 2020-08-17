package com.virnect.data.dto.request;

import com.virnect.data.dao.DeviceType;
import com.virnect.data.dao.MemberType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ApiModel
public class JoinRoomRequest {
    @NotBlank
    @ApiModelProperty(value = "사용자 식별자")
    private String uuid;

    @NotBlank
    @ApiModelProperty(value = "사용자 이메일", position = 1)
    private String email;

    @ApiModelProperty(value = "멤버 타입 ( LEADER(방장), EXPERT(전문가), WORKER(작업자) )", position = 2)
    private MemberType memberType;

    @ApiModelProperty( value = "장치 타입 " +
            "( UNKNOWN(알 수 없음), DESKTOP(PC), MOBILE(모바일), SMART_GLASSES(스마트 글라스), HOLOLENS(홀로렌즈) )",
            position = 3)
    private DeviceType deviceType;

    @Override
    public String toString() {
        return "JoinRoomRequest{" +
                "uuid='" + uuid + '\'' +
                "email='" + email + '\'' +
                "memberType='" + memberType + '\'' +
                "deviceType='" + deviceType + '\'' +
                '}';
    }

}
