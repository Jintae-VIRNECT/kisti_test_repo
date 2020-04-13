package com.virnect.download.global.error;

/**
 * Project: PF-Admin
 * DATE: 2020-02-20
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public enum ErrorCode {
    // 공통 에러
    ERR_API_AUTHENTICATION(8003, "Authentication Error"),
    ERR_INVALID_VALUE(8004,"Invalid Value"),
    ERR_INVALID_REQUEST_PARAMETER(8001, "Invalid request parameter cause api errors"),
    ERR_RESOURCE_UPLOAD_ERROR(8005,"Resource Upload Error"),
    ERR_NOT_SUPPORTED_FILE(8006,"Invalid File"),
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
}
