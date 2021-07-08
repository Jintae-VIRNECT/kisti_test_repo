package com.virnect.uaa.domain.user.application;

import java.util.Locale;

import com.virnect.uaa.domain.user.dto.request.EmailFindRequest;
import com.virnect.uaa.domain.user.dto.request.UserPasswordChangeRequest;
import com.virnect.uaa.domain.user.dto.request.UserPasswordFindAuthCodeCheckRequest;
import com.virnect.uaa.domain.user.dto.request.UserPasswordFindAuthCodeRequest;
import com.virnect.uaa.domain.user.dto.response.UserEmailFindResponse;
import com.virnect.uaa.domain.user.dto.response.UserPasswordChangeResponse;
import com.virnect.uaa.domain.user.dto.response.UserPasswordFindAuthCodeResponse;
import com.virnect.uaa.domain.user.dto.response.UserPasswordFindCodeCheckResponse;

public interface UserInformationFindService {
	/**
	 * 가입 이메일 찾기
	 * @param emailFindRequest - 가입 이메일 찾기 요청 데이터
	 * @return - 가입 이메일 찾기 결과
	 */
	UserEmailFindResponse findUserEmail(EmailFindRequest emailFindRequest);

	/**
	 * 비밀번호 재설정 인증 코드 생성 및 발송
	 * @param passwordAuthCodeRequest - 비밀번호 재설정 요청 데이터
	 * @param locale - 사용자 언어 정보
	 * @return - 비밀번호 재설정 인증 코드 발송 결과
	 */
	UserPasswordFindAuthCodeResponse sendPasswordResetEmail(
		UserPasswordFindAuthCodeRequest passwordAuthCodeRequest, Locale locale
	);

	/**
	 * 비밀번호 재설정 인증 코드 검증
	 * @param authCodeCheckRequest - 비밀번호 재설정 인증 코드 검증 요청 데이터
	 * @return - 인증코드 검증 결과
	 */
	UserPasswordFindCodeCheckResponse verifyPasswordResetCode(
		UserPasswordFindAuthCodeCheckRequest authCodeCheckRequest
	);

	/**
	 * 비밀번호 재설정
	 * @param passwordChangeRequest - 비밀번호 재설정 데이터 
	 * @return - 비밀번호 재설정 결과
	 */
	UserPasswordChangeResponse renewalPreviousPassword(UserPasswordChangeRequest passwordChangeRequest);
}
