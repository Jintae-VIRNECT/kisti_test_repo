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
	ERR_CONTENT_MANAGED(
		4009,
		"Content deletion failed. Because it is managed. Delete the process created with this content and try again."
	),
	ERR_CONTENT_DELETE_OWNERSHIP(4010, "Content deletion failed. Because it is NOT ownership."),
	ERR_CONTENT_DELETE_SUCCEED(4011, "Content deletion succeed."),
	ERR_MISMATCH_TARGET(4012, "Target data does not match."),
	ERR_NOT_FOUND_TARGET(4013, "Target not found."),
	ERR_DELETE_CONTENT(4014, "Content File deletion failed."),
	ERR_OWNERSHIP(4015, "An error occurred in the request. Because it is NOT ownership."),
	ERROR_WORKSPACE(4016, "An error occurred in the request. Because Workspace is different."),
	ERR_CONTENT_DOWNLOAD(4017, "Content Download fail."),
	ERR_CONTENT_UPLOAD_LICENSE(4018, "Content upload fail. Because provided capacity exceeded."),
	ERR_CONTENT_DOWNLOAD_INVALID_LICENSE(4019, "Content Download fail.  Invalid download request license info."),
	ERR_CONTENT_DELETE_SHARED(4020, "Content deletion failed. Because it is managed. Check this content is shared."),
	ERR_CONTENT_UPLOAD_LICENSE_PRODUCT_NOT_FOUND(4021, "Content upload fail. Because user haven't make product plan"),
	ERR_CONTENT_UPLOAD_LICENSE_NOT_FOUND(4022, "Content upload fail. Because workspace haven't license plan"),
	ERR_CONTENT_DOWNLOAD_INVALID_SHARED(4023, "Content upload fail. Contents is not shared."),

	// 타겟 관련
	ERR_TARGET_DATA_ALREADY_EXIST(4101, "Target insert fail. Because this target data already exist."),

	//프로젝트 관련
	ERR_PROJECT_NOT_FOUND(5000, "Project information not found."),

	//프로젝트 업로드
	ERR_PROJECT_UPLOAD(5010, "Project upload fail."),
	ERR_PROJECT_UPLOAD_MAX_STORAGE(5011, "Project upload fail. Because maximum uploadable storage is exceeded."),
	ERR_PROJECT_UPLOAD_INVALID_LICENSE(5012, "Project upload fail. Because user have no uploadable product license."),

	//프로젝트 조회
	ERR_PROJECT_ACCESS_INVALID_SHARE_PERMISSION(
		5020, "Project access fail. Because user have invalid project share permission."),

	//프로젝트 수정
	ERR_PROJECT_UPDATE(5030, "Project update fail."),
	ERR_PROJECT_UPDATE_INVALID_SHARE_PERMISSION(
		5031, "Project update fail. Because user have invalid project share permission."),
	ERR_PROJECT_UPDATE_INVALID_EDIT_PERMISSION(
		5032, "Project update fail. Because user have invalid project edit permission."),
	ERR_PROJECT_UPDATE_MAX_STORAGE(5033, "Project update fail. Because maximum uploadable storage is exceeded."),
	ERR_PROJECT_UPDATE_INVALID_LICENSE(5034, "Project update fail. Because user have no uploadable product license."),

	//프로젝트 다운로드
	ERR_PROJECT_DOWNLOAD(5040, "Project download fail."),
	ERR_PROJECT_DOWNLOAD_INVALID_SHARE_PERMISSION(
		5041, "Project download fail. Because user have invalid project share permission."),

	//프로젝트 삭제
	ERR_PROJECT_DELETE(5050, "Project delete fail."),
	ERR_PROJECT_DELETE_INVALID_SHARE_PERMISSION(5051, "Project delete fail. Because user have invalid project share permission."),
	ERR_PROJECT_DELETE_INVALID_EDIT_PERMISSION(5052, "Project delete fail. Because user have invalid project edit permission."),

	// 공통 에러
	ERR_INVALID_REQUEST_PARAMETER(8001, "Invalid request parameter cause api errors"),
	ERR_UNEXPECTED_SERVER_ERROR(9999, "Unexpected Server Error, Please contact Administrator");

	private final int code;
	private final String message;

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

	@Override
	public String toString() {
		return "ErrorCode{" +
			"code=" + code +
			", message='" + message + '\'' +
			'}';
	}
}

