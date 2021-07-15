package com.virnect.data.dto.request.room;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.data.domain.DeviceType;
import com.virnect.data.domain.member.MemberType;

@Getter
@Setter
@ApiModel
public class JoinRoomRequest {
    @NotBlank
    @ApiModelProperty(value = "User Unique Identifier")
    private String uuid;

    @ApiModelProperty(
            value = "Member Type ( LEADER, EXPERT )",
            example = "UNKNOWN",
            position = 1
    )
    private MemberType memberType;

    @ApiModelProperty(
            value = "Device Type ( UNKNOWN, DESKTOP(PC), MOBILE, SMART_GLASSES, HOLOLENS )",
            example = "MOBILE",
            position = 2
    )
    private DeviceType deviceType;

    @Override
    public String toString() {
        return "JoinRoomRequest{" +
                "uuid='" + uuid + '\'' +
                "memberType='" + memberType + '\'' +
                "deviceType='" + deviceType + '\'' +
                '}';
    }

}
