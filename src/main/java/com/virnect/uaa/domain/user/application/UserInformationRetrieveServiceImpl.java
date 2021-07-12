package com.virnect.uaa.domain.user.application;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.virnect.uaa.domain.user.dto.response.InviteUserInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserAccessHistoryResponse;
import com.virnect.uaa.domain.user.dto.response.UserDetailsInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoListOnlyResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoListResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;

@Service
public class UserInformationRetrieveServiceImpl
	implements UserInformationRetrieveService {
	@Override
	public UserInfoListResponse searchUserInformation(
		String searchQuery, Pageable pageable,
		boolean isPaging
	) {
		return null;
	}

	@Override
	public UserDetailsInfoResponse getUserDetailsInformationByAuthentication(
		HttpServletRequest request,
		Authentication authentication
	) {
		return null;
	}

	@Override
	public UserInfoResponse getUserInformationByUserUUID(String userUUID) {
		return null;
	}

	@Override
	public UserAccessHistoryResponse getUserAccessHistoryByUserUUID(
		String userUUID,
		Pageable pageable
	) {
		return null;
	}

	@Override
	public UserInfoListOnlyResponse getUserInformationListByUserUUIDArray(
		String[] userUUIDs
	) {
		return null;
	}

	@Override
	public InviteUserInfoResponse getUserInformationByUserEmail(
		String email
	) {
		return null;
	}
}
