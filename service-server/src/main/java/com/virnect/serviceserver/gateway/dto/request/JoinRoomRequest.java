package com.virnect.serviceserver.gateway.dto.request;

import com.virnect.serviceserver.gateway.domain.MemberType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class JoinRoomRequest {
    @NotBlank
    @ApiModelProperty(value = "사용자 식별자")
    private String uuid;

    @NotBlank
    @ApiModelProperty(value = "사용자 이메일", position = 1)
    private String email;

    @ApiModelProperty(value = "멤버 타입 ( LEADER(방장), EXPERT(전문가), WORKER(작업자) )", position = 2)
    private MemberType memberType;

    @Override
    public String toString() {
        return "JoinRoomRequest{" +
                "uuid='" + uuid + '\'' +
                "memberType='" + memberType + '\'' +
                '}';
    }

}
