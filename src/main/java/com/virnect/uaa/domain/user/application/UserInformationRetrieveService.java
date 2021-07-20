package com.virnect.uaa.domain.user.application;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import com.virnect.uaa.domain.user.dto.response.InviteUserInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserAccessHistoryResponse;
import com.virnect.uaa.domain.user.dto.response.UserDetailsInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoListOnlyResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoListResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;

public interface UserInformationRetrieveService {

	UserInfoListResponse searchUserInformation(String searchQuery, Pageable pageable, boolean isPaging);

	UserDetailsInfoResponse getUserDetailsInformationByUserAuthenticatedRequest(
		HttpServletRequest request, Authentication authentication
	);

	UserInfoResponse findUserInformationByUserUUID(String userUUID);

	UserAccessHistoryResponse getUserAccessHistoryByUserUUID(String userUUID, Pageable pageable);

	UserInfoListOnlyResponse getUserInformationListByUserUUIDArray(String[] userUUIDs);

	InviteUserInfoResponse getUserInformationByUserEmail(String email);
}
