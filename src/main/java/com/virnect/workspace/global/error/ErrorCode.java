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

    //워크스페이스 초대 관련 에러
    ERR_NOT_FOUND_INVITE_WORKSPACE_INFO(1004, "Not found Workspace Invite Code."),
    ERR_INCORRECT_INVITE_WORKSPACE_CODE(1005,"Incorrect Workspace Invite Code."),
    ERR_WORKSPACE_INVITE(1006,"Workspace Invite Fail."),
    //ERR_INVALID_USER_EXIST(1006,"Invalid User Exist."),

    ERR_INCORRECT_USER_LICENSE_INFO(1007,"Users must have at least 1 plan."),
    ERR_NOMORE_JOIN_WORKSPACE(1008,"No more join the workspace"),
    ERR_WORKSPACE_NOT_FOUND(1009, "Workspace not found"),
    ERR_WORKSPACE_USER_NOT_FOUND(1010, "Workspace User not found"),
    ERR_WORKSPACE_USER_ACCOUNT_CREATE_FAIL(1011,"Workspace user account create fail"),
    ERR_WORKSPACE_USER_ACCOUNT_DELETE_FAIL(1012,"Workspace user account delete fail"),
	ERR_WORKSPACE_USER_PASSWORD_CHANGE(1013,"Workspace user password change fail. Workspace Member Type User Not found"),
    ERR_WORKSPACE_ROLE_NOT_FOUND(1014,"Workspace Role not found"),
    ERR_WORKSPACE_PERMISSION_NOT_FOUND(1015,"Workspace Permission not found"),


    //라이선스 관련 에러
    ERR_WORKSPACE_USER_LICENSE_GRANT_FAIL(2000,"Workspace user license grant fail"),
    ERR_WORKSPACE_USER_LICENSE_REVOKE_FAIL(2001,"Workspace user license revoke fail"),
    ERR_NOT_FOUND_WORKSPACE_LICENSE_PLAN(2002,"Workspace license plan not found"),
    ERR_NOT_FOUND_USEFUL_WORKSPACE_LICENSE(2003,"Useful workspace license not found"),

    //파일 관련 에러
    ERR_NOT_ALLOW_FILE_EXTENSION(3000,"Not Allow file extension."),
    ERR_NOT_ALLOW_FILE_SIZE(3001,"Not Allow file size."),
    ERR_NOT_FOUND_FILE(3002,"Not Found file."),
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
