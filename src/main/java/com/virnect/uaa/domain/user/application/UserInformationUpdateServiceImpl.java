package com.virnect.uaa.domain.user.application;

import org.springframework.stereotype.Service;

import com.virnect.uaa.domain.user.dto.request.AccessPermissionCheckRequest;
import com.virnect.uaa.domain.user.dto.request.ProfileImageUpdateRequest;
import com.virnect.uaa.domain.user.dto.request.UserInfoModifyRequest;
import com.virnect.uaa.domain.user.dto.request.UserSecessionRequest;
import com.virnect.uaa.domain.user.dto.response.UserInfoAccessCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserProfileUpdateResponse;
import com.virnect.uaa.domain.user.dto.response.UserSecessionResponse;

@Service
public class UserInformationUpdateServiceImpl
	implements UserInformationUpdateService {
	@Override
	public UserInfoAccessCheckResponse accessPermissionCheck(
		String userId,
		AccessPermissionCheckRequest accessPermissionCheckRequest
	) {
		return null;
	}

	@Override
	public UserProfileUpdateResponse profileImageUpdate(
		String userId, ProfileImageUpdateRequest profileImageUpdateRequest
	) {
		return null;
	}

	@Override
	public UserInfoResponse updateDetailInformation(
		String userUUID, UserInfoModifyRequest userInfoModifyRequest
	) {
		return null;
	}

	@Override
	public UserSecessionResponse accountSecession(
		UserSecessionRequest userSecessionRequest
	) {
		return null;
	}
}
