package com.virnect.uaa.domain.user.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Project: PF-User
 * DATE: 2020-02-17
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class UserInfoListResponse {
	private final List<UserInfoResponse> userInfoList;
	private final PageMetadataResponse pageMeta;
}
