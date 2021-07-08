package com.virnect.uaa.domain.user.application;

import com.virnect.uaa.domain.user.dto.request.AccessPermissionCheckRequest;
import com.virnect.uaa.domain.user.dto.request.ProfileImageUpdateRequest;
import com.virnect.uaa.domain.user.dto.request.UserInfoModifyRequest;
import com.virnect.uaa.domain.user.dto.request.UserSecessionRequest;
import com.virnect.uaa.domain.user.dto.response.UserInfoAccessCheckResponse;
import com.virnect.uaa.domain.user.dto.response.UserInfoResponse;
import com.virnect.uaa.domain.user.dto.response.UserProfileUpdateResponse;
import com.virnect.uaa.domain.user.dto.response.UserSecessionResponse;

public interface UserInformationUpdateService {
	/**
	 * 개인 정보 접근 권한 확인
	 * @param userId - 사용자 고유 식별자
	 * @param accessPermissionCheckRequest - 권한 확인 요청 데이터
	 * @return - 권한 확인 결과
	 */
	UserInfoAccessCheckResponse accessPermissionCheck(
		String userId, AccessPermissionCheckRequest accessPermissionCheckRequest
	);

	/**
	 * 프로필 이미지 업데이트 
	 * @param userId - 사용자 고유 식별자
	 * @param profileImageUpdateRequest - 프로필 업데이트 요청 데이터
	 * @return - 프로필 이미지 업데이트 결과
	 */
	UserProfileUpdateResponse profileImageUpdate(String userId, ProfileImageUpdateRequest profileImageUpdateRequest);

	/**
	 * 개인 정보 수정 
	 * @param userUUID - 사용자 고유 식별자
	 * @param userInfoModifyRequest - 개인 정보 수정 요청 데이터
	 * @return - 개인 정보 수정 결과
	 */
	UserInfoResponse updateDetailInformation(String userUUID, UserInfoModifyRequest userInfoModifyRequest);

	/**
	 * 회원 탈퇴
	 * @param userSecessionRequest - 탈퇴 요청 데이터
	 * @return - 탈퇴 처리 결과
	 */
	UserSecessionResponse accountSecession(UserSecessionRequest userSecessionRequest);
}
