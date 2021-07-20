package com.virnect.uaa.domain.user.error;

/**
 * Project: base
 * DATE: 2020-01-07
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public enum UserAccountErrorCode {
	// 로그인 관련 에러
	ERR_LOGIN(2000, "Login error occurred caused not matched email or password"),

	// 회원가입 관련 에러
	ERR_REGISTER(2101, "Account registration fail."),
	ERR_REGISTER_AUTHENTICATION(2102, "Email authentication fail."),
	ERR_REGISTER_SESSION_EXPIRE(2103, "Register session expire."),
	ERR_REGISTER_DUPLICATE_EMAIL(2104, "Register email is duplicate"),

	// 회원 등록 관련 에러
	ERR_REGISTER_MEMBER_DUPLICATE_ID(2201, "Member Registration fail. Member ID is duplicated."),

	//사용자 정보 관련 에러
	ERR_USER_INFO(4000, "User Info Error"),
	ERR_USER_INFO_ACCESS(4001, "User info access fail"),
	ERR_USER_NOT_FOUND(4002, "User Not found"),
	ERR_USER_INFO_UPDATE(4003, "User info update fail"),
	ERR_USER_PROFILE_IMAGE_UPLOAD(4004, "User profile upload fail"),
	ERR_USER_PROFILE_IMAGE_EXTENSION(4005, "User profile image extension error, not support"),
	ERR_USER_PROFILE_IMAGE_SIZE_LIMIT(4006, "User profile image size is exceeded"),
	ERR_PASSWORD_INIT_CODE_NOT_FOUND(4007, "User password initialize info not found"),
	ERR_USER_PASSWORD_CHANGE(4008, "User Password Change Error"),
	ERR_USER_PASSWORD_CHANGE_DUPLICATE(4009, "Password is duplicated with previous password"),
	ERR_USER_PASSWORD_CHANGE_ANSWER_AND_QUESTION(4010, "User Password Change Error. Question or Answer is incorrect."),
	ERR_USER_PASSWORD_QUESTION_AND_ANSWER_NOT_INITIALIZED(
		4011, "User Password Question and Answer is not exist. Please Contact Administrator"),

	// 사용자 탈퇴 관련 에러
	ERR_USER_SECESSION(5000, "User secession process fail."),
	ERR_USER_SECESSION_PASSWORD(5001, "User secession process fail. password is mismatch."),
	// 사용자 초대 시, 탈퇴회원인 경우 에러
	ERR_USER_SECESSION_INVITE(5002, "This user is secession user. Can't invite to workspace."),

	// 공통 에러
	ERR_INVALID_REQUEST_PARAMETER(8001, "Invalid request parameter cause api errors"),
	ERR_API_AUTHENTICATION(8003, "Authentication Error"),
	ERR_INVALID_VALUE(8004, "Invalid Value"),
	ERR_AUTHORIZATION_EXPIRED(8005, "Authorization token is expired"),
	ERR_UNEXPECTED_SERVER_ERROR(9999, "Unexpected Server Error, Please contact Administrator");

	private final int code;
	private final String message;

	UserAccountErrorCode(final int code, final String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
