package com.virnect.process.dto.rest.response.workspace;

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

    @Override
    public String toString() {
        return "UserInfoResponseDto{" +
                "uuid='" + uuid + '\'' +
                '}';
    }
}
