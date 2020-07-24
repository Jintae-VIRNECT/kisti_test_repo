package com.virnect.content.dto.rest;

import com.virnect.content.global.common.PageMetadataResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ProcessManagement
 * @email practice1356@gmail.com
 * @description User Server Info Response Dto Class
 * @since 2020.03.13
 */
@Getter
@Setter
public class MemberListResponse {
    private List<MemberInfoDTO> memberInfoList;
    private PageMetadataResponse pageMeta;
}