package com.virnect.uaa.domain.user.application;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.dao.UserOTPRepository;
import com.virnect.uaa.domain.user.dao.user.UserRepository;
import com.virnect.uaa.domain.user.dao.useraccesslog.UserAccessLogRepository;
import com.virnect.uaa.domain.user.dao.userpermission.UserPermissionRepository;
import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.dto.request.MemberDeleteRequest;
import com.virnect.uaa.domain.user.dto.request.MemberPasswordUpdateRequest;
import com.virnect.uaa.domain.user.dto.request.MemberRegistrationRequest;
import com.virnect.uaa.domain.user.dto.request.SeatMemberDeleteRequest;
import com.virnect.uaa.domain.user.dto.request.SeatMemberRegistrationRequest;
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
import com.virnect.uaa.infra.file.FileService;
import com.virnect.uaa.infra.rest.remote.RemoteRestService;
import com.virnect.uaa.infra.rest.remote.dto.RemoteSecessionResponse;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberUserInformationService {
	private final FileService fileService;
	private final UserInfoMapper userInfoMapper;
	private final UserRepository userRepository;
	private final UserAccessLogRepository userAccessLogRepository;
	private final UserPermissionRepository userPermissionRepository;
	private final UserOTPRepository userOTPRepository;
	private final PasswordEncoder passwordEncoder;
	private final RemoteRestService remoteRestService;

	/**
	 * 새 멤버 사용자 등록
	 * @param memberRegistrationRequest - 멤버 사용자 데이터
	 * @return - 등록된 새 멤버 사용자 정보
	 */
	public UserInfoResponse registerNewMember(MemberRegistrationRequest memberRegistrationRequest) {

		if (userRepository.existsByEmail(memberRegistrationRequest.getEmail())) {
			log.error("Member User Create Fail. Email Duplicate : {}", memberRegistrationRequest.getEmail());
			throw new UserServiceException(UserAccountErrorCode.ERR_REGISTER_MEMBER_DUPLICATE_ID);
		}

		User masterUser = userRepository.findByUuid(memberRegistrationRequest.getMasterUUID())
			.orElseThrow(
				() -> new UserServiceException(UserAccountErrorCode.ERR_REGISTER_MEMBER_MASTER_PERMISSION_DENIED));

		User user = User.ByRegisterMemberUserBuilder()
			.masterUser(masterUser)
			.memberRegistrationRequest(memberRegistrationRequest)
			.encodedPassword(passwordEncoder.encode(memberRegistrationRequest.getPassword()))
			.build();

		userRepository.save(user);

		log.info("[REGISTER_NEW_MEMBER_USER] - {}", user);

		return userInfoMapper.toUserInfoResponse(user);
	}

	/**
	 * 멤버 사용자 삭제
	 *
	 * @param memberDeleteRequest - 멤버 사용자 정보 삭제 요청 데이터
	 * @return - 사용자 삭제 처리 결과
	 */
	@CacheEvict(value = "userInfo", key = "#memberDeleteRequest.memberUserUUID")
	public UserDeleteResponse deleteMemberUser(MemberDeleteRequest memberDeleteRequest) {
		User masterUser = userRepository.findByUuid(memberDeleteRequest.getMasterUUID())
			.orElseThrow(
				() -> new UserServiceException(UserAccountErrorCode.ERR_DELETE_MEMBER_MASTER_PERMISSION_DENIED));

		User deleteTargetUser = userRepository.findWorkspaceOnlyUserByMasterAndSeatUserUUID(
			masterUser, memberDeleteRequest.getMemberUserUUID()
		).orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));

		deleteMemberInformation(deleteTargetUser);

		return new UserDeleteResponse(deleteTargetUser.getUuid(), LocalDateTime.now());
	}

	/**
	 * 신규 멤버 사용자 이메일 중복 여부 확인
	 * @param email - 신규 멤버 사용자 이메일 정보
	 * @return - 중복 유무 결과
	 */
	@Transactional(readOnly = true)
	public UserEmailExistCheckResponse userEmailDuplicateCheck(String email) {
		Optional<User> user = userRepository.findByEmail(email);

		if (!user.isPresent()) {
			log.info("[CREATE_WORKSPACE_ONLY_USER] - Email duplicate [{}]", email);
			throw new UserServiceException(UserAccountErrorCode.ERR_REGISTER_MEMBER_DUPLICATE_ID);
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
	@Transactional(readOnly = true)
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

		User memberUser = userRepository.findByUuid(memberPasswordUpdateRequest.getUuid())
			.orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));

		String encodedPassword = passwordEncoder.encode(memberPasswordUpdateRequest.getPassword());
		userInfoMapper.updateFromMemberUserPasswordRequest(encodedPassword, memberUser);
		userRepository.save(memberUser);

		return MemberPasswordUpdateResponse.ofMemberUserInfo(memberUser);
	}

	/**
	 * 시트 사용자 계정 등록 요청 처리
	 * @param seatMemberRegistrationRequest - seat 사용자 등록 요청  정보
	 * @return - seat 사용자 정보
	 */
	public UserInfoResponse registerNewSeatMember(SeatMemberRegistrationRequest seatMemberRegistrationRequest) {
		User masterUser = userRepository.findByUuid(seatMemberRegistrationRequest.getMasterUserUUID())
			.orElseThrow(
				() -> new UserServiceException(UserAccountErrorCode.ERR_REGISTER_SEAT_MEMBER_MASTER_PERMISSION_DENIED)
			);
		String seatUserPassword = masterUser.getUuid() + "_" + seatMemberRegistrationRequest.getWorkspaceUUID();
		int currentSeatUserCount = (int)userRepository.countCurrentSeatUserNumber(masterUser);

		User seatUser = User.ByRegisterSeatMemberUserBuilder()
			.workspaceUUID(seatMemberRegistrationRequest.getWorkspaceUUID())
			.masterUser(masterUser)
			.encodedPassword(passwordEncoder.encode(seatUserPassword))
			.seatUserSequence(currentSeatUserCount + 1)
			.build();

		userRepository.save(seatUser);

		return userInfoMapper.toUserInfoResponse(seatUser);
	}

	/**
	 * 시트 사용자 계정 삭제 요청 처리
	 * @param seatMemberDeleteRequest - 시트 사용자 계정 삭제 요청
	 * @return - 삭제 처리 결과
	 */
	@CacheEvict(value = "userInfo", key = "#seatMemberDeleteRequest.seatUserUUID")
	public UserDeleteResponse deleteSeatMember(SeatMemberDeleteRequest seatMemberDeleteRequest) {
		User masterUser = userRepository.findByUuid(seatMemberDeleteRequest.getMasterUUID())
			.orElseThrow(
				() -> new UserServiceException(UserAccountErrorCode.ERR_DELETE_SEAT_MEMBER_MASTER_PERMISSION_DENIED)
			);

		User seatUser = userRepository.findSeatUserByMasterAndSeatUserUUID(
			masterUser,
			seatMemberDeleteRequest.getSeatUserUUID()
		).orElseThrow(() -> new UserServiceException(UserAccountErrorCode.ERR_USER_NOT_FOUND));

		deleteMemberInformation(seatUser);

		return new UserDeleteResponse(seatUser.getUuid(), LocalDateTime.now());
	}

	private void deleteMemberInformation(User deleteUser) {
		userAccessLogRepository.deleteAllUserAccessLogByUser(deleteUser);
		deleteUserInformation(deleteUser);
		userRepository.delete(deleteUser);

		// send user delete event to remote service
		ApiResponse<RemoteSecessionResponse> response = remoteRestService.remoteUserSecession(deleteUser.getUuid());
		if (response == null || !response.getData().isResult()) {
			log.error("[REMOTE_MEMBER_USER_SECESSION ERROR]");
		}
	}

	private void deleteUserInformation(User user) {
		// Delete user profile image
		fileService.delete(user.getProfile());
		// Delete user account permission
		userPermissionRepository.deleteAllUserPermissionByUser(user);
		// Delete user otp code information
		userOTPRepository.deleteAllByEmail(user.getEmail());
		// Delete user access log history
		userAccessLogRepository.deleteAllUserAccessLogByUser(user);
	}
}
