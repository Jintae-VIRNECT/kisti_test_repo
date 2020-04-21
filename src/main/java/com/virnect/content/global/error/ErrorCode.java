package com.virnect.content.global.error;

import io.swagger.annotations.ApiModel;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-07
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@ApiModel
public enum ErrorCode {
    // 콘텐츠 관련
    ERR_CONTENT_UPLOAD(4001, "Content upload fail."),
    ERR_UNSUPPORTED_FILE_EXTENSION(4002, "Content upload file. Unsupported extensions."),
    ERR_CONTENT_NOT_FOUND(4003, "Content not found."),
    ERR_CONTENT_UPDATE(4004, "Content Update fail."),
    ERR_CONTENT_SCENE_GROUP_NOT_FOUND(4005, "Content SceneGroup Info not found."),
    ERR_CONTENT_METADATA_READ(4006, "Content Metadata parse and read fail."),
    ERR_CONTENT_DELETE(4007, "Content Delete fail."),
    ERR_NOT_FOUND_CONTENT_TYPE(4008, "Not found content type."),
    ERR_CONTENT_MANAGED(4009, "Content deletion failed. Because it is managed. Delete the process created with this content and try again."),
    ERR_CONTENT_DELETE_OWNERSHIP(4010, "Content deletion failed. Because it is NOT ownership."),
    ERR_CONTENT_DELETE_SUCCEED(4011, "Content deletion succeed."),
    ERR_DELETE_CONTENT(4012, "Content File deletion failed."),
    ERR_OWNERSHIP(4013, "An error occurred in the request. Because it is NOT ownership."),
    ERROR_WORKSPACE(4014, "An error occurred in the request. Because Workspace is different."),

    // 공통 에러
    ERR_INVALID_REQUEST_PARAMETER(8001, "Invalid request parameter cause api errors");

    private int code;
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


    @Override
    public String toString() {
        return "ErrorCode{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
