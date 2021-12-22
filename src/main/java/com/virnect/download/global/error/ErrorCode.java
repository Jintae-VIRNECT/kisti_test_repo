package com.virnect.download.global.error;

/**
 * Project: PF-Admin
 * DATE: 2020-02-20
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public enum ErrorCode {
	//Download 서비스 에러
	ERR_NOT_FOUND_FILE(1001, "Not Found File"),
	ERR_NOT_UPLOADED_FILE(1002, "Not Uploaded File"),

	// App 서비스 에러
	ERR_APP_UPLOAD_FAIL(6000, "Application file upload fail."),
	ERR_APP_UPLOAD_EMPTY_APPLICATION_FILE(6001, "Application file upload file. file size is 0"),
	ERR_APP_UPLOAD_FILE_EXTENSION_NOT_SUPPORT(6002,
		"Application file upload file. Application extension not supported."),
	ERR_APP_UPLOAD_FAIL_DEVICE_TYPE_NOT_FOUND(6003, "Application file upload fail. Device type information not found."),
	ERR_APP_UPLOAD_FAIL_DEVICE_INFO_NOT_FOUND(6004, "Application file upload fail. Device information not found."),
	ERR_APP_UPLOAD_FAIL_OS_INFO_NOT_FOUND(
		6005, "Application file upload fail. Operation System information not found."),
	ERR_APP_UPLOAD_FAIL_PRODUCT_INFO_NOT_FOUND(
		6006, "Application file upload fail. Application product information not found."),
	ERR_APP_UPLOAD_FAIL_DUPLICATE_VERSION(6007, "Application file upload fail. Application version is duplicated."),
	ERR_APP_UPLOAD_FAIL_VERSION_IS_LOWER(
		6008, "Application file upload fail. Application version is lower then latest version duplicated."),
	ERR_APP_PACKAGE_NAME_NOT_FOUND(6009, "Application information not found."),
	ERR_APP_SIGNING_KEY_REGISTER(
		6010,
		"Application signing key registration failed. Every application found by packageName has signing key information."
	),
	ERR_APP_INFO_NOT_FOUND(
		6011,
		"No such application information with app uuid."
	),
	ERR_APP_INFO_UPDATE(
		6012,
		"Application information update fail."
	),
	ERR_APP_UPLOAD_FAIL_WORKSPACE_INVALID_PERMISSION(6013,"Application file upload fail. User is haven't valid workspace permission"),

	// 공통 에러
	ERR_API_AUTHENTICATION(8003, "Authentication Error"),
	ERR_INVALID_VALUE(8004, "Invalid Value"),
	ERR_INVALID_REQUEST_PARAMETER(8001, "Invalid request parameter cause api errors"),
	ERR_RESOURCE_UPLOAD_ERROR(8005, "Resource Upload Error"),
	ERR_NOT_SUPPORTED_FILE(8006, "Invalid File"),
	ERR_UNEXPECTED_SERVER_ERROR(9999, "Unexpected Server Error, Please contact Administrator");

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
}
