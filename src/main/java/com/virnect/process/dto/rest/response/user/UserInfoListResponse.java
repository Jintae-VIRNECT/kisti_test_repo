package com.virnect.process.dto.rest.response.user;

import com.virnect.process.global.common.PageMetadataResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-SMIC_CUSTOM
 * @email practice1356@gmail.com
 * @description User Server Info List Response Dto Class
 * @since 2020.03.13
 */
@Getter
@Setter
public class UserInfoListResponse {
    private List<UserInfoResponse> userInfoList;
    private PageMetadataResponse pageMeta;
}
