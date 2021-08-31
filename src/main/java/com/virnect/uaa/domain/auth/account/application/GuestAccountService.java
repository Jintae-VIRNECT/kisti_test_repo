package com.virnect.uaa.domain.auth.account.application;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.dto.ClientGeoIPInfo;
import com.virnect.uaa.domain.auth.account.dto.request.GuestAccountAllocateRequest;
import com.virnect.uaa.domain.auth.account.dto.response.GuestAccountInfoResponse;
import com.virnect.uaa.domain.auth.account.dto.response.SeatUserStat;
import com.virnect.uaa.domain.auth.common.error.AuthenticationErrorCode;
import com.virnect.uaa.domain.auth.common.exception.UserAuthenticationServiceException;
import com.virnect.uaa.domain.auth.websocket.ClientSessionInfo;
import com.virnect.uaa.domain.auth.websocket.session.SessionManager;
import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.global.common.ClientUserAgentInformationParser;
import com.virnect.uaa.global.security.token.JwtProvider;
import com.virnect.uaa.infra.rest.license.LicenseRestService;
import com.virnect.uaa.infra.rest.license.dto.UserLicenseInfo;
import com.virnect.uaa.infra.rest.license.dto.UserLicenseInfoResponse;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GuestAccountService {
	private final UserRepository userRepository;
	private final LicenseRestService licenseRestService;
	private final SessionManager<ClientSessionInfo> sessionManager;
	private final JwtProvider jwtProvider;
	private final ClientUserAgentInformationParser clientUserAgentInformationParser;

	public GuestAccountInfoResponse getAllocatableGuestAccount(
		GuestAccountAllocateRequest accountAllocateRequest,
		String guestUserAgent,
		String guestUserIp
	) {
		// 1. 라이선스를 가진 게스트 사용자 정보 조회
		List<User> licensedSeatUsers = filteredProductLicenseAllocateUser(
			accountAllocateRequest.getWorkspaceId(), accountAllocateRequest.getProduct()
		);
		int seatUserTotal = licensedSeatUsers.size();
		int currentAllocatedSeatUserTotal = 0;
		int currentDeallocatedSeatUserTotal = 0;
		User allocatableUser = null;

		for (User seatUser : licensedSeatUsers) {
			// 해당 사용자가 웹소켓 세션을 갖고 있는지 확인 -> 있다면 통화 중이거나 사용중
			String userSessionKey = String.format("%s_%s", accountAllocateRequest.getWorkspaceId(), seatUser.getUuid());
			if (sessionManager.hasExistUserSessionId(userSessionKey)) {
				currentAllocatedSeatUserTotal++;
				continue;
			}
			currentDeallocatedSeatUserTotal++;
			if (allocatableUser == null) {
				allocatableUser = seatUser;
			}
		}

		// 모든 게스트 사용자가 할당(통화) 상태
		if (currentAllocatedSeatUserTotal == seatUserTotal || allocatableUser == null) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_GUEST_USER_NOT_ENOUGH);
		}

		ClientGeoIPInfo clientGeoIPInfo = clientUserAgentInformationParser.getClientGeoIPInformation(
			guestUserIp, guestUserAgent
		);

		String accessTokenJwtId = getUUIDString();
		String refreshTokenJwtId = getUUIDString();
		String accessToken = jwtProvider.createAccessToken(allocatableUser, accessTokenJwtId, clientGeoIPInfo);
		String refreshToken = jwtProvider.createRefreshToken(
			allocatableUser, refreshTokenJwtId, accessTokenJwtId, clientGeoIPInfo);

		GuestAccountInfoResponse guestAccountInfoResponse = new GuestAccountInfoResponse();
		guestAccountInfoResponse.setName(allocatableUser.getName());
		guestAccountInfoResponse.setNickname(allocatableUser.getNickname());
		guestAccountInfoResponse.setUuid(allocatableUser.getUuid());
		guestAccountInfoResponse.setAccessToken(accessToken);
		guestAccountInfoResponse.setRefreshToken(refreshToken);
		guestAccountInfoResponse.setExpireIn(jwtProvider.getAccessTokenExpire());
		guestAccountInfoResponse.setScope("read,write");
		guestAccountInfoResponse.setTokenType("Bearer");

		SeatUserStat seatUserStat = new SeatUserStat();
		seatUserStat.setTotalSeatUser(seatUserTotal);
		seatUserStat.setAllocateSeatUserTotal(currentAllocatedSeatUserTotal);
		seatUserStat.setDeallocateSeatUserTotal(currentDeallocatedSeatUserTotal);
		guestAccountInfoResponse.setSeatUserStat(seatUserStat);

		return guestAccountInfoResponse;
	}

	private List<User> filteredProductLicenseAllocateUser(String workspaceId, String productName) {
		// 1. find all seat users in workspace
		List<User> allSeatUsers = userRepository.findAllSeatUsersByWorkspaceId(workspaceId);

		if (allSeatUsers.isEmpty()) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_GUEST_USER_NOT_FOUND);
		}

		List<String> userIds = allSeatUsers.stream().map(User::getUuid).collect(Collectors.toList());

		List<String> licenseAllocatedUserIds = findLicenseAllocateUserAndCollectUserId(
			workspaceId, productName, userIds);

		return allSeatUsers.stream()
			.filter(u -> licenseAllocatedUserIds.contains(u.getUuid()))
			.collect(Collectors.toList());
	}

	private List<String> findLicenseAllocateUserAndCollectUserId(
		String workspaceId, String productName, List<String> userIds
	) {
		ApiResponse<UserLicenseInfoResponse> remoteRestResponse = licenseRestService.getUserLicenseInfos(
			workspaceId, userIds, productName);

		if (remoteRestResponse == null || remoteRestResponse.getData() == null
			|| remoteRestResponse.getData().getUserLicenseInfos().isEmpty()) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_GUEST_USER_NOT_FOUND);
		}

		return remoteRestResponse.getData()
			.getUserLicenseInfos()
			.stream()
			.map(UserLicenseInfo::getUserId)
			.collect(Collectors.toList());
	}

	private String getUUIDString() {
		return UUID.randomUUID().toString();
	}
}
