package com.virnect.uaa.domain.user.application;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.virnect.uaa.domain.user.domain.User;
import com.virnect.uaa.domain.user.dto.request.MemberUserPasswordChangeRequest;
import com.virnect.uaa.domain.user.dto.request.RegisterDetailsRequest;
import com.virnect.uaa.domain.user.dto.request.RegisterMemberRequest;
import com.virnect.uaa.domain.user.dto.request.RegisterRequest;
import com.virnect.uaa.domain.user.dto.request.UserEmailFindRequest;
import com.virnect.uaa.domain.user.dto.request.UserIdentityCheckRequest;
import com.virnect.uaa.domain.user.dto.request.UserInfoAccessCheckRequest;
import com.virnect.uaa.domain.user.dto.request.UserInfoModifyRequest;
import com.virnect.uaa.domain.user.dto.request.UserPasswordChangeRequest;
import com.virnect.uaa.domain.user.dto.request.UserPasswordFindAuthCodeCheckRequest;
import com.virnect.uaa.domain.user.dto.request.UserPasswordFindAuthCodeRequest;
import com.virnect.uaa.domain.user.dto.request.UserProfileUpdateRequest;
import com.virnect.uaa.domain.user.dto.request.UserSecessionRequest;
import com.virnect.uaa.domain.user.dto.response.InviteUserInfoResponse;
import com.virnect.uaa.domain.user.dto.response.LoginResponse;
import com.virnect.uaa.domain.user.dto.response.MemberUserPasswordChangeResponse;
import com.virnect.uaa.domain.user.dto.response.UserAccessHistoryResponse;
import com.virnect.uaa.domain.user.dto.response.UserDeleteResponse;
import com.virnect.uaa.domain.user.dto.response.UserDetailsInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserEmailExistCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserEmailFindResponse;
import com.virnect.uaa.domain.user.dto.response.UserIdentityCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoAccessCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoListOnlyResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoListResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserPasswordChangeResponse;
import com.virnect.uaa.domain.user.dto.response.UserPasswordFindAuthCodeResponse;
import com.virnect.uaa.domain.user.dto.response.UserPasswordFindCodeCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserProfileUpdateResponse;
import com.virnect.uaa.domain.user.dto.response.UserSecessionResponse;

public interface UserService {

	/**
	 * 사용자 로그인 요청 처리
	 *
	 * @param uuid - 사용자 고유 번호
	 * @return - 로그인 처리 완료 정보 ( 사용자 정보 + 사용자 소속 워크스페이스 정보 )
	 */
	LoginResponse login(String uuid);

	/**
	 * 사용자 UUID로 사용자 정보 조회
	 *
	 * @param userUUID - 사용자 고유 식별번호
	 * @return - 사용자 정보
	 */
	UserInfoResponse getUserInfoByUUID(String userUUID);

	/**
	 * 사용자 정보 조회 요청 처리
	 *
	 * @param uuid - 사용자 고유 식별번호
	 * @return - 사용자 정보
	 */
	@Cacheable(value = "userInfo", key = "#uuid")
	UserInfoResponse getUserInfoByUserId(String uuid);

	/**
	 * 사용자 ID로 사용자 정보 조회
	 *
	 * @param id - 사용자 고유 식별번호
	 * @return - 사용자 정보
	 */
	UserInfoResponse getUserInfoByUserId(long id);

	/**
	 * 회원가입 요청 처리
	 *
	 * @param registerRequest - 회원가입 요청 데이터
	 * @return - 등록된 신규 사용자 정보
	 */
	@Transactional
	UserInfoResponse register(RegisterRequest registerRequest);

	/**
	 * 사용자 엔티티 객체를 사용자 데이터 객체로 변환
	 *
	 * @param user - 사용제 엔티티 객체
	 * @return - 사용자 데이터 객체
	 */
	UserInfoResponse convertUserEntityToUserInfoDto(User user);

	/**
	 * 사용자 목록 조회 요청 처리
	 *
	 * @param search   - 조회 조건
	 * @param pageable - 페이징 요청 정보
	 * @return - 사용자 정보 목록
	 */
	UserInfoListResponse getUserInfoList(String search, boolean paging, Pageable pageable);

	/**
	 * 이메일로 초대 사용자 정보 조회 요청 처리
	 *
	 * @param email - 초대될 사용자들의 이메일 정보 리스트
	 * @return - 초대될 사용자의 정보 리스트
	 */
	InviteUserInfoResponse getInviteUserInfoList(String email);

	/**
	 * 사용자 상세 정보 조회 요청 처리
	 * @param request - 사용자 상세 정보 조회 요청 정보
	 * @return - 사용자 상세 정보
	 */
	UserDetailsInfoResponse getUserDetailsInfo(HttpServletRequest request);

	/**
	 * 사용자 정보 수정 요청 처리
	 * @param userUUID - 사용자 정보 식별자
	 * @param userInfoModifyRequest - 사용자 정보 수정 요청
	 * @return - 수정된 사용자 정보
	 */
	@Transactional
	@CacheEvict(value = "userInfo", key = "#userUUID")
	UserInfoResponse modifyUserInfo(String userUUID, UserInfoModifyRequest userInfoModifyRequest);

	/**
	 * 사용자 정보 접근 권한 여부 체크
	 *
	 * @param userUUID                   - 사용자 식별 번호
	 * @param userInfoAccessCheckRequest - 접근 요청 데이터(사용자 아이디 및 비밀번호 정보)
	 * @return - 접근 권한 체크 여부 및 사용자 정보 데이터
	 */
	UserInfoAccessCheckResponse userInfoAccessCheck(
		String userUUID, UserInfoAccessCheckRequest userInfoAccessCheckRequest
	);

