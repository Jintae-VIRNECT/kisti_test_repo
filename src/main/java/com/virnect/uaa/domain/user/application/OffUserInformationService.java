package com.virnect.uaa.domain.user.application;

import static com.virnect.uaa.domain.user.domain.UserType.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.dao.UserOTPRepository;
import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.dao.useraccesslog.UserAccessLogRepository;
import com.virnect.uaa.domain.user.dao.userpermission.UserPermissionRepository;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.dto.request.MemberPasswordUpdateRequest;
import com.virnect.uaa.domain.user.dto.request.RegisterMemberRequest;
import com.virnect.uaa.domain.user.dto.request.UserIdentityCheckRequest;
import com.virnect.uaa.domain.user.dto.response.MemberPasswordUpdateResponse;
import com.virnect.uaa.domain.user.dto.response.UserDeleteResponse;
import com.virnect.uaa.domain.user.dto.response.UserEmailExistCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserIdentityCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;
import com.virnect.uaa.domain.user.error.UserAccountErrorCode;
import com.virnect.uaa.domain.user.exception.UserServiceException;
import com.virnect.uaa.domain.user.mapper.UserInfoMapper;
import com.virnect.uaa.global.common.ApiResponse;
import com.virnect.uaa.infra.rest.remote.RemoteRestService;
import com.virnect.uaa.infra.rest.remote.dto.RemoteSecessionResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class OffUserInformationService {
	private final UserInfoMapper userInfoMapper;
	private final UserRepository userRepository;
	private final UserAccessLogRepository userAccessLogRepository;
	private final UserPermissionRepository userPermissionRepository;
	private final UserOTPRepository userOTPRepository;
	private final PasswordEncoder passwordEncoder;
	private final RemoteRestService remoteRestService;

	/**
	 * 새 멤버 사용자 등록
	 * @param registerMemberRequest - 멤버 사용자 데이터
	 * @return - 등록된 새 멤버 사용자 정보
	 */
	public UserInfoResponse registerNewMember(RegisterMemberRequest registerMemberRequest) {
		User user = User.ByRegisterMemberUserBuilder()
			.registerMemberRequest(registerMemberRequest)
			.encodedPassword(passwordEncoder.encode(registerMemberRequest.getPassword()))
			.build();

		userRepository.save(user);

		log.info("[REGISTER_NEW_MEMBER_USER] - {}", user);

		return userInfoMapper.toUserInfoResponse(user);
	}

	/**
	 * 기존 멤버 사용자 삭제
	 * @param userUUID - 사용자 정보 식별자
	 * @return - 사용자 삭제 처리 결과
	 */
	public UserDeleteResponse deleteMemberUser(String userUUID) {
		User deleteTargetUser = userRepository.findByUuid(userUUID)
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));

		userAccessLogRepository.deleteAllUserAccessLogByUser(deleteTargetUser);
		userRepository.delete(deleteTargetUser);

		deleteUserInformation(deleteTargetUser);

		// send user delete event to remote service
		ApiResponse<RemoteSecessionResponse> remoteResponse = remoteRestService.remoteUserSecession(userUUID);
		if (remoteResponse == null || !remoteResponse.getData().isResult()) {
			log.error("[REMOTE_MEMBER_USER_SECESSION ERROR]");
		}

		return new UserDeleteResponse(userUUID, LocalDateTime.now());
	}

	/**
	 * 신규 멤버 사용자 이메일 중복 여부 확인
	 * @param email - 신규 멤버 사용자 이메일 정보
	 * @return - 중복 유무 결과
	 */
	public UserEmailExistCheckResponse userEmailDuplicateCheck(String email) {
		Optional<User> user = userRepository.findByEmail(email);

		if (!user.isPresent()) {
			return new UserEmailExistCheckResponse(email, false, LocalDateTime.now());
		}

		//  비밀번호 찾기 질의 정보가 설정되어있지 않은 경우
		if (StringUtils.isEmpty(user.get().getQuestion()) && StringUtils.isEmpty(user.get().getAnswer())) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_PASSWORD_QUESTION_AND_ANSWER_NOT_INITIALIZED);
		}

		return new UserEmailExistCheckResponse(email, true, LocalDateTime.now());
	}

	/**
	 * 멤버 사용자 비밀번호 재설정 질답 검증
	 * @param userIdentityCheckRequest - 비밀번호 재설정 질답 데이터
	 * @return - 비밀번호 재설정 질답 검증 결과
	 */
	public UserIdentityCheckResponse verifyPasswordResetQuestion(UserIdentityCheckRequest userIdentityCheckRequest) {
		User user = userRepository.findByEmail(userIdentityCheckRequest.getEmail())
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));

		if (!user.passwordResetQuestionAndAnswerValidation(
			userIdentityCheckRequest.getQuestion(), userIdentityCheckRequest.getAnswer()
		)) {
			throw new UserServiceException(UserAccountErrorCode.ERR_USER_PASSWORD_CHANGE_ANSWER_AND_QUESTION);
		}
		return new UserIdentityCheckResponse(user.getEmail(), user.getUuid(), LocalDateTime.now());
	}

	/**
	 * 멤버 사용자 비밀번호 재설정
	 * @param memberPasswordUpdateRequest - 멤버 사용자 비밀번호 재설정 요청 데이터
	 * @return - 비밀번호 재설정 처리 결과
	 */
	public MemberPasswordUpdateResponse updateMemberPassword(MemberPasswordUpdateRequest memberPasswordUpdateRequest) {
		User memberUser = userRepository.findByUuidAndUserType(memberPasswordUpdateRequest.getUuid(), MEMBER_USER)
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));

		String encodedPassword = passwordEncoder.encode(memberPasswordUpdateRequest.getPassword());
		userInfoMapper.updateFromMemberUserPasswordRequest(encodedPassword, memberUser);
		userRepository.save(memberUser);

		return MemberPasswordUpdateResponse.ofMemberUserInfo(memberUser);
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
