package com.virnect.license.global.error;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Project: base
 * DATE: 2020-01-07
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@ApiModel
public enum ErrorCode {
    // 로그인 관련 에러
    ERR_LOGIN(2000, "Login error occurred caused not matched email or password"),

    // 회원가입 관련 에러
    ERR_REGISTER(2101, "Account registration fail."),
    ERR_REGISTER_AUTHENTICATION(2102, "Email authentication fail."),
    ERR_REGISTER_SESSION_EXPIRE(2103, "Register session expire."),
    ERR_REGISTER_DUPLICATE_EMAIL(2104, "Register email is duplicate"),

    //사용자 정보 관련 에러
    ERR_USER_INFO(4000, "User Info Error"),
    ERR_USER_INFO_ACCESS(4001, "User info access fail"),
    ERR_USER_NOT_FOUND(4002, "User Not found"),
    ERR_USER_INFO_UPDATE(4003, "User info update fail"),
    ERR_USER_PROFILE_IMAGE_UPLOAD(4004, "User profile upload fail"),
    ERR_USER_PROFILE_IMAGE_EXTENSION(4005, "User profile image extension error, not support"),
    ERR_USER_PROFILE_IMAGE_SIZE_LIMIT(4006, "User profile image size is exceeded"),
    ERR_USER_PASSWORD_CHANGE(4007, "User Password Change Error"),
    ERR_USER_LEAVE_SERVICE(4008, "User Info Not Matched"),

    // 공통 에러
    ERR_API_AUTHENTICATION(8003, "Authentication Error"),
    ERR_INVALID_VALUE(8004,"Invalid Value"),
    ERR_INVALID_REQUEST_PARAMETER(8001, "Invalid request parameter cause api errors"),
    ERR_UNEXPECTED_SERVER_ERROR(9999, "Unexpected Server Error, Please contact Administrator");

    @ApiModelProperty(name = "code")
    private int code;
    @ApiModelProperty(name = "message")
    private String message;

    ErrorCode(final int code, final String message) {
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
