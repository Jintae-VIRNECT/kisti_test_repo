package com.virnect.uaa.domain.user.application;

import com.virnect.uaa.domain.user.dto.request.MemberPasswordUpdateRequest;
import com.virnect.uaa.domain.user.dto.request.RegisterMemberRequest;
import com.virnect.uaa.domain.user.dto.request.UserIdentityCheckRequest;
import com.virnect.uaa.domain.user.dto.response.MemberPasswordUpdateResponse;
import com.virnect.uaa.domain.user.dto.response.UserDeleteResponse;
import com.virnect.uaa.domain.user.dto.response.UserEmailExistCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserIdentityCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;

public class OffUserService {
	/**
	 * 새 멤버 사용자 등록
	 * @param registerMemberRequest - 멤버 사용자 데이터
	 * @return - 등록된 새 멤버 사용자 정보
	 */
	public UserInfoResponse registerNewMember(RegisterMemberRequest registerMemberRequest) {
		return null;
	}

	/**
	 * 기존 멤버 사용자 삭제
	 * @param userUUID - 사용자 정보 식별자
	 * @return - 사용자 삭제 처리 결과
	 */
	public UserDeleteResponse deleteMemberUser(String userUUID) {
		return null;
	}

	/**
	 * 신규 멤버 사용자 이메일 중복 여부 확인
	 * @param email - 신규 멤버 사용자 이메일 정보
	 * @return - 중복 유무 결과
	 */
	public UserEmailExistCheckResponse userEmailDuplicateCheck(String email) {
		return null;
	}

	/**
	 * 멤버 사용자 비밀번호 재설정 질답 검증
	 * @param userIdentityCheckRequest - 비밀번호 재설정 질답 데이터
	 * @return - 비밀번호 재설정 질답 검증 결과
	 */
	public UserIdentityCheckResponse verifyPasswordResetQuestion(UserIdentityCheckRequest userIdentityCheckRequest) {
		return null;
	}

	/**
	 * 멤버 사용자 비밀번호 재설정
	 * @param memberPasswordUpdateRequest - 멤버 사용자 비밀번호 재설정 요청 데이터
	 * @return - 비밀번호 재설정 처리 결과
	 */
	public MemberPasswordUpdateResponse updateMemberPassword(MemberPasswordUpdateRequest memberPasswordUpdateRequest) {
		return null;
	}
}
