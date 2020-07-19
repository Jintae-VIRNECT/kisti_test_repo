package com.virnect.serviceserver.gateway.global.error;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public enum  ErrorCode {
    //원격협업 정보 관련 에러
    ERR_ROOM_INFO(4000, "Room Info Error"),
    ERR_ROOM_INFO_ACCESS(4001, "Room info access fail"),
    ERR_ROOM_NOT_FOUND(4002, "Room Not found"),
    ERR_ROOM_INFO_UPDATE(4003, "Room info update fail"),
    ERR_ROOM_PROFILE_IMAGE_UPLOAD(4004, "Room profile upload fail"),
    ERR_ROOM_PROFILE_IMAGE_EXTENSION(4005, "Room profile image extension error, not support"),
    ERR_ROOM_PROFILE_IMAGE_SIZE_LIMIT(4006, "Room profile image size is exceeded"),
    ERR_ROOM_MEMBER_INFO_EMPTY(4007, "Room Member information is empty"),
    ERR_ROOM_MEMBER_NOT_FOUND(4008, "Specific room member can not found"),
    ERR_ROOM_MEMBER_IS_OVER(4009, "Room cannot join"),
    ERR_ROOM_LICENSE_TYPE(4010, "Room license type is not allowed"),
    ERR_ROOM_INVALID_PERMISSION(4011, "Invalid permission to the Room"),

    //원격협업 유효성 정보 관련 에러
    ERR_LICENSE_NOT_VALIDITY(5001, "License has no validity"),
    ERR_LICENSE_TYPE_VALIDITY(5002, "License is not the license type"),
    ERR_LICENSE_PRODUCT_VALIDITY(5003, "License has no product"),
    ERR_LICENSE_UNEXPECTED_TYPE(5004, "This license is not unexpected type"),

    /*ERR_PASSWORD_INIT_CODE_NOT_FOUND(4007, "User password initialize info not found"),
    ERR_USER_PASSWORD_CHANGE(4008, "User Password Change Error"),
    ERR_USER_PASSWORD_CHANGE_DUPLICATE(4009, "Password is duplicated with previous password"),
    ERR_USER_LEAVE_SERVICE(4008, "User Info Not Matched"),*/

    // 공통 에러
    ERR_API_AUTHENTICATION(8003, "Authentication Error"),
    ERR_INVALID_VALUE(8004, "Invalid Value"),
    ERR_INVALID_REQUEST_PARAMETER(8001, "Invalid request parameter cause api errors"),
    ERR_AUTHORIZATION_EXPIRED(8005, "Authorization token is expired"),
    ERR_UNEXPECTED_SERVER_ERROR(9999, "Unexpected Server Error, Please contact Administrator");

    @ApiModelProperty(value = "에러 코드")
    private int code;
    @ApiModelProperty(value = "에러 메시지")
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
