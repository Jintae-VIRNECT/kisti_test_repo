package com.virnect.uaa.domain.user.application;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.security.UserDetailsImpl;
import com.virnect.uaa.domain.user.dao.secession.SecessionUserRepository;
import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.dao.useraccesslog.UserAccessLogRepository;
import com.virnect.uaa.domain.user.domain.SecessionUser;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.domain.UserAccessLog;
import com.virnect.uaa.domain.user.dto.response.InviteUserDetailInfoResponse;
import com.virnect.uaa.domain.user.dto.response.InviteUserInfoResponse;
import com.virnect.uaa.domain.user.dto.response.PageMetadataResponse;
import com.virnect.uaa.domain.user.dto.response.UserAccessDeviceInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserAccessHistoryResponse;
import com.virnect.uaa.domain.user.dto.response.UserDetailsInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoListOnlyResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoListResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;
import com.virnect.uaa.domain.user.error.UserAccountErrorCode;
import com.virnect.uaa.domain.user.exception.UserServiceException;
import com.virnect.uaa.domain.user.mapper.UserAccessDeviceInfoMapper;
import com.virnect.uaa.domain.user.mapper.UserInfoMapper;
import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.global.security.token.JwtPayload;
import com.virnect.uaa.global.security.token.JwtProvider;
import com.virnect.uaa.infra.rest.workspace.WorkspaceRestService;
import com.virnect.uaa.infra.rest.workspace.dto.WorkspaceInfoListResponse;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserInformationRetrieveServiceImpl implements UserInformationRetrieveService {
	private final SecessionUserRepository secessionUserRepository;
	private final UserRepository userRepository;
	private final UserAccessLogRepository userAccessLogRepository;
	private final UserInfoMapper userInfoMapper;
	private final UserAccessDeviceInfoMapper userAccessDeviceInfoMapper;
	private final WorkspaceRestService workspaceRestService;
	private final JwtProvider jwtProvider;

	@Override
	public UserInfoListResponse searchUserInformation(
		String searchQuery, Pageable pageable,
		boolean isPaging
	) {
		if (isPaging) {
			log.info("search user information with search and paging condition");
			Page<User> pagingResult = userRepository.findAllUserInfoWithSearchAndPagingCondition(
				searchQuery, pageable
			);
			List<UserInfoResponse> userInfoResponseList = pagingResult.get()
				.map(userInfoMapper::toUserInfoResponse)
				.collect(Collectors.toList());
			return new UserInfoListResponse(userInfoResponseList, PageMetadataResponse.of(pageable, pagingResult));
		}
		log.info("search user information with search condition");
		List<UserInfoResponse> userInfoResponseList = userRepository.findAllUserInfoWithSearchCondition(searchQuery)
			.stream()
			.map(userInfoMapper::toUserInfoResponse)
			.collect(Collectors.toList());

		return new UserInfoListResponse(userInfoResponseList, null);
	}

	@Override
	public UserDetailsInfoResponse getUserDetailsInformationByUserAuthenticatedRequest(
		HttpServletRequest request,
		Authentication authentication
	) {
		String userUUID = getUserUUIDFromUserAuthenticatedRequest(request, authentication);

		if (StringUtils.isEmpty(userUUID) || !isSessionRequest(request, authentication) && !isJwtRequest(request)) {
			log.error("session and jwt both validation process fail.");
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND);
		}

		UserInfoResponse userInfoResponse = findUserInformationByUserUUID(userUUID);
		ApiResponse<WorkspaceInfoListResponse> myWorkspaceInfoList = workspaceRestService.getMyWorkspaceInfoList(
			userUUID, 50);
		return new UserDetailsInfoResponse(userInfoResponse, myWorkspaceInfoList.getData().getWorkspaceList());
	}

	@Cacheable(value = "userInfo", key = "#uuid")
	@Override
	public UserInfoResponse findUserInformationByUserUUID(String uuid) {
		Optional<SecessionUser> secessionUserInformation = secessionUserRepository.findByUserUUID(uuid);
		if (secessionUserInformation.isPresent()) {
			log.info("[User][{}] is Secession User. Find Secession User Information", uuid);
			return userInfoMapper.toUserInfoResponse(secessionUserInformation.get());
		}
		User user = userRepository.findByUuid(uuid)
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));
		return userInfoMapper.toUserInfoResponse(user);
	}

	@Override
	public UserAccessHistoryResponse getUserAccessHistoryByUserUUID(
		String userUUID,
		Pageable pageable
	) {
		User user = userRepository.findByUuid(userUUID)
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));
		Page<UserAccessLog> userAccessLogs = userAccessLogRepository.findAllByUser(user, pageable);
		List<UserAccessDeviceInfoResponse> userAccessDeviceInfos = userAccessLogs.stream()
			.map(userAccessDeviceInfoMapper::ofUserAccessLog)
			.collect(Collectors.toList());
		return new UserAccessHistoryResponse(userAccessDeviceInfos, PageMetadataResponse.of(pageable, userAccessLogs));
	}

	@Override
	public UserInfoListOnlyResponse getUserInformationListByUserUUIDArray(
		String[] userUUIDs
	) {
		List<User> users = userRepository.findByUuidIn(Arrays.asList(userUUIDs));
		List<UserInfoResponse> userInfos = users.stream()
			.map(userInfoMapper::toUserInfoResponse)
			.collect(Collectors.toList());
		return new UserInfoListOnlyResponse(userInfos);
	}

	@Override
	public InviteUserInfoResponse getUserInformationByUserEmail(
		String email
	) {
		if (secessionUserRepository.existsByEmail(email)) {
			log.info("[SECESSION_USER_INVITE_ERROR] - USER: [{}] is secession user. Can't Invite to Workspace", email);
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_SECESSION_INVITE);
		}
		Optional<User> userInfo = userRepository.findByEmail(email);
		return userInfo.map(user -> new InviteUserInfoResponse(true, InviteUserDetailInfoResponse.ofUser(user)))
			.orElseGet(() -> {
				log.info("CreateDummyInviteUserInfo - Request User Not Found");
				return new InviteUserInfoResponse(false, InviteUserDetailInfoResponse.getDummy());
			});
	}

	private boolean isSessionRequest(HttpServletRequest request, Authentication authentication) {
		return request.getSession().getAttribute("userName") != null && authentication != null;
	}

	private boolean isJwtRequest(HttpServletRequest request) {
		return StringUtils.hasText(jwtProvider.getJwtFromRequest(request));
	}

	private String getUserUUIDFromUserAuthenticatedRequest(HttpServletRequest request, Authentication authentication) {
		if (isSessionRequest(request, authentication)) {
			return getUserUUIDFromSessionAuthenticationRequest(authentication);
		}

		if (isJwtRequest(request)) {
			return getUserUUIDFromJwtAuthenticationRequest(request);
		}
		return null;
	}

	private String getUserUUIDFromJwtAuthenticationRequest(HttpServletRequest request) {
		log.info("get user detail information from jwt authenticated request");
		String authorizationToken = jwtProvider.getJwtFromRequest(request);
		if (!jwtProvider.isValidToken(authorizationToken)) {
			throw new UserServiceException(UserAccountErrorCode.ERR_API_AUTHENTICATION);
		}
		JwtPayload jwtPayload = jwtProvider.getJwtPayload(authorizationToken);
		return jwtPayload.getUuid();
	}

	private String getUserUUIDFromSessionAuthenticationRequest(Authentication authentication) {
		log.info("get user detail information from session authenticated request");
		UserDetailsImpl userDetails = (UserDetailsImpl)authentication.getDetails();
		return userDetails.getUuid();
	}

}
