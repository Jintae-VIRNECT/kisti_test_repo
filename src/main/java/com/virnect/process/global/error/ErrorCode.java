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
	// 작업 관련 에러코드 - 500x
	ERR_OWNERSHIP(5001, "An error occurred in the request. Because it is NOT ownership."),
	ERR_NOT_FOUND_CONTENT(5002, "Could not found content"),
	ERR_CAN_CREATE_PROCESS(5003, "The content can create process."),    // make에서 로직처리 되어 있음. 컨텐츠 업데이트가 가능한 메시지
	ERR_NOT_FOUND_PROCESS(5004, "Could not found process."),
	ERR_NOT_FOUND_PROCESS_FOR_PROCESS_METADATA(5005, "The requested process id could not be found."),
	ERR_NOT_FOUND_SUBPROCESS_FOR_PROCESS_METADATA(5006, "The requested sub-process id could not be found."),
	ERR_NOT_FOUND_PROCESS_OF_TARGET(
		5007, "Could not found process of target. or This process state was CLOSED, DELETED"),
	ERR_HAS_ALREADY_PROCESS_OF_THIS_CONTENT(
		5008,
		"The content has already been created for the process. Please register new content."
	),        // 워크스테이션 후처리 있음.

	// 하위작업 관련 에러코드 - 501x
	ERR_NOT_FOUND_SUBPROCESS(5011, "Could not found sub-process."),
	ERR_PROCESS_UPDATED(5012, "Process update Failed."),
	ERR_SUBPROCESS_UPDATED(5013, "SubProcess update Failed."),
	ERR_NOT_FOUND_SUBPROCESS_WORKER(5014, "Could not found sub-process worker."),

	// 단계 관련 에러코드 - 502x
	ERR_NOT_FOUND_JOB(5021, "Could not found job."),

	// 페이퍼 관련 에러코드 - 507x
	ERR_NOT_FOUND_REPORT(5071, "Not found report."),
	ERR_NOT_FOUND_REPORT_ITEM(5072, "Not found report item."),

	// 이슈 관련 에러코드 - 509x
	ERR_NOT_FOUND_ISSUE(5091, "Not found issue from this issue_id."),

	// 동기화 관련 에러코드 - 510x
	ERR_PROCESS_WORK_RESULT_SYNC(5100, "Process work result synchronization failed."),
	ERR_WORKER_NOT_EQUAL_SYNC(5101, "Process work result synchronization failed. Doesn't Match syncUser."),

	// 파일처리 관련 에러코드 - 580x
	ERR_UNSUPPORTED_FILE_EXTENSION(5801, "Content upload file. Unsupported extensions."),
	ERR_FILE_UPLOAD(5802, "File upload fail."),
	ERR_FILE_DELETE(5803, "File delete fail."),
	ERR_FILE_DOWNLOAD(5804, "File download fail."),

	// 등록 관련 에러코드 - 590x
	ERR_PROCESS_REGISTER(5901, "Process Registration failed."),
	ERR_SUB_PROCESS_REGISTER(5902, "SubProcess Registration failed."),
	ERR_JOB_REGISTER(5903, "Job Registration failed."),
	ERR_REPORT_REGISTER(5904, "Report Registration failed."),
	ERR_TARGET_REGISTER(5905, "Target Registration failed."),
	ERR_DELETE_PROCESS(5906, "Process Deletion failed."),
	ERR_SUB_PROCESS_REGISTER_NO_WORKER(5907, "SubProcess Registration failed. No worker selected."),

	// 타겟 관련 에러코드 - 592x
	ERR_NO_CONTENT_TARGET(5920, "Not found Content Target. Transform failed."),
	ERR_ALREADY_TRANSFORMED(5921, "This Content is already Transformed. Transform failed"),
	ERR_NOT_FOUND_TARGET(5922, "Could not found Target Data. Check your Target Data."),
	ERR_OVER_MAX_TARGET(5923, "One Contents is mapped One Target Data."),

	// 컨텐츠 다운로드 관련 에러코드 - 600x
	ERR_CONTENT_DOWNLOAD_NOT_FOUND_INFO(6000, "Content download fail. Not found process content info."),
	ERR_CONTENT_DOWNLOAD_INVALID_STATE(
		6001, "Content download fail. Process is not in progress."),
	ERR_CONTENT_DOWNLOAD_INVALID_WORKER(
		6002, "Content download fail. Download request user is not registered process worker."),
	ERR_CONTENT_DOWNLOAD_INVALID_WORKSPACE(
		6003, "Content download fail. Invalid download request workspace info."),
	ERR_CONTENT_DOWNLOAD_INVALID_LICENSE(6004, "Content download fail. Invalid download request license info."),

	// 공통 에러 - 800x
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
