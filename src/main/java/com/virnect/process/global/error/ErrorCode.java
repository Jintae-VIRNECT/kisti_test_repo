package com.virnect.process.global.error;

import io.swagger.annotations.ApiModel;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-07
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@ApiModel
public enum ErrorCode {
    // 콘텐츠 관련
    ERR_UNSUPPORTED_FILE_EXTENSION(4002, "Content upload file. Unsupported extensions."),
    ERR_CONTENT_NOT_FOUND(4003, "Content not found."),
    ERR_NO_MORE_ARUCO(4008, "No more ARUCO."),
    ERR_CONTENT_MANAGED(4009, "Content deletion failed. Because it is managed. Delete the process created with this content and try again."),
    ERR_DEALLOCATE_ARUCO(4010, "Failed deallocate ARUCO."),
    ERR_DELETE_CONTENT(4011, "Content File deletion failed."),
    ERR_FILE_UPLOAD(4012, "File upload fail."),

    // 공정 관련
    ERR_NOT_FOUND_ARUCO(5002, "Could not found ARUCO for the content."),                // make에서 로직처리 되어 있음. 컨텐츠 업데이트 관련.
    ERR_CAN_CREATE_PROCESS(5003, "The content can create process."),    // make에서 로직처리 되어 있음. 컨텐츠 업데이트가 가능한 메시지
    ERR_NOT_FOUND_PROCESS(5004, "Could not found process."),
    ERR_NOT_FOUND_PROCESS_FOR_PROCESS_METADATA(5005, "The requested process id could not be found."),
    ERR_NOT_FOUND_SUBPROCESS_FOR_PROCESS_METADATA(5006, "The requested sub-process id could not be found."),
    ERR_NOT_FOUND_PROCESS_OF_ARUCO(5007, "Could not found process of ARUCO. or This process state was CLOSED, DELETED"),
    ERR_HAS_ALREADY_PROCESS_OF_THIS_CONTENT(5008, "The content has already been created for the process. Please register new content."),        // 워크스테이션 후처리 있음.

    ERR_NOT_FOUND_SUBPROCESS(5011, "Could not found sub-process."),
    ERR_PROCESS_UPDATED(5012, "Process update Failed."),
    ERR_SUBPROCESS_UPDATED(5013, "SubProcess update Failed."),
    ERR_NOT_FOUND_SUBPROCESS_WORKER(5014, "Could not found sub-process worker."),

    ERR_NOT_FOUND_JOB(5021, "Could not found job."),

    ERR_NOT_FOUND_REPORT(5071, "Not found report."),
    ERR_NOT_FOUND_REPORT_ITEM(5072, "Not found report item."),

    ERR_NOT_FOUND_SMART_TOOL(5081, "Not found smart-tool."),
    ERR_NOT_FOUND_SMART_TOOL_ITEM(5072, "Not found smart-tool item."),

    ERR_NOT_FOUND_ISSUE(5091, "Not found issue from this issue_id."),

    ERR_PROCESS_WORK_RESULT_SYNC(5100, "Process work result synchronization failed."),

    ERR_PROCESS_REGISTER(5901, "Process Registration failed."),
    ERR_SUB_PROCESS_REGISTER(5902, "SubProcess Registration failed."),
    ERR_JOB_REGISTER(5903, "Job Registration failed."),
    ERR_REPORT_REGISTER(5904, "Report Registration failed."),
    ERR_SMART_TOOL_REGISTER(5905, "SmartTool Registration failed."),


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
