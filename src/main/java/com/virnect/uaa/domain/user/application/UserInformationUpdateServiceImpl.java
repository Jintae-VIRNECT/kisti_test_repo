package com.virnect.uaa.domain.user.application;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.dao.UserOTPRepository;
import com.virnect.uaa.domain.user.application.process.SecessionProcessService;
import com.virnect.uaa.domain.user.dao.secession.SecessionUserRepository;
import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.dao.useraccesslog.UserAccessLogRepository;
import com.virnect.uaa.domain.user.dao.userpermission.UserPermissionRepository;
import com.virnect.uaa.domain.user.domain.SecessionUser;
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
import com.virnect.uaa.infra.file.FileService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInformationUpdateServiceImpl implements UserInformationUpdateService {
	private final UserRepository userRepository;
	private final SecessionUserRepository secessionUserRepository;
	private final UserPermissionRepository userPermissionRepository;
	private final UserOTPRepository userOTPRepository;
	private final UserAccessLogRepository userAccessLogRepository;

	private final SecessionProcessService secessionProcessService;

	private final PasswordEncoder passwordEncoder;
	private final UserInfoMapper userInfoMapper;
	private final FileService fileService;

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
		User user = userRepository.findByUuid(userId)
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));

		if (profileImageUpdateRequest.isUpdateAsDefaultImage()) {
			user.profileImageSetAsDefaultImage();
		} else {
			try {
				String newProfileImageUrl = fileService.upload(profileImageUpdateRequest.getProfile());
				fileService.delete(user.getProfile());
				user.setProfile(newProfileImageUrl);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				throw new UserServiceException(UserAccountErrorCode.ERR_USER_PROFILE_IMAGE_UPLOAD);
			}
		}
		userRepository.save(user);
		return new UserProfileUpdateResponse(userId, user.getProfile());
	}

	@Override
	public UserInfoResponse updateDetailInformation(
		String userUUID, UserInfoModifyRequest userInfoModifyRequest
	) {
		User user = userRepository.findByUuid(userUUID)
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));
		// password duplication check
		if (userInfoModifyRequest.getPassword() != null) {
			if (passwordEncoder.matches(userInfoModifyRequest.getPassword(), user.getPassword())) {
				throw new UserServiceException(UserAccountErrorCode.ERR_USER_PASSWORD_CHANGE_DUPLICATE);
			}
			user.setPassword(passwordEncoder.encode(userInfoModifyRequest.getPassword()));
			user.setPasswordUpdateDate(LocalDateTime.now());
		}
		userInfoMapper.updateFromDetailUpdateRequest(userInfoModifyRequest, user);
		userRepository.save(user);
		return userInfoMapper.toUserInfoResponse(user);
	}

	@Override
	public UserSecessionResponse accountSecession(
		UserSecessionRequest userSecessionRequest
	) {
		// 1. find target user
		User user = userRepository.findByUuid(userSecessionRequest.getUuid())
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));

		// 2. user password compare for access grant validation
		if (!passwordEncoder.matches(userSecessionRequest.getPassword(), user.getPassword())) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_SECESSION_PASSWORD);
		}

		// 3. Register current user as secession user
		registerUserToSecessionUser(user, userSecessionRequest.getReason());

		// 4. Delete current user information
		deleteUserInformation(user);

		// 5. Delete user account
		userRepository.delete(user);

		// 6. secession process to related service
		return secessionProcessService.sendSecessionRequestToAllExternalService(userSecessionRequest, user);
	}

	private boolean accessPermissionValidator(
		String userId, AccessPermissionCheckRequest accessPermissionCheckRequest, User user
	) {
		return !user.getUuid().equals(userId) ||
			!passwordEncoder.matches(accessPermissionCheckRequest.getPassword(), user.getPassword());
	}

	/**
	 * 탈퇴 계정 정보에 탈퇴 대상 계정 정보 등록
	 * @param user - 탈퇴 대상 계정 정보
	 * @param secessionReason - 탈퇴 사유
	 */
	private void registerUserToSecessionUser(final User user, final String secessionReason) {
		SecessionUser secessionUser = SecessionUser.of(user, secessionReason);
		secessionUserRepository.save(secessionUser);
		log.info("[USER_SECESSION] - {}", secessionUser);
	}

	private void deleteUserInformation(User user) {
		// Delete user account permission
		userPermissionRepository.deleteAllUserPermissionByUser(user);
		// Delete user otp code information
		userOTPRepository.deleteAllByEmail(user.getEmail());
		// Delete user access log history
		userAccessLogRepository.deleteAllUserAccessLogByUser(user);
	}
}
