package com.virnect.uaa.domain.user.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.user.dao.secession.SecessionUserRepository;
import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.dto.request.AccessPermissionCheckRequest;
import com.virnect.uaa.domain.user.dto.request.ProfileImageUpdateRequest;
import com.virnect.uaa.domain.user.dto.request.UserInfoModifyRequest;
import com.virnect.uaa.domain.user.dto.request.UserSecessionRequest;
import com.virnect.uaa.domain.user.dto.response.UserInfoAccessCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserProfileUpdateResponse;
import com.virnect.uaa.domain.user.dto.response.UserSecessionResponse;
import com.virnect.uaa.domain.user.error.UserAccountErrorCode;
import com.virnect.uaa.domain.user.exception.UserServiceException;
import com.virnect.uaa.domain.user.mapper.UserInfoMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInformationUpdateServiceImpl implements UserInformationUpdateService {
	private final SecessionUserRepository secessionUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final UserInfoMapper userInfoMapper;

	@Override
	public UserInfoAccessCheckResponse accessPermissionCheck(
		String userId,
		AccessPermissionCheckRequest accessPermissionCheckRequest
	) {
		User user = userRepository.findByEmail(accessPermissionCheckRequest.getEmail())
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_INFO_ACCESS));
		if (accessPermissionValidator(userId, accessPermissionCheckRequest, user)) {
			log.error("UserInfoAccessCheckFail - Email:[{}]", accessPermissionCheckRequest.getEmail());
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_INFO_ACCESS);
		}
		return new UserInfoAccessCheckResponse(true, userInfoMapper.toUserInfoResponse(user));
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

	private boolean accessPermissionValidator(
		String userId, AccessPermissionCheckRequest accessPermissionCheckRequest, User user
	) {
		return !user.getUuid().equals(userId) ||
			!passwordEncoder.matches(accessPermissionCheckRequest.getPassword(), user.getPassword());
	}
}