	/**
	 * 사용자 프로필 업데이트 요청 처리
	 * @param userUUID - 사용자 식별자
	 * @param userProfileUpdateRequest - 프로필 이미지 변경 요청 정보
	 * @return - 프로필 이미지 변경된 사용자 정보
	 */
	@CacheEvict(value = "userInfo", key = "#userUUID")
	@Transactional
	UserProfileUpdateResponse profileImageUpdate(
		String userUUID, UserProfileUpdateRequest userProfileUpdateRequest
	);

	/**
	 * 회원가입 상세 정보 등록 요청 처리
	 * @param registerDetailsRequest - 사용자 회원가입 상세 정보
	 * @return - 등록 회원 정보
	 */
	@Transactional
	UserInfoResponse registerWithDetails(RegisterDetailsRequest registerDetailsRequest);

	/**
	 * 사용자 이메일 아이디 찾기 요청 처리
	 * @param userEmailFindRequest - 이메일 찾기 요청 정보
	 * @return - 이메일 찾기 결과 정보
	 */
	@Transactional(readOnly = true)
	UserEmailFindResponse userFindEmailHandler(UserEmailFindRequest userEmailFindRequest);

	/**
	 * 사용자 정보 전체 조회 (어드민 서비스)
	 * @param pageable - 페이징 요청 정보
	 * @return - 사용자 정보 리스트
	 */
	UserInfoListResponse findAllUserInfo(Pageable pageable);

	/**
	 * 비밀번호 변경 인증 코드 메일 전송 처리
	 * @param passwordAuthCodeRequest - 비밀번호 변경 요청 관련 정보
	 * @param locale
	 * @return - 인증 메일 전송 결과
	 */
	@Transactional
	UserPasswordFindAuthCodeResponse userPasswordFindAuthCodeSendHandler(
		UserPasswordFindAuthCodeRequest passwordAuthCodeRequest,
		Locale locale
	);

	/**
	 * 사용자 비밀번호 변경 인증 코드 정보 확인 처리
	 * @param authCodeCheckRequest - 비밀번호 변경 요청 인증 정보
	 * @return - 비밀번호 변경 요청 인증 정보 검증 결과
	 */
	@Transactional
	UserPasswordFindCodeCheckResponse userPasswordFindAuthCodeCheckHandler(
		UserPasswordFindAuthCodeCheckRequest authCodeCheckRequest
	);

	/**
	 * 사용자 비밀번호 변경 처리
	 * @param passwordChangeRequest - 비밀번호 변경 요청 정보
	 * @return - 비밀번호 변경 결과 정보
	 */
	@Transactional
	UserPasswordChangeResponse userPasswordChangeHandler(UserPasswordChangeRequest passwordChangeRequest);

	/**
	 * 사용자 정보 검색
	 * @param search - 검색어 (닉네임 또는 이메일)
	 * @param workspaceUserIdList - 사용자 식별번호 리스트 정보
	 * @return - 사용자 정보리스트
	 */
	UserInfoListResponse getUsersInfoList(String search, String[] workspaceUserIdList);

	/**
	 * 사용자 서비스 접속 기록 조회
	 * @param userId - 사용자 식별자 정보
	 * @param pageable - 페이징 요청 정보
	 * @return - 사용자 서비스 접속 기록 정보
	 */
	UserAccessHistoryResponse getUserAccessDeviceHistory(String userId, Pageable pageable);

	/**
	 * 회원탈퇴 처리
	 *
	 * @param userSecessionRequest - 회원탈퇴 요청
	 * @return - 회원 탈퇴 처리 결과
	 */
	@Transactional
	@CacheEvict(value = "userInfo", key = "#userSecessionRequest.uuid")
	UserSecessionResponse userSecessionRequestHandler(UserSecessionRequest userSecessionRequest);

	/**
	 * 멤버 추가 처리
	 * @param registerMemberRequest - 새로 추가할 멤버 사용자 정보
	 * @return - 새로 등록된 멤버 사용자 정보
	 */
	@Transactional
	UserInfoResponse registerNewMember(RegisterMemberRequest registerMemberRequest);

	/**
	 * 멤버 삭제 처리
	 * @param userUUID - 삭제할 멤버 사용자의 고유 식별자
	 * @return - 삭제 처리 결과
	 */
	@Transactional
	@CacheEvict(value = "userInfo", key = "#userUUID")
	UserDeleteResponse deleteMemberUser(String userUUID);

	/**
	 *  이메일 존재 여부 검사
	 * @param email - 존재 여부 검사 대상 이메일
	 * @return - 존재여부
	 */
	@Transactional(readOnly = true)
	UserEmailExistCheckResponse userEmailExistCheck(String email);

	/**
	 * 비밀번호 변경 질문 및 답변 확인
	 * @param userIdentityCheckRequest - 비밀번호 변경을 위한 사용자 인증 정보
	 * @return - 인증 결과 정보
	 */
	@Transactional
	UserIdentityCheckResponse userIdentityCheck(UserIdentityCheckRequest userIdentityCheckRequest);

	/**
	 * 멤버 사용자 비밀번호 변경 처리
	 * @param memberUserPasswordChangeRequest - 비밀번호 변경 요청 정보
	 * @return - 비밀번호 변경 처리 결과
	 */
	@Transactional
	MemberUserPasswordChangeResponse memberUserPasswordChangeHandler(
		MemberUserPasswordChangeRequest memberUserPasswordChangeRequest
	);

	/**
	 * 사용자 UUID 리스트 기반 사용자 정보 조회
	 * @param uuidList - 사용자 식별자 배열
	 * @return - 사용자 정보 배열
	 */
	UserInfoListOnlyResponse getUserInfoListByUUIDList(String[] uuidList);
}
