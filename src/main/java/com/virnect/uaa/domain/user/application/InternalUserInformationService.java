package com.virnect.uaa.domain.user.application;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.virnect.uaa.domain.user.dto.response.UserInfoListResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;

public interface InternalUserInformationService {
	UserInfoListResponse findAllUserInfo(String search, Pageable pageable);

	UserInfoListResponse getUsersInfoList(String search, List<String> workspaceUserIdList);

	UserInfoResponse getUserInfoByUserId(long userId);

}
