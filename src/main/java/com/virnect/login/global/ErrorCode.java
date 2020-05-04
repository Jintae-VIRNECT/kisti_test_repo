package com.virnect.login.global;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Login
 * @email practice1356@gmail.com
 * @description Error code definition enum class
 * @since 2020.03.20
 */
public enum ErrorCode {
    ERR_LOGIN(1000, "Login Error"),
    ERR_REGISTER(1002, "Register Error"),
    ERR_INVALID_PARAMETER(8001, "Invalid Request Parameter");

    private int code;
    private String message;

    ErrorCode(int code, String message) {
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
