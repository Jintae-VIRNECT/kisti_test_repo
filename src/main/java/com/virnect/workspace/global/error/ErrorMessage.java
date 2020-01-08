package com.virnect.workspace.global.error;

/**
 * Project: base
 * DATE: 2020-01-07
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public enum ErrorMessage {
    SOME_ERROR_MESSAGE(1, "error message");
    private int code;
    private String message;

    ErrorMessage(final int code, final String message){
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
