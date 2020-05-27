package com.virnect.workspace.global.error;

/**
 * Project: base
 * DATE: 2020-01-07
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public enum ErrorCode {
    // 워크스페이스 에러
    ERR_MASTER_WORKSPACE_ALREADY_EXIST(1001, "User already have master workspace."),
    ERR_WORKSPACE_USER_ALREADY_EXIST(1002, "Workspace User already exist."),
    ERR_WORKSPACE_INVALID_PERMISSION(1003, "Invalid permission to the Workspace."),
    ERR_NOT_FOUND_INVITE_WORKSPACE_INFO(1004, "Not found Wokrspace Invite Code."),
    ERR_INCORRECT_INVITE_WORKSPACE_CODE(1005,"Incorrect Workspace Invite Code."),
    ERR_INVALID_USER_EXIST(1006,"Invalid User Exist."),
    ERR_INCORRECT_USER_PLAN_INFO(1007,"Users must have at least 1 plan."),

    // 공통 에러
    ERR_API_AUTHENTICATION(8003, "Authentication Error"),
    ERR_INVALID_VALUE(8004,"Invalid Value"),
    ERR_INVALID_REQUEST_PARAMETER(8001, "Invalid request parameter cause api errors"),
    ERR_UNEXPECTED_SERVER_ERROR(9999, "Unexpected Server Error, Please contact Administrator");

    private int code;
    private String message;

    ErrorCode(final int code, final String message){
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
        return new ErrorResponseMessage(this).toString();
    }
}
