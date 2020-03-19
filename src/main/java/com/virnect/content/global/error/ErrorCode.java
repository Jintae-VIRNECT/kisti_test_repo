package com.virnect.content.global.error;

import io.swagger.annotations.ApiModel;

/**
 * Project: SMIC_CUSTOM
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

    // 공정 관련
    ERR_PROCESS_REGISTER(5001, "Process Registration fail."),
    ERR_NOT_FOUND_ARUCO(5002, "Could not found ARUCO for the content."),
    ERR_NOT_BEEN_CREATED_PROCESS(5003, "The content has not been created process."),
    ERR_NOT_FOUND_PROCESS(5004, "Could not found process."),
    ERR_NOT_FOUND_PROCESS_FOR_PROCESS_METADATA(5004, "The requested process id could not be found."),
    ERR_NOT_FOUND_SUBPROCESS_FOR_PROCESS_METADATA(5004, "The requested sub-process id could not be found."),

    ERR_NOT_FOUND_SUBPROCESS(5011, "Could not found sub-process."),
    ERR_PROCESS_UPDATED(5012, "SubProcess update Failed."),
    ERR_SUBPROCESS_UPDATED(5013, "SubProcess update Failed."),
    ERR_NOT_FOUND_SUBPROCESS_WORKER(5014, "Could not found sub-process worker."),

    ERR_NOT_FOUND_JOB(5021, "Could not found job."),

    ERR_NOT_FOUND_REPORT(5071, "Not found report."),
    ERR_NOT_FOUND_REPORT_ITEM(5072, "Not found report item."),

    ERR_NOT_FOUND_SMART_TOOL(5081, "Not found smart-tool."),
    ERR_NOT_FOUND_SMART_TOOL_ITEM(5072, "Not found smart-tool item."),

    ERR_NOT_FOUND_ISSUE(5091, "Not found issue from this issue_id."),

    ERR_PROCESS_WORK_RESULT_SYNC(5100, "Process work result synchronization failed."),


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
