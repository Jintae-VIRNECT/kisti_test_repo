package com.virnect.uaa.domain.user.application;

import org.springframework.data.domain.Pageable;

import com.virnect.uaa.domain.user.dto.response.UserInfoListResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;

public interface InternalUserService {
	UserInfoListResponse findAllUserInfo(Pageable pageable);

	UserInfoListResponse getUsersInfoList(String search, String[] workspaceUserIdList);

	UserInfoResponse getUserInfoByUserId(long userId);

}
