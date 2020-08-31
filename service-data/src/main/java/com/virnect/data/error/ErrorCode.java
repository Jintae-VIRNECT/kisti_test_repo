package com.virnect.data.error;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public enum  ErrorCode {
    //
    ERR_SUCCESS(200, "anyway ok"),

    //원격협업 서버 에러
    ERR_SERVICE_PROCESS(3001, "Service Server Process error"),

    //원격협업 정보 관련 에러
    ERR_ROOM_INFO(4000, "Room Info Error"),
    ERR_ROOM_INFO_ACCESS(4001, "Room info access fail"),
    ERR_ROOM_NOT_FOUND(4002, "Room Not found maybe session is not active"),
    ERR_ROOM_INFO_UPDATE(4003, "Room info update fail"),
    ERR_ROOM_PROFILE_IMAGE_UPLOAD(4004, "Room profile upload fail"),
    ERR_ROOM_PROFILE_IMAGE_EXTENSION(4005, "Room profile image extension error, not support"),
    ERR_ROOM_PROFILE_IMAGE_SIZE_LIMIT(4006, "Room profile image size is exceeded"),
    ERR_ROOM_MEMBER_INFO_EMPTY(4007, "Room Member information is empty"),
    ERR_ROOM_MEMBER_NOT_FOUND(4008, "Specific room member can not found"),
    ERR_ROOM_MEMBER_IS_OVER(4009, "Room cannot join"),
    ERR_ROOM_LICENSE_TYPE(4010, "Room license type is not allowed"),
    ERR_ROOM_INVALID_PERMISSION(4011, "Invalid permission to the Room"),
    ERR_ROOM_STATUS_NOT_ACTIVE(4012, "Room is not active"),
    ERR_ROOM_MEMBER_NOT_ALLOWED(4013, "Not allowed member cannot join the room"),
    ERR_ROOM_MEMBER_NOT_ASSIGNED(4014, "any member not assigned, cannot join the room"),
    ERR_ROOM_LEADER_INVALID_EXIT(4015, "Room leader can not exit the room, must delete."),
    ERR_ROOM_MEMBER_ALREADY_JOINED(4016, "This member is already joined"),
    ERR_ROOM_MEMBER_STATUS_LOADED(4017, "This member status is loaded"),
    ERR_ROOM_MEMBER_KICK_FAIL(4018, "This member cannot enforce disconnect."),
    ERR_ROOM_CREATE_FAIL(4019, "Room Create fail"),
    ERR_ROOM_PROCESS_FAIL(4020, "Current Room request is failed"),
    ERR_ROOM_MEMBER_MAX_COUNT(4021, "Current Room member is over the limit"),

    //원격협업 유효성 정보 관련 에러
    ERR_LICENSE_NOT_VALIDITY(5001, "License has no validity"),
    ERR_LICENSE_TYPE_VALIDITY(5002, "License is not the license type"),
    ERR_LICENSE_PRODUCT_VALIDITY(5003, "License has no product"),
    ERR_LICENSE_UNEXPECTED_TYPE(5004, "This license is not unexpected type"),

    //원격협업 최근기록 관련 에러
    //ERR_HISTORY_NOT_VALIDITY(6001, "License has no validity"),
    ERR_HISTORY_ROOM_NOT_FOUND(6001, "Room History can not found"),
    ERR_HISTORY_ROOM_MEMBER_NOT_FOUND(6002, "Room History member not found"),
    ERR_HISTORY_TYPE_VALIDITY(6003, "License is not the license type"),
    ERR_HISTORY_PRODUCT_VALIDITY(6004, "License has no product"),
    ERR_HISTORY_UNEXPECTED_TYPE(6005, "This license is not unexpected type"),

    //원격협업 파일 업로드/다운로드 관련 에러
    ERR_FILE_UPLOAD_FAILED(7001, "File upload has failed"),
    ERR_FILE_ASSUME_DUMMY(7002, "File is assumed to dummy"),
    ERR_FILE_UNSUPPORTED_EXTENSION(7003, "This file extension is not supported"),
    ERR_FILE_SIZE_LIMIT(7004, "This file size is exceeded"),
    ERR_FILE_DOWNLOAD_FAILED(7005, "File download has failed"),
    ERR_FILE_DELETE_FAILED(7006, "File delete has failed"),
    ERR_FILE_DELETE_EXCEPTION(7007, "File delete has an exception"),
    ERR_FILE_UPLOAD_EXCEPTION(7008, "File upload has an exception"),
    ERR_FILE_GET_SIGNED_EXCEPTION(7009, "Get Pre signed url has an exception"),
    ERR_FILE_DOWNLOAD_EXCEPTION(7010, "File download has an exception"),

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
