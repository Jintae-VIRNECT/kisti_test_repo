package com.virnect.content.dto.rest;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jiyong.heo
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.06.03
 */
@Getter
@Setter
public class MemberInfoDTO {
    private String uuid;
    private String name;
    private String nickName;
    private String profile;
    private String role;

    @Override
    public String toString() {
        return "UserInfoResponseDto{" +
                "uuid='" + uuid + '\'' +
                "uuid='" + name + '\'' +
                "uuid='" + nickName + '\'' +
                "uuid='" + profile + '\'' +
                "uuid='" + role + '\'' +
                '}';
    }
}
