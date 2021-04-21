package com.virnect.uaa.domain.auth.error;

/**
 * @project: PF-Auth
 * @author: jeonghyeon.chang (johnmark)
 * @email: practice1356@gmail.com
 * @since: 2020.03.09
 * @description: Auth Service Error Messages
 */
public enum AuthenticationErrorCode {
	// 로그인 관련 에러
	ERR_LOGIN(2000, "등록하신 계정 정보가 존재하지않습니다. 이메일 또는 비밀번호를 확인해주세요"),
	ERR_ACCOUNT_EXPIRED(2001, "Account expired"), // 계정 만료 -> 휴면 계정
	ERR_ACCOUNT_LOCK(2002, "Account is lock"), // 비밀번호 시도 횟수 -> 계정 잠금
	ERR_ACCOUNT_CREDENTIALS_EXPIRED(2003, "Account Credentials expired"), // 비밀번호 만료 -> 비밀번호 재등록
	ERR_ACCOUNT_NOT_ENABLE(2004, "Account not enabled"),
	ERR_SSO_TOKEN_VALIDATION_FAIL(2005, "The SSO token is not valid."),
	ERR_LOGIN_SESSION_DUPLICATE(2006, "Login Session Duplicate. Previous Session Exist"),

	// 로그아웃 관련 에러
	ERR_LOGOUT(2100, "Logout error"),
	ERR_LOGOUT_USER_NOT_FOUND(2101, "Logout error, user not found"),

	// 회원가입 관련 에러
	ERR_REGISTER_DUPLICATED_EMAIL(2200, "Email is duplicate"),
	ERR_REGISTER_AUTHENTICATION(2201, "Email authentication fail."),
	ERR_REGISTER_SESSION_EXPIRE(2202, "Register session expire."),
	ERR_REGISTER(2203, "Account registration fail."),
	ERR_REGISTER_SECESSION_USER_EMAIL(2206, "Secession User Email."),
	ERR_REGISTER_PROFILE_IMAGE_MAX_SIZE_EXCEEDED(2204, "Profile image max size limit exceeded."),
	ERR_REGISTER_PROFILE_IMAGE_NOT_SUPPORT(2205, "Profile image type not supported."),

	// 인증 관련 에러
	ERR_OTP_QR_CODE_CREATE(3001, "OTP QR Code Generate fail."),
	ERR_REFRESH_ACCESS_TOKEN(3002, "Access token refresh fail."),

	// 첫 로그인 비밀번호 초기화 관련 에러
	ERR_ACCOUNT_PASSWORD_INITIALIZED_REQUIRED(3301, "User password initialized required."),
	ERR_PASSWORD_INITIALIZED_SESSION_NOT_FOUND(3302, "User Password Initialized session info not found."),
	ERR_PASSWORD_INITIALIZED_SESSION_EXPIRED(3303, "User Password Initialized session expired."),

	// 앱 인증 관련 에러
	ERR_APP_AUTHENTICATION(4000, "Application authentication process fail."),
	ERR_APP_INFORMATION_DECRYPT(4001, "Application authentication process fail. detail data decrypt fail."),

	// 공통 에러
	ERR_INVALID_REQUEST_PARAMETER(8001, "Invalid request parameter cause api errors"),
	ERR_API_AUTHENTICATION(8003, "Authentication Error"),
	ERR_INVALID_VALUE(8004, "Invalid Value"),
	ERR_AUTHORIZATION_EXPIRED(8005, "Authorization token is expired"),
	ERR_UNEXPECTED_SERVER_ERROR(9999, "Unexpected Server Error, Please contact Administrator");

	private final int code;
	private final String message;

	AuthenticationErrorCode(final int code, final String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "AuthenticationErrorCode{" +
			"code=" + code +
			", message='" + message + '\'' +
			'}';
	}
}
