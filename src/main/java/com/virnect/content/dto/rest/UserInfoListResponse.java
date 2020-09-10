package com.virnect.content.dto.rest;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.virnect.content.global.common.PageMetadataResponse;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
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
